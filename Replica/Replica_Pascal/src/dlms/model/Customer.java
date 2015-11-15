package dlms.model;

/**
 * @author Pascal Tozzi 27664850 Customer class used by the client and server
 *         and even serialized as a return parameter within the Bank class
 */
public class Customer implements java.io.Serializable, Cloneable, KeyUserNameInterface
{
	private static final long serialVersionUID = -278167316058180827L;

	private String accountNumber;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String phoneNumber;
	private String password;
	private double creditLimit;

	/**
	 * @return the accountNumber
	 */
	public String getAccountNumber()
	{
		return accountNumber;
	}

	/**
	 * @param accountNumber
	 *            the accountNumber to set
	 */
	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress()
	{
		return emailAddress;
	}

	/**
	 * @param emailAddress
	 *            the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	/**
	 * @param phoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * @return the creditLimit
	 */
	public double getCreditLimit()
	{
		return creditLimit;
	}

	/**
	 * @param creditLimit
	 *            the creditLimit to set
	 */
	public void setCreditLimit(double creditLimit)
	{
		this.creditLimit = creditLimit;
	}

	public String getUserName()
	{
		return this.getLastName() + "," + this.getFirstName();
	}

	@Override
	public boolean equals(Object o)
	{
		boolean isEqual = false;
		if (o instanceof Customer)
		{
			isEqual = this.getUserName().equalsIgnoreCase(((Customer) o).getUserName());
		}
		return isEqual;
	}

	@Override
	public int hashCode()
	{
		return getUserName().toLowerCase().hashCode();
	}
}
