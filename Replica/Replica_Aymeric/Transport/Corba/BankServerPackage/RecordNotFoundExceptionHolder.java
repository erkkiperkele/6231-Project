package Transport.Corba.BankServerPackage;

/**
 * Holder class for : RecordNotFoundException
 * 
 * @author OpenORB Compiler
 */
final public class RecordNotFoundExceptionHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal RecordNotFoundException value
     */
    public Transport.Corba.BankServerPackage.RecordNotFoundException value;

    /**
     * Default constructor
     */
    public RecordNotFoundExceptionHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public RecordNotFoundExceptionHolder(Transport.Corba.BankServerPackage.RecordNotFoundException initial)
    {
        value = initial;
    }

    /**
     * Read RecordNotFoundException from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = RecordNotFoundExceptionHelper.read(istream);
    }

    /**
     * Write RecordNotFoundException into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        RecordNotFoundExceptionHelper.write(ostream,value);
    }

    /**
     * Return the RecordNotFoundException TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return RecordNotFoundExceptionHelper.type();
    }

}
