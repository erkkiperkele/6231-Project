package Transport.Corba.BankServerPackage;

/**
 * Exception definition: RecordNotFoundException.
 * 
 * @author OpenORB Compiler
 */
public final class RecordNotFoundException extends org.omg.CORBA.UserException
{
    /**
     * Exception member message
     */
    public String message;

    /**
     * Default constructor
     */
    public RecordNotFoundException()
    {
        super(RecordNotFoundExceptionHelper.id());
    }

    /**
     * Constructor with fields initialization
     * @param message message exception member
     */
    public RecordNotFoundException(String message)
    {
        super(RecordNotFoundExceptionHelper.id());
        this.message = message;
    }

    /**
     * Full constructor with fields initialization
     * @param message message exception member
     */
    public RecordNotFoundException(String orb_reason, String message)
    {
        super(RecordNotFoundExceptionHelper.id() +" " +  orb_reason);
        this.message = message;
    }

}
