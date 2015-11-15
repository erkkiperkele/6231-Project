package dlms.exception;

/**
 * @author Pascal Tozzi 27664850 Specific Exception class
 */
public class ExceptionInvalidLoanID extends Exception
{
	private static final long serialVersionUID = -7529089541877985527L;

	public ExceptionInvalidLoanID()
	{
		super("Invalid loanID!");
	}

}
