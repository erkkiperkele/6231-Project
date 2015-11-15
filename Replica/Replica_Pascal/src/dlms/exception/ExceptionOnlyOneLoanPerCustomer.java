package dlms.exception;

/**
 * @author Pascal Tozzi 27664850 Specific Exception class
 */
public class ExceptionOnlyOneLoanPerCustomer extends Exception
{
	private static final long serialVersionUID = 819655563551868245L;

	public ExceptionOnlyOneLoanPerCustomer()
	{
		super("ExceptionOnlyOneLoanPerCustomer");
	}
}
