package dlms.exception;

/**
 * @author Pascal Tozzi 27664850 Specific Exception class
 */
public class ExceptionCustomerAlreadyExist extends Exception
{
	private static final long serialVersionUID = -3170882347299343389L;
	private String accountID;

	/**
	 * @return the accountID
	 */
	public String getAccountID()
	{
		return accountID;
	}

	public ExceptionCustomerAlreadyExist(String accountID)
	{
		super("Customer already exist under the account name: " + accountID);
		this.accountID = accountID;
	}

}
