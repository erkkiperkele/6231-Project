package Transport.Corba.BankServerPackage;

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
    public Transport.Corba.BankServerPackage.Account account;

    /**
     * Struct member loans
     */
    public Transport.Corba.BankServerPackage.Loan[] loans;

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
    public CustomerInfo(String userName, Transport.Corba.BankServerPackage.Account account, Transport.Corba.BankServerPackage.Loan[] loans)
    {
        this.userName = userName;
        this.account = account;
        this.loans = loans;
    }

}
