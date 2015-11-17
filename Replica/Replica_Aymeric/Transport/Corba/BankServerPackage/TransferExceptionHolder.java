package Transport.Corba.BankServerPackage;

/**
 * Holder class for : TransferException
 * 
 * @author OpenORB Compiler
 */
final public class TransferExceptionHolder
        implements org.omg.CORBA.portable.Streamable
{
    /**
     * Internal TransferException value
     */
    public Transport.Corba.BankServerPackage.TransferException value;

    /**
     * Default constructor
     */
    public TransferExceptionHolder()
    { }

    /**
     * Constructor with value initialisation
     * @param initial the initial value
     */
    public TransferExceptionHolder(Transport.Corba.BankServerPackage.TransferException initial)
    {
        value = initial;
    }

    /**
     * Read TransferException from a marshalled stream
     * @param istream the input stream
     */
    public void _read(org.omg.CORBA.portable.InputStream istream)
    {
        value = TransferExceptionHelper.read(istream);
    }

    /**
     * Write TransferException into a marshalled stream
     * @param ostream the output stream
     */
    public void _write(org.omg.CORBA.portable.OutputStream ostream)
    {
        TransferExceptionHelper.write(ostream,value);
    }

    /**
     * Return the TransferException TypeCode
     * @return a TypeCode
     */
    public org.omg.CORBA.TypeCode _type()
    {
        return TransferExceptionHelper.type();
    }

}
