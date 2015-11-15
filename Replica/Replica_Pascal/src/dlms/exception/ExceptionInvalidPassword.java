package dlms.exception;

/**
 * @author Pascal Tozzi 27664850 Specific Exception class
 */
public class ExceptionInvalidPassword extends Exception
{
	private static final long serialVersionUID = 5121009025845409006L;

	public ExceptionInvalidPassword()
	{
		super("Invalid Password!");
	}
}
