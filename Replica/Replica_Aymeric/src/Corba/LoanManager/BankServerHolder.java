package Corba.LoanManager;

/**
 * Holder class for : StartBankServer
 * 
 * @author OpenORB Compiler
 */
final public class BankServerHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal StartBankServer value
     */
    public BankServer value;

    /**
     * Default constructor
     */
    public BankServerHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public BankServerHolder(BankServer initial)
    {
        value = initial;
    }

    /**
     * Read StartBankServer from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = BankServerHelper.read(istream);
    }

    /**
     * Write StartBankServer into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        BankServerHelper.write(ostream,value);
    }

    /**
     * Return the StartBankServer TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return BankServerHelper.type();
    }

}
