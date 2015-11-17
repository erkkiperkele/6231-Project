package Transport.Corba.BankServerPackage;

/**
 * Struct definition: Customer.
 * 
 * @author OpenORB Compiler
*/
public final class Customer implements org.omg.CORBA.portable.IDLEntity
{
    /**
     * Struct member id
     */
    public short id;

    /**
     * Struct member firstName
     */
    public String firstName;

    /**
     * Struct member lastName
     */
    public String lastName;

    /**
     * Struct member email
     */
    public String email;

    /**
     * Struct member bank
     */
    public Transport.Corba.BankServerPackage.Bank bank;

    /**
     * Struct member accountNumber
     */
    public short accountNumber;

    /**
     * Struct member phone
     */
    public String phone;

    /**
     * Struct member password
     */
    public String password;

    /**
     * Default constructor
     */
    public Customer()
    { }

    /**
     * Constructor with fields initialization
     * @param id id struct member
     * @param firstName firstName struct member
     * @param lastName lastName struct member
     * @param email email struct member
     * @param bank bank struct member
     * @param accountNumber accountNumber struct member
     * @param phone phone struct member
     * @param password password struct member
     */
    public Customer(short id, String firstName, String lastName, String email, Transport.Corba.BankServerPackage.Bank bank, short accountNumber, String phone, String password)
    {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.phone = phone;
        this.password = password;
    }

}
