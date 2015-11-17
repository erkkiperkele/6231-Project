package Data;

import Exceptions.RecordNotFoundException;
import Services.LockFactory;
import Services.SessionService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A thread safe repository serving as an in memory database
 * For accounts and loans.
 * Note: currently some operations cost more processing because some of the interface contracts do not
 * provide a userName when the data structure is optimised for a userName index.
 */
public class DataRepository {


    private Hashtable<String, List<Account>> accounts;
    private Hashtable<String, List<Loan>> loans;

    private int accountNumber = 1;
    private int loanNumber = 1;
    private int customerNumber = 1;


    public DataRepository() {

        this.accounts = new Hashtable<>();
        this.loans = new Hashtable<>();

        generateInitialData();
    }

    /**
     * retrieves a Customer's information for a given userName.
     *
     * @param userName
     * @return
     */
    public Customer getCustomer(String userName) {
        Account customerAccount = getAccount(userName);

        return customerAccount == null
                ? null
                : customerAccount.getOwner();
    }

    /**
     * retrives a Customer's account information for a given account number.
     *
     * @param accountNumber
     * @return
     */
    public Account getAccount(int accountNumber) {
        return this.accounts.values()
                .stream()
                .flatMap(a -> a.stream())
                .filter(a -> a.getAccountNumber() == accountNumber)
                .findFirst()
                .orElse(null);
    }

    /**
     * retrieve a Customer's account information for a given userName.
     *
     * @param userName
     * @return
     */
    public Account getAccount(String userName) {
        String index = getIndex(userName);
        Account foundAccount = null;

        try{

        LockFactory.getInstance().readlock(userName);

        foundAccount = getAccountsAtIndex(index)
                .stream()
                .filter(a -> a.getOwner().getUserName().equalsIgnoreCase(userName))
                .findFirst()
                .orElse(null);

        } finally {
            LockFactory.getInstance().readUnlock(userName);
        }

        return foundAccount;
    }

    /**
     * retrieves a customer's account information for a given first name and last name.
     *
     * @param firstName
     * @param lastName
     * @return
     */
    public Account getAccount(String firstName, String lastName) {
        return this.accounts.values()
                .stream()
                .flatMap(x -> x.stream())
                .filter(a -> a.getOwner().getFirstName().equalsIgnoreCase(firstName))
                .filter(a -> a.getOwner().getLastName().equalsIgnoreCase(lastName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Creates a new account for the given user with a given credit limit.
     *
     * @param owner
     * @param creditLimit
     */
    public void createAccount(Customer owner, long creditLimit) {

        if (getAccount(owner.getUserName()) != null) {
            SessionService.getInstance().log().info(
                    String.format("This Account already exists for %s", owner.getFirstName())
            );
            return;
        }

        setOwnerInfo(owner);    //PATCH: bad pattern.

        Account newAccount = new Account(generateNewAccountNumber(), owner, creditLimit);
        owner.setAccountNumber(newAccount.getAccountNumber());

        createAccountThreadSafe(owner, newAccount);

        SessionService.getInstance().log().info(
                String.format("new account created for %s (account #%d)",
                        owner.getFirstName(),
                        newAccount.getAccountNumber()
                ));
    }

    /**
     * retrieves all the loans attached to a given account Number at the current bank.
     *
     * @param accountNumber
     * @return
     */
    public List<Loan> getLoans(int accountNumber) {

        Customer customer = getCustomer(accountNumber);
        String userName = customer.getUserName();

        String index = getIndex(customer.getUserName().toLowerCase());
        List<Loan> loans = null;

        try {
            LockFactory.getInstance().readlock(userName);

            loans = getLoansAtIndex(index)
                    .stream()
                    .filter(l -> l.getCustomerAccountNumber() == customer.getAccountNumber())
                    .collect(Collectors.toList());
        } finally {
            LockFactory.getInstance().readUnlock(userName);
        }

        return loans;
    }

    /**
     * retrieves a loan by its loan id
     * @param loanId
     * @return
     */
    public Loan getLoan(int loanId)
    {
        Loan loan = this.loans.values()
                .stream()
                .flatMap(l -> l.stream())
                .filter(l -> l.getLoanNumber() == loanId)
                .findFirst()
                .orElse(null);
        return loan;
    }

    /**
     * Creates a new loan at the current bank.
     * No validation is performed at this layer.
     *
     * @param userName
     * @param amount
     * @param dueDate
     * @return
     */
    public Loan createLoan(String userName, long amount, Date dueDate) {

        String index = getIndex(userName);
        int customerAccountNumber = getAccount(userName).getAccountNumber();

        Loan newLoan = new Loan(generateNewLoanNumber(), customerAccountNumber, amount, dueDate);

        try {
            LockFactory.getInstance().writeLock(userName);
            getLoansAtIndex(index).add(newLoan);
        } finally {
            LockFactory.getInstance().writeUnlock(userName);
        }
        return newLoan;
    }

    /**
     * Modifies a given loan's due date.
     * No validation is performed at this layer
     *
     * @param loanId
     * @param newDueDate
     * @throws RecordNotFoundException
     */
    public void updateLoan(int loanId, Date newDueDate) throws RecordNotFoundException {
        Customer customer = getCustomerByLoanId(loanId);

        if (customer != null) {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            updateLoanThreadSafe(customer, loanId, newDueDate);

            SessionService.getInstance().log().info(
                    String.format("Due date for loan #%d changed to %s",
                            loanId,
                            dateFormat.format(newDueDate))
            );
        } else {
            String message = String.format("No record found for loand #%d", loanId);
            SessionService.getInstance().log().info(message);
            throw new RecordNotFoundException(message);
        }
    }

    /**
     * Retrieves all of the customer's information at this bank.
     *
     * @return
     */
    public CustomerInfo[] getCustomersInfo() {
        CustomerInfo[] customersInfo = this.accounts.values()
                .stream()
                .flatMap(a -> a.stream())
                .map(a -> new CustomerInfo(a, getLoans(a.getAccountNumber())))
                .toArray(size -> new CustomerInfo[size]);
        return customersInfo;
    }

    /**
     * Deletes a given loan
     * @param username
     * @param loanNumber
     * @return
     */
    public boolean deleteLoan(String username, int loanNumber) {

        String index = getIndex(username);
        List<Loan> loans = getLoansAtIndex(index);
        return loans.removeIf(l -> l.getLoanNumber() == loanNumber);
    }

    private Customer getCustomerByLoanId(int loanId) {
        Customer customer = null;

        int accountNumber = this.loans.values()
                .stream()
                .flatMap(l -> l.stream())
                .filter(l -> l.getLoanNumber() == loanId)
                .map(l -> l.getCustomerAccountNumber())
                .findFirst()
                .orElse(-1);

        if (accountNumber > -1) {
            customer = getCustomer(accountNumber);
        }
        return customer;
    }

    private Customer getCustomer(int accountNumber) {
        return this.accounts.values()
                .stream()
                .flatMap(accounts -> accounts
                        .stream()
                        .filter(x -> x.getAccountNumber() == accountNumber)
                        .map(a -> a.getOwner()))
                .findFirst()
                .orElse(null);
    }

    private void updateLoanThreadSafe(Customer customer, int loanNumber, Date newDueDate) {
        String index = getIndex(customer.getUserName());
        String userName = customer.getUserName();

        try {

            LockFactory.getInstance().writeLock(userName);

            getLoansAtIndex(index)
                    .stream()
                    .filter(l -> l.getLoanNumber() == loanNumber)
                    .findFirst()
                    .orElse(null)
                    .setDueDate(newDueDate);
        } finally {
            LockFactory.getInstance().writeUnlock(userName);
        }
    }

    private void createAccountThreadSafe(Customer owner, Account newAccount) {
        String index = getIndex(owner.getUserName());
        try {
            List<Account> accounts = getAccountsAtIndex(index);
            LockFactory.getInstance().writeLock(owner.getUserName());

//            //UNCOMMENT FOR CONCURRENT CREATION TESTING
//            testConcurrentAccess(owner, "Concurrent1");

            accounts.add(newAccount);

            SessionService.getInstance().log().info(
                    String.format("Thread #%d CREATED account for %s", Thread.currentThread().getId(), owner.getFirstName())
            );
        } finally {
            LockFactory.getInstance().writeUnlock(owner.getUserName());
        }
    }

    private String getIndex(String userName) {
        return userName.toLowerCase().substring(0, 1);
    }

    private List<Loan> getLoansAtIndex(String index) {
        if (this.loans.get(index) == null) {
            this.loans.put(index, new ArrayList<>());
        }

        return this.loans.get(index);
    }

    private List<Account> getAccountsAtIndex(String index) {
        if (this.accounts.get(index) == null) {
            this.accounts.put(index, new ArrayList<>());
        }

        return this.accounts.get(index);
    }

    private void setOwnerInfo(Customer owner) {
        if (owner.getId() == 0) {
            owner.setId(++this.customerNumber);
        }
        owner.setBank(SessionService.getInstance().getBank());
    }

    private int generateNewAccountNumber() {
        return ++this.accountNumber;
    }

    private int generateNewLoanNumber() {
        return ++this.loanNumber;
    }

    /**
     * Some dummy data initialization
     */
    private void generateInitialData() {

        Bank bank = SessionService.getInstance().getBank();
        long defaultCreditLimit = 1500;

        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.MONTH, 1);

        Date dueDate = calendar.getTime();

        Customer alex = new Customer(0, 0, "Alex", "Emond", "aa", bank, "alex.emond@gmail.com", "514.111.2222");
        Customer justin = new Customer(0, 0, "Justin", "Paquette", "aa", bank, "justin.paquette@gmail.com", "514.111.2222");
        Customer maria = new Customer(0, 0, "Maria", "Etinger", "aa", bank, "maria.etinger@gmail.com", "514.111.2222");

        //Those 3 have an account on each bank
        createAccount(alex, defaultCreditLimit);
        createAccount(justin, defaultCreditLimit);
        createAccount(maria, defaultCreditLimit);

        if (bank == Bank.Royal) {
            Customer sylvain = new Customer(0, 0, "Sylvain", "Poudrier", "aa", bank, "sylvain.poudrier@gmail.com", "514.111.2222");
            createAccount(sylvain, defaultCreditLimit);

            createLoan(alex.getUserName(), 200, dueDate);
        }

        if (bank == Bank.National) {
            Customer pascal = new Customer(0, 0, "Pascal", "Groulx", "aa", bank, "pascal.groulx@gmail.com", "514.111.2222");
            createAccount(pascal, defaultCreditLimit);

            createLoan(alex.getUserName(), 300, dueDate);
        }

        if (bank == Bank.Dominion) {
            Customer max = new Customer(0, 0, "Max", "Tanquerel", "aa", bank, "max.tanquerel@gmail.com", "514.111.2222");
            createAccount(max, defaultCreditLimit);

            createLoan(justin.getUserName(), 1000, dueDate);
            createLoan(maria.getUserName(), 500, dueDate);
        }
    }

    /**
     * Use this method to test concurrent access
     */
    private void testConcurrentAccess(Customer owner, String ownerNameToProtect) {

        try {
            if (owner.getFirstName().equalsIgnoreCase(ownerNameToProtect)
                    ) {
                System.err.println(
                        String.format(
                                "THREAD #%d : NO OTHER THREAD CAN CREATE ACCOUNT WHILE I SLEEP FOR %s"
                                , Thread.currentThread().getId(), owner.getFirstName()
                        ));
                Thread.currentThread().sleep(3000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
