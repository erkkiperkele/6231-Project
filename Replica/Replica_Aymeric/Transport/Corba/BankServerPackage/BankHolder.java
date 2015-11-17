package Transport.Corba.BankServerPackage;

/**
 * Holder class for : Bank
 * 
 * @author OpenORB Compiler
 */
final public class BankHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal Bank value
     */
    public Transport.Corba.BankServerPackage.Bank value;

    /**
     * Default constructor
     */
    public BankHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public BankHolder(Transport.Corba.BankServerPackage.Bank initial)
    {
        value = initial;
    }

    /**
     * Read Bank from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = BankHelper.read(istream);
    }

    /**
     * Write Bank into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        BankHelper.write(ostream,value);
    }

    /**
     * Return the Bank TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return BankHelper.type();
    }

}
