package Transport.Corba.BankServerPackage;

/**
 * Holder class for : BankInfo
 * 
 * @author OpenORB Compiler
 */
final public class BankInfoHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal BankInfo value
     */
    public Transport.Corba.BankServerPackage.BankInfo value;

    /**
     * Default constructor
     */
    public BankInfoHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public BankInfoHolder(Transport.Corba.BankServerPackage.BankInfo initial)
    {
        value = initial;
    }

    /**
     * Read BankInfo from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = BankInfoHelper.read(istream);
    }

    /**
     * Write BankInfo into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        BankInfoHelper.write(ostream,value);
    }

    /**
     * Return the BankInfo TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return BankInfoHelper.type();
    }

}
