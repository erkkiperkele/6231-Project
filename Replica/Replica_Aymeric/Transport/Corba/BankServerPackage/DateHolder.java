package Transport.Corba.BankServerPackage;

/**
 * Holder class for : Date
 * 
 * @author OpenORB Compiler
 */
final public class DateHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal Date value
     */
    public Transport.Corba.BankServerPackage.Date value;

    /**
     * Default constructor
     */
    public DateHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public DateHolder(Transport.Corba.BankServerPackage.Date initial)
    {
        value = initial;
    }

    /**
     * Read Date from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = DateHelper.read(istream);
    }

    /**
     * Write Date into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        DateHelper.write(ostream,value);
    }

    /**
     * Return the Date TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return DateHelper.type();
    }

}
