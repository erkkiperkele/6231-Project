package Transport.Corba.BankServerPackage;

/**
 * Holder class for : FailedLoginException
 * 
 * @author OpenORB Compiler
 */
final public class FailedLoginExceptionHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal FailedLoginException value
     */
    public Transport.Corba.BankServerPackage.FailedLoginException value;

    /**
     * Default constructor
     */
    public FailedLoginExceptionHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public FailedLoginExceptionHolder(Transport.Corba.BankServerPackage.FailedLoginException initial)
    {
        value = initial;
    }

    /**
     * Read FailedLoginException from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = FailedLoginExceptionHelper.read(istream);
    }

    /**
     * Write FailedLoginException into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        FailedLoginExceptionHelper.write(ostream,value);
    }

    /**
     * Return the FailedLoginException TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return FailedLoginExceptionHelper.type();
    }

}
