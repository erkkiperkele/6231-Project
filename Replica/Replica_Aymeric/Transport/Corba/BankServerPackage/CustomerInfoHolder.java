package Transport.Corba.BankServerPackage;

/**
 * Holder class for : CustomerInfo
 * 
 * @author OpenORB Compiler
 */
final public class CustomerInfoHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal CustomerInfo value
     */
    public Transport.Corba.BankServerPackage.CustomerInfo value;

    /**
     * Default constructor
     */
    public CustomerInfoHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public CustomerInfoHolder(Transport.Corba.BankServerPackage.CustomerInfo initial)
    {
        value = initial;
    }

    /**
     * Read CustomerInfo from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = CustomerInfoHelper.read(istream);
    }

    /**
     * Write CustomerInfo into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        CustomerInfoHelper.write(ostream,value);
    }

    /**
     * Return the CustomerInfo TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return CustomerInfoHelper.type();
    }

}
