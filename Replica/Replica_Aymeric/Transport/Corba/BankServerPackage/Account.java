package Transport.Corba.BankServerPackage;

/**
 * Struct definition: Account.
 * 
 * @author OpenORB Compiler
*/
public final class Account implements org.omg.CORBA.portable.IDLEntity
{
    /**
     * Struct member accountNumber
     */
    public short accountNumber;

    /**
     * Struct member owner
     */
    public Customer owner;

    /**
     * Struct member creditLimit
     */
    public int creditLimit;

    /**
     * Default constructor
     */
    public Account()
    { }

    /**
     * Constructor with fields initialization
     * @param accountNumber accountNumber struct member
     * @param owner owner struct member
     * @param creditLimit creditLimit struct member
     */
    public Account(short accountNumber, Transport.Corba.BankServerPackage.Customer owner, int creditLimit)
    {
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.creditLimit = creditLimit;
    }

}
