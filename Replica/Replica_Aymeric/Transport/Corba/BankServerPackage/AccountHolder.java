package Transport.Corba.BankServerPackage;

/**
 * Holder class for : Account
 * 
 * @author OpenORB Compiler
 */
final public class AccountHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal Account value
     */
    public Transport.Corba.BankServerPackage.Account value;

    /**
     * Default constructor
     */
    public AccountHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public AccountHolder(Transport.Corba.BankServerPackage.Account initial)
    {
        value = initial;
    }

    /**
     * Read Account from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = AccountHelper.read(istream);
    }

    /**
     * Write Account into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        AccountHelper.write(ostream,value);
    }

    /**
     * Return the Account TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return AccountHelper.type();
    }

}
