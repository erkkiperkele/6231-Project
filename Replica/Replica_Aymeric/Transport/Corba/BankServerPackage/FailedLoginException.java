package Transport.Corba.BankServerPackage;

/**
 * Exception definition: FailedLoginException.
 * 
 * @author OpenORB Compiler
 */
public final class FailedLoginException extends org.omg.CORBA.UserException
{
    /**
     * Exception member message
     */
    public String message;

    /**
     * Default constructor
     */
    public FailedLoginException()
    {
        super(FailedLoginExceptionHelper.id());
    }

    /**
     * Constructor with fields initialization
     * @param message message exception member
     */
    public FailedLoginException(String message)
    {
        super(FailedLoginExceptionHelper.id());
        this.message = message;
    }

    /**
     * Full constructor with fields initialization
     * @param message message exception member
     */
    public FailedLoginException(String orb_reason, String message)
    {
        super(FailedLoginExceptionHelper.id() +" " +  orb_reason);
        this.message = message;
    }

}
