package impl;
import java.io.Serializable;

public class CustomerAccount implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7902021447980385222L;

	private static int AccountAccumulator;
	
	private int AccountID;
	private String FirstName;
	private String LastName;
	private String Email;
	private String PhoneNumber;
	private String Password;
	private double CreditLimit = 1000;
	
	public CustomerAccount(String firstName, String lastName, String email, String phoneNumber,
			String password) {
		AccountID = ++AccountAccumulator;
		FirstName = firstName;
		LastName = lastName;
		Email = email;
		PhoneNumber = phoneNumber;
		Password = password;
	}
	
	public String toString() {
		return "=== Account ID: " + AccountID + " ===\n"
			 + "Name: " + FirstName + " " + LastName + "\n"
			 + "Phone: " + PhoneNumber + "\n"
			 + "Email: " + Email + "\n"
			 + String.format("Credit limit: %.2f%n", CreditLimit);
	}

	public int getID() {
		return AccountID;
	}
	
	public double getCreditLimit() {
		return CreditLimit;
	}
	
	public String getPassword() {
		return Password;
	}
	
	public String getFirstName() {
		return FirstName;
	}
	
	public String getLastName() {
		return LastName;
	}

	public String getEmail() {
		return Email;
	}

	public String getPhoneNumber() {
		return PhoneNumber;
	}
}
