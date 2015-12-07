package shared.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Customer implements Serializable {

    private String phone;
    private int id;

    private String firstName;
    private String lastName;

    private int accountNumber;
    private String password;
    private String email;
    private long creditLimit;
    

    public Customer() {}

    public Customer(int id, int accountNumber, String firstName, String lastName, String password, String email, String phone) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

    public Customer(String firstName, String lastName, String password, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }
    
    public String getUserName() {
    	return email.toLowerCase();
    }

    public String toString() {
        String displayInfo = "";

        displayInfo += (this.id + " - ");
        displayInfo += (this.accountNumber + " - ");
        displayInfo += (this.firstName + " - ");
        displayInfo += (this.lastName + " - ");
        displayInfo += (this.email);

        return displayInfo;
    }
    
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(int accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public long getCreditLimit() {
		return creditLimit;
	}
	public void setCreditLimit(long creditLimit) {
		this.creditLimit = creditLimit;
	}

}
