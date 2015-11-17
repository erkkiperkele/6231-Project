package Transport.Corba.BankServerPackage;

/**
 * Exception definition: TransferException.
 * 
 * @author OpenORB Compiler
 */
public final class TransferException extends org.omg.CORBA.UserException
{
    /**
     * Exception member message
     */
    public String message;

    /**
     * Default constructor
     */
    public TransferException()
    {
        super(TransferExceptionHelper.id());
    }

    /**
     * Constructor with fields initialization
     * @param message message exception member
     */
    public TransferException(String message)
    {
        super(TransferExceptionHelper.id());
        this.message = message;
    }

    /**
     * Full constructor with fields initialization
     * @param message message exception member
     */
    public TransferException(String orb_reason, String message)
    {
        super(TransferExceptionHelper.id() +" " +  orb_reason);
        this.message = message;
    }

}
