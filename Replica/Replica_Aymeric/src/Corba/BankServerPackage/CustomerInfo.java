package Corba.BankServerPackage;

/**
 * Struct definition: CustomerInfo.
 * 
 * @author OpenORB Compiler
*/
public final class CustomerInfo implements org.omg.CORBA.portable.IDLEntity
{
    /**
     * Struct member userName
     */
    public String userName;

    /**
     * Struct member account
     */
    public Account account;

    /**
     * Struct member loans
     */
    public Loan[] loans;

    /**
     * Default constructor
     */
    public CustomerInfo()
    { }

    /**
     * Constructor with fields initialization
     * @param userName userName struct member
     * @param account account struct member
     * @param loans loans struct member
     */
    public CustomerInfo(String userName, Account account, Loan[] loans)
    {
        this.userName = userName;
        this.account = account;
        this.loans = loans;
    }

}
