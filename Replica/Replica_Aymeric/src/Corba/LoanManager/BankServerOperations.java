package Corba.LoanManager;

import Corba.BankServerPackage.*;

/**
 * Interface definition: StartBankServer.
 * 
 * @author OpenORB Compiler
 */
public interface BankServerOperations
{
    /**
     * Operation openAccount
     */
    public short openAccount(Bank bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password);

    /**
     * Operation getCustomer
     */
    public Customer getCustomer(Bank bank, String email, String password)
        throws FailedLoginException;

    /**
     * Operation signIn
     */
    public Customer signIn(Bank bank, String email, String password)
        throws FailedLoginException;

    /**
     * Operation getLoan
     */
    public Loan getLoan(Bank bankId, short accountNumber, String password, int loanAmount)
        throws FailedLoginException;

    /**
     * Operation delayPayment
     */
    public void delayPayment(Bank bank, short loanID, Date currentDueDate, Date newDueDate)
        throws RecordNotFoundException;

    /**
     * Operation getCustomersInfo
     */
    public BankInfo getCustomersInfo(Bank bank)
        throws FailedLoginException;

    /**
     * Operation transferLoan
     */
    public Loan transferLoan(short loanId, Bank currentBank, Bank otherBank)
        throws TransferException;

}
