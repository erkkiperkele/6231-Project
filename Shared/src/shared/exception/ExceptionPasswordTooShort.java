package shared.exception;

/**
 * @author Pascal Tozzi 27664850 Specific Exception class
 */
public class ExceptionPasswordTooShort extends Exception
{
	private static final long serialVersionUID = 5121009025845409006L;

	public ExceptionPasswordTooShort()
	{
		super("Password too short!");
	}
}
