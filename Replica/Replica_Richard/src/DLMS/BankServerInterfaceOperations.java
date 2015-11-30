package DLMS;

/**
 * Interface definition: BankServerInterface.
 * 
 * @author OpenORB Compiler
 */
public interface BankServerInterfaceOperations
{
    /**
     * Operation openAccount
     */
    public String openAccount(String bank, String FirstName, String LastName, String EmailAddress, String PhoneNumber, String Password);

    /**
     * Operation getLoan
     */
    public String getLoan(String Bank, String AccountNumber, String Password, double Amount);

    /**
     * Operation delayPayment
     */
    public String delayPayment(String Bank, String LoanID, String CurrentDueDate, String NewDueDate);

    /**
     * Operation printCustomerInfo
     */
    public String printCustomerInfo(String Bank);

    /**
     * Operation transferLoan
     */
    public String transferLoan(String LoanID, String CurrentBank, String OtherBank);

}
