package Transport.Corba.BankServerPackage;

/**
 * Holder class for : Customer
 * 
 * @author OpenORB Compiler
 */
final public class CustomerHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal Customer value
     */
    public Transport.Corba.BankServerPackage.Customer value;

    /**
     * Default constructor
     */
    public CustomerHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public CustomerHolder(Transport.Corba.BankServerPackage.Customer initial)
    {
        value = initial;
    }

    /**
     * Read Customer from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = CustomerHelper.read(istream);
    }

    /**
     * Write Customer into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        CustomerHelper.write(ostream,value);
    }

    /**
     * Return the Customer TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return CustomerHelper.type();
    }

}
