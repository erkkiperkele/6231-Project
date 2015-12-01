package shared.exception;

/**
 * @author Pascal Tozzi 27664850 Specific Exception class
 */
public class ExceptionUDPBankNotAvailableRetryLater extends Exception
{
	private static final long serialVersionUID = 2548962065703142770L;

	public ExceptionUDPBankNotAvailableRetryLater(String errorMessage)
	{
		super("Not all bank are available for credit check, retry at a later time." + System.lineSeparator() + errorMessage);
	}
}