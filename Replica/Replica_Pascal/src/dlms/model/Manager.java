package dlms.model;

/**
 * @author Pascal Tozzi 27664850 Manager class isn't used for now, might be
 *         useful later on
 */
public class Manager
{
	private String username;
	private String password;
	private String educationalInsitution;

	/**
	 * @return the username
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username)
	{
		this.username = username;
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
	 * @return the educationalInsitution
	 */
	public String getEducationalInsitution()
	{
		return educationalInsitution;
	}

	/**
	 * @param educationalInsitution
	 *            the educationalInsitution to set
	 */
	public void setEducationalInsitution(String educationalInsitution)
	{
		this.educationalInsitution = educationalInsitution;
	}

}
