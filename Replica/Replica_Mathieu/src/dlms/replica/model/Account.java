package dlms.replica.model;

import java.io.Serializable;

/**
 * The Account class stores account related data
 * 
 * @author mat
 *
 */
public class Account implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected int accountNbr;
	protected String firstName;
	protected String lastName;
	protected String emailAddress;
	protected String phoneNbr;
	protected String password;
	protected int creditLimit;
	
	public Account(int accountNbr, String firstName, String lastName, String emailAddress, String phoneNbr,  String password) {
		super();
		this.accountNbr = accountNbr;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.phoneNbr = phoneNbr;
		this.password = password;
		this.creditLimit = 1000;
	}

	/**
	 *  Returns the first letter of the username, uppercased
	 *  
	 * @return
	 */
	public String getLetterKey() {
		return this.emailAddress.substring(0, 1).toUpperCase();
	}
	
	public int getAccountNbr() {
		return accountNbr;
	}

	public void setAccountNbr(int accountNbr) {
		this.accountNbr = accountNbr;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPhoneNbr() {
		return phoneNbr;
	}

	public void setPhoneNbr(String phoneNbr) {
		this.phoneNbr = phoneNbr;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(int creditLimit) {
		this.creditLimit = creditLimit;
	}
	
	public String toString() {
		return "accountNbr: " + this.accountNbr + ", firstName: " + this.firstName + ", lastName: " + this.lastName + ", emailAddress: " + this.emailAddress + 
		", phoneNbr: " + this.phoneNbr + ", password: " + this.password + ", creditLimit: " + this.creditLimit;
	}
}
