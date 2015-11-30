import java.io.Serializable;

public class CustomerAccount implements Serializable {
	
	private static int AccountAccumulator = 1000;
	
	private String AccountID;
	private String FirstName;
	private String LastName;
	private String Email;
	private String PhoneNumber;
	private String Password;
	private double CreditLimit;
	
	public CustomerAccount(String firstName, String lastName, String email, String phoneNumber,
			String password) {
		char fn = Character.toUpperCase(firstName.charAt(0));
		AccountID = String.format("%c%d", fn, ++AccountAccumulator);
		FirstName = firstName;
		LastName = lastName;
		Email = email;
		PhoneNumber = phoneNumber;
		Password = password;
		CreditLimit = 10000;
	}
	
	public String toString() {
		return "=== Account ID: " + AccountID + " ===\n"
			 + "Name: " + FirstName + " " + LastName + "\n"
			 + "Phone: " + PhoneNumber + "\n"
			 + "Email: " + Email + "\n"
			 + String.format("Credit limit: %.2f%n", CreditLimit);
	}

	public String getID() {
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
