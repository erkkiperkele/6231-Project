package DLMS;

/**
 * Holder class for : BankServerInterface
 * 
 * @author OpenORB Compiler
 */
final public class BankServerInterfaceHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal BankServerInterface value
     */
    public DLMS.BankServerInterface value;

    /**
     * Default constructor
     */
    public BankServerInterfaceHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public BankServerInterfaceHolder(DLMS.BankServerInterface initial)
    {
        value = initial;
    }

    /**
     * Read BankServerInterface from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = BankServerInterfaceHelper.read(istream);
    }

    /**
     * Write BankServerInterface into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        BankServerInterfaceHelper.write(ostream,value);
    }

    /**
     * Return the BankServerInterface TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return BankServerInterfaceHelper.type();
    }

}
