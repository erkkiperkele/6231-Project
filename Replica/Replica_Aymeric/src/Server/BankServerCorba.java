package Server;


import Contracts.IBankService;
import Corba.BankServerPackage.Bank;
import Corba.BankServerPackage.Customer;
import Corba.BankServerPackage.Loan;
import Corba.BankServerPackage.TransferException;
import Corba.Helpers.ObjectMapper;
import Corba.LoanManager.BankServerPOA;
import Corba.BankServerPackage.*;
import Corba.BankServerPackage.RecordNotFoundException;

import javax.security.auth.login.FailedLoginException;

public class BankServerCorba extends BankServerPOA {

    private IBankService bankService;
    private int serverPort;

    public BankServerCorba(IBankService bankService, int serverPort) {
        this.bankService = bankService;
        this.serverPort = serverPort;
    }

    @Override
    public short openAccount(Bank bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password) {
        System.out.println("openning account!!!!");

        shared.data.Bank serverBank = ObjectMapper.toBank(bank);
        int accountNumber = bankService.openAccount(serverBank, firstName, lastName, emailAddress, phoneNumber, password);
        return (short) accountNumber;
    }

    @Override
    public Customer getCustomer(Bank bank, String email, String password)
            throws Corba.BankServerPackage.FailedLoginException {

        System.out.println("get customer!!!!");

        shared.data.Customer serverCustomer = null;
        try {
            serverCustomer = bankService.getCustomer(email, password);
        } catch (FailedLoginException e) {
            throw new Corba.BankServerPackage.FailedLoginException(e.getMessage());
        }

        return ObjectMapper.toCorbaCustomer(serverCustomer);
    }

    @Override
    public Customer signIn(Bank bank, String email, String password)
            throws Corba.BankServerPackage.FailedLoginException {

        return getCustomer(bank, email, password);
    }

    @Override
    public Loan getLoan(Bank bank, short accountNumber, String password, int loanAmount)
            throws Corba.BankServerPackage.FailedLoginException {

        shared.data.Bank serverBank = ObjectMapper.toBank(bank);
        shared.data.Loan serverLoan = null;
        try {
            serverLoan = bankService.getLoan(serverBank, accountNumber, password, loanAmount);
        } catch (FailedLoginException e) {
            throw new Corba.BankServerPackage.FailedLoginException(e.getMessage());
        }
        return ObjectMapper.toCorbaLoan(serverLoan);
    }

    @Override
    public void delayPayment(Bank bank, short loanID, Date currentDueDate, Date newDueDate)
            throws RecordNotFoundException {

        shared.data.Bank serverBank = ObjectMapper.toBank(bank);
        java.util.Date serverCurrentDueDate = ObjectMapper.toDate(currentDueDate);
        java.util.Date serverNewDueDate = ObjectMapper.toDate(newDueDate);
        try {
            bankService.delayPayment(serverBank, (int) loanID, serverCurrentDueDate, serverNewDueDate);
        } catch (Exceptions.RecordNotFoundException e) {
            throw new RecordNotFoundException(e.getMessage());
        }
    }

    @Override
    public BankInfo getCustomersInfo(Bank bank)
            throws Corba.BankServerPackage.FailedLoginException {

        shared.data.Bank serverBank = ObjectMapper.toBank(bank);
        shared.data.CustomerInfo[] customersInfo;
        try {
            customersInfo = bankService.getCustomersInfo(serverBank);
            return ObjectMapper.toCorbaBankInfo(customersInfo);
        } catch (FailedLoginException e) {
            throw new Corba.BankServerPackage.FailedLoginException(e.getMessage());
        }
    }

    @Override
    public Loan transferLoan(short loanId, Bank currentBank, Bank otherBank) throws TransferException {

        shared.data.Bank serverCurrentBank = ObjectMapper.toBank(currentBank);
        shared.data.Bank serverOtherBank = ObjectMapper.toBank(otherBank);

        shared.data.Loan newLoan;

        try {
            newLoan = bankService.transferLoan(loanId, serverCurrentBank, serverOtherBank);
            return ObjectMapper.toCorbaLoan(newLoan);
        } catch (Exceptions.TransferException e) {
            throw new TransferException(e.getMessage());
        }
    }
}
