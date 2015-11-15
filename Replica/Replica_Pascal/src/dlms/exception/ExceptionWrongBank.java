package dlms.exception;

/**
 * @author Pascal Tozzi 27664850 Specific Exception class
 */
public class ExceptionWrongBank extends Exception
{
	private static final long serialVersionUID = -5129905571141701766L;

	public ExceptionWrongBank(String message)
	{
		super("Wrong bank server." + System.lineSeparator() + message);
	}
}
