package Transport.Corba.BankServerPackage;

/**
 * Holder class for : Loan
 * 
 * @author OpenORB Compiler
 */
final public class LoanHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal Loan value
     */
    public Transport.Corba.BankServerPackage.Loan value;

    /**
     * Default constructor
     */
    public LoanHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public LoanHolder(Transport.Corba.BankServerPackage.Loan initial)
    {
        value = initial;
    }

    /**
     * Read Loan from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = LoanHelper.read(istream);
    }

    /**
     * Write Loan into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        LoanHelper.write(ostream,value);
    }

    /**
     * Return the Loan TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return LoanHelper.type();
    }

}
