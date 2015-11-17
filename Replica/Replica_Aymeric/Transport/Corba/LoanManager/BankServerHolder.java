package Transport.Corba.LoanManager;

/**
 * Holder class for : BankServer
 * 
 * @author OpenORB Compiler
 */
final public class BankServerHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal BankServer value
     */
    public Transport.Corba.LoanManager.BankServer value;

    /**
     * Default constructor
     */
    public BankServerHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public BankServerHolder(Transport.Corba.LoanManager.BankServer initial)
    {
        value = initial;
    }

    /**
     * Read BankServer from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = BankServerHelper.read(istream);
    }

    /**
     * Write BankServer into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        BankServerHelper.write(ostream,value);
    }

    /**
     * Return the BankServer TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return BankServerHelper.type();
    }

}
