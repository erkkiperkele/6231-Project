package dlms.exception;

/**
 * @author Pascal Tozzi 27664850 Specific Exception class
 */
public class ExceptionInvalidDueDate extends Exception
{
	private static final long serialVersionUID = 5754912921019687437L;

	public ExceptionInvalidDueDate()
	{
		super("Invalid Loan Current End Date!");
	}

}
