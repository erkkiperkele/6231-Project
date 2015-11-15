package dlms.exception;

/**
 * @author Pascal Tozzi 27664850 Specific Exception class
 */
public class ExceptionServerCommitFailed extends Exception
{
	private static final long serialVersionUID = -409601660883446123L;

	public ExceptionServerCommitFailed(String errorMsg)
	{
		super("Commit failed on server." + System.lineSeparator() + errorMsg);
	}
}
