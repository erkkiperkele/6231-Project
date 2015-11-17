package Services;

import Contracts.IBankService;
import Data.*;
import Exceptions.RecordNotFoundException;
import Exceptions.TransferException;
import UDP.*;

import javax.security.auth.login.FailedLoginException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * The bank service provides an implementation for both the customer and manager services.
 * (See interface documentation for details on the functionality provided by both those services)
 */
public class BankService implements IBankService {

    private DataRepository repository;
    private UDPClient udp;
    private final long DEFAULT_CREDIT_LIMIT = 1500;

    public BankService() {
        this.repository = new DataRepository();
        this.udp = new UDPClient();
    }

    @Override
    public int openAccount(Bank bank, String firstName, String lastName, String email, String phone, String password) {


        Customer newCustomer = new Customer(firstName, lastName, password, email, phone);

        this.repository.createAccount(newCustomer, DEFAULT_CREDIT_LIMIT);

        return this.repository.getAccount(newCustomer.getUserName()).getAccountNumber();
        //log inside the repository to manage errors.
    }

    @Override
    public Customer getCustomer(String email, String password) throws FailedLoginException {

        Customer foundCustomer = this.repository.getCustomer(email);


        if (foundCustomer == null) {
            throw new FailedLoginException(String.format("Customer doesn't exist for email: %s", email));
        }
        if (!foundCustomer.getPassword().equals(password)) {
            throw new FailedLoginException(String.format("Wrong password for email %s", email));
        }

        return foundCustomer;
    }

    @Override
    public List<Loan> getLoans(int accountNumber) {

        List<Loan> loans = this.repository.getLoans(accountNumber);

        if (loans.size() < 0) {
            SessionService.getInstance().log().info(
                    String.format("%1$d loans have been retrieved for account %2$d",
                            loans.size(),
                            accountNumber)
            );
        }

        return loans;
    }

    @Override
    public Loan getLoan(Bank bank, int accountNumber, String password, long loanAmount) throws FailedLoginException {

        Account account = this.repository.getAccount(accountNumber);
        Customer customer = account.getOwner();

        long internalLoansAmount = getInternalLoansAmount(accountNumber, password, customer);

        long externalLoansAmount = getExternalLoans(customer.getFirstName(), customer.getLastName());

        long currentCredit = externalLoansAmount + internalLoansAmount + loanAmount;

        Loan newLoan = createLoan(loanAmount, account, customer, currentCredit);

        return newLoan;
    }

    @Override
    public Account getAccount(String firstName, String lastName) {
        return this.repository.getAccount(firstName, lastName);
    }

    @Override
    public void delayPayment(Bank bank, int loanId, Date currentDueDate, Date newDueDate)
            throws RecordNotFoundException {

        //Note: in the context of this assignment, the current due date is not verified.
        this.repository.updateLoan(loanId, newDueDate);
        //Log inside the repository.
    }

    @Override
    public CustomerInfo[] getCustomersInfo(Bank bank) throws FailedLoginException {
        if (bank == SessionService.getInstance().getBank()) {
            CustomerInfo[] customersInfo = this.repository.getCustomersInfo();

            SessionService.getInstance().log().info(
                    String.format("Server returned %d customers info", customersInfo.length)
            );
            return customersInfo;
        } else {
            SessionService.getInstance().log().info(
                    String.format("Wrong bank: Info requested for bank: %1$s at bank server: %2$s",
                            bank,
                            SessionService.getInstance().getBank()
                    ));
            throw new FailedLoginException(
                    String.format("Wrong bank: Info requested for bank: %1$s at bank server: %2$s",
                            bank,
                            SessionService.getInstance().getBank()
                    ));
        }
    }

    /**
     * Note that the core of this operation is atomic, so that both UDP operations and data access operations
     * are protected against concurrency errors while transferring data from one server to another.
     *
     * @param loanId of the loan at the current bank
     * @param currentBank the current customer's bank where the loan is from
     * @param otherBank the new bank the loan is transferred to
     * @return
     * @throws TransferException
     */
    @Override
    public Loan transferLoan(int loanId, Bank currentBank, Bank otherBank) throws TransferException {

        Loan loan = findLoan(loanId);
        Customer customer = findCustomer(loan);

        Loan externalLoan;
        try{

            //WARNING: Needs 2 locks!
            //Need a large lock to avoid concurrent UDP access (message mixed up)
            //Need the ususal smaller lock to protect data being read/write at given index.
            //Mind the lock order when locking/unlocking this section.
            LockFactory.getInstance().writeLock("");
            LockFactory.getInstance().writeLock(customer.getUserName());

            Account externalAccount = findExternalAccount(otherBank, customer);
            externalLoan = tryTransferLoan(loanId, otherBank, loan, externalAccount);
            tryDeleteLoan(loanId, loan, customer);

            SessionService.getInstance().log().info(
                    String.format("%1$s transferred loan #%2$d to Bank: %3$s",
                            customer.getFirstName(),
                            loanId,
                            otherBank.name())
            );
        }
        finally{
            LockFactory.getInstance().writeUnlock(customer.getUserName());
            LockFactory.getInstance().writeUnlock("");
        }
        return externalLoan;
    }

    /**
     * In case of a transfer (via UDP), credit line is not verified at other banks since loan already exists.
     * Should Not Be Accessible via API! UDP privileges only.
     * Need a better control of elevated operations
     * @param bank
     * @param accountNumber
     * @param password
     * @param loanAmount
     * @return
     */
    public Loan getLoanWithNoCreditLineCheck(Bank bank, int accountNumber, String password, long loanAmount)
            throws FailedLoginException {
        Account account = this.repository.getAccount(accountNumber);
        Customer customer = account.getOwner();

        verifyPassword(accountNumber, password, customer);
        Loan newLoan = createLoan(loanAmount, account, customer, 0);
        return newLoan;
    }

    private void tryDeleteLoan(int loanId, Loan loan, Customer customer) throws TransferException {
        boolean isTransferSuccessful = this.repository.deleteLoan(customer.getUserName(), loan.getLoanNumber());
        if (!isTransferSuccessful)
        {
            String message = String.format(
                    "Could not delete loan #%1$d AFTER transfer.",
                    loanId);

            SessionService.getInstance().log().error(message);
            throw new TransferException(message);
        }
    }

    private Loan tryTransferLoan(int loanId, Bank otherBank, Loan loan, Account externalAccount) throws TransferException {

        Loan externalLoan = createLoanAtBank(otherBank, externalAccount, loan);
        if (externalLoan == null)
        {
            String message = String.format(
                    "Could not create loan at bank %1$s, for current loan #%2$d.",
                    otherBank.name(),
                    loanId);

            SessionService.getInstance().log().error(message);
            throw new TransferException(message);
        }
        return externalLoan;
    }

    private Customer findCustomer(Loan loan) {
        return this.repository.getAccount(loan.getCustomerAccountNumber()).getOwner();
    }

    private Account findExternalAccount(Bank otherBank, Customer customer) throws TransferException {
        Account externalAccount = getExternalAccount(otherBank, customer);

        if(externalAccount == null)
        {
            String message = String.format(
                    "Could not either get or create an acount at bank %1$s, for user %2$s.",
                    otherBank.name(),
                    customer.getUserName());

            SessionService.getInstance().log().error(message);
            throw new TransferException(message);
        }
        return externalAccount;
    }

    private Loan findLoan(int loanId) throws TransferException {
        Loan loan = this.repository.getLoan(loanId);
        if (loan == null)
        {
            String message = String.format("Loan #%1$d doesn't exist.", loanId);
            SessionService.getInstance().log().warn(message);
            throw new TransferException(message);
        }
        return loan;
    }

    private long getExternalLoans(String firstName, String lastName) {
        Bank currentBank = SessionService.getInstance().getBank();

        long externalLoans = 0;

        if (currentBank != Bank.National) {
            long externalLoan = getLoanAtBank(Bank.National, firstName, lastName);
            externalLoans += externalLoan;
        }
        if (currentBank != Bank.Royal) {
            long externalLoan = getLoanAtBank(Bank.Royal, firstName, lastName);
            externalLoans += externalLoan;
        }
        if (currentBank != Bank.Dominion) {
            long externalLoan = getLoanAtBank(Bank.Dominion, firstName, lastName);
            externalLoans += externalLoan;
        }
        return externalLoans;
    }

    private long getLoanAtBank(Bank bank, String firstName, String lastName) {

        long externalLoan = 0;
        try {

            IOperationMessage getLoanMessage = new GetLoanMessage(firstName, lastName);
            byte[] data = createUDPMessageData(getLoanMessage, OperationType.GetLoan);
            byte[] udpAnswer = this.udp.sendMessage(data, ServerPorts.getUDPPort(bank));

            Serializer getLoanSerializer = new Serializer<Long>();
            externalLoan = (Long) getLoanSerializer.deserialize(udpAnswer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return externalLoan;
    }

    private Account getExternalAccount(Bank otherBank, Customer customer) {

        Account account = getAccountAtBank(otherBank, customer);

        if (account == null)
        {
            account = createAccountAtBank(otherBank, customer);
        }
        return account;
    }

    private long getInternalLoansAmount(int accountNumber, String password, Customer customer) throws FailedLoginException {
        verifyPassword(accountNumber, password, customer);

        return this.repository.getLoans(accountNumber)
                .stream()
                .mapToLong(l -> l.getAmount())
                .sum();
    }

    private void verifyPassword(int accountNumber, String password, Customer customer) throws FailedLoginException {
        if (!customer.getPassword().equals(password)) {

            SessionService.getInstance().log().warn(
                    String.format("%s failed to log in when trying to get a new loan",
                            customer.getFirstName())
            );
            throw new FailedLoginException(String.format("Wrong password for account %d", accountNumber));
        }
    }

    private Loan createLoan(long loanAmount, Account account, Customer customer, long currentCredit) {

        Loan newLoan = null;
        String userName = account.getOwner().getUserName();

        if (currentCredit < account.getCreditLimit()) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(calendar.MONTH, 6);
            Date dueDate = calendar.getTime();

            newLoan = this.repository.createLoan(userName, loanAmount, dueDate);
            SessionService.getInstance().log().info(
                    String.format("new Loan accepted for %s (%d$), current credit: %d$",
                            customer.getFirstName(),
                            newLoan.getAmount(),
                            loanAmount + currentCredit)
            );
        } else {
            SessionService.getInstance().log().info(
                    String.format("new Loan refuse for %s (%d$), current credit: %d$",
                            customer.getFirstName(),
                            loanAmount,
                            currentCredit)
            );
        }
        return newLoan;
    }

    private Account getAccountAtBank(Bank bank, Customer customer) {

        Account externalAccount = null;
        try {
            IOperationMessage getAccountMessage = new GetAccountMessage(customer.getFirstName(), customer.getLastName());
            byte[] data = createUDPMessageData(getAccountMessage, OperationType.GetAccount);
            byte[] udpAnswer = this.udp.sendMessage(data, ServerPorts.getUDPPort(bank));

            Serializer getAccountSerializer = new Serializer<Account>();
            externalAccount = (Account) getAccountSerializer.deserialize(udpAnswer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return externalAccount;
    }

    private Account createAccountAtBank(Bank bank, Customer customer) {

        Account externalAccount = null;
        try {
            IOperationMessage createUDPMessageData = new CreateAccountMessage(customer);
            byte[] data = createUDPMessageData(createUDPMessageData, OperationType.CreateAccount);
            byte[] udpAnswer = this.udp.sendMessage(data, ServerPorts.getUDPPort(bank));

            Serializer getAccountSerializer = new Serializer<Account>();
            externalAccount = (Account) getAccountSerializer.deserialize(udpAnswer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return externalAccount;
    }

    private Loan createLoanAtBank(Bank bank, Account externalAccount, Loan loan) {

        Loan externalLoan = null;
        try {
            IOperationMessage createUDPMessageData = new CreateLoanMessage(externalAccount, loan);
            byte[] data = createUDPMessageData(createUDPMessageData, OperationType.CreateLoan);
            byte[] udpAnswer = this.udp.sendMessage(data, ServerPorts.getUDPPort(bank));

            Serializer getLoanSerializer = new Serializer<Loan>();
            externalLoan = (Loan) getLoanSerializer.deserialize(udpAnswer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return externalLoan;
    }

    private byte[] createUDPMessageData(IOperationMessage operationMessage, OperationType type) throws IOException {
        Serializer udpMessageSerializer = new Serializer<UDPMessage>();
        UDPMessage message = new UDPMessage(operationMessage, type);

        return udpMessageSerializer.serialize(message);
    }
}
