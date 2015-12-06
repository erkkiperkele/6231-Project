package shared.udp.message.client;

import java.io.Serializable;

import shared.udp.IOperationMessage;
import shared.udp.OperationType;

@SuppressWarnings("serial")
public class OpenAccountMessage implements Serializable, IOperationMessage 
{
	private String bank;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String phoneNumber;
	private String password;
	private int resultAccountID;
	private Exception exception;
	private String machineName;

	public OpenAccountMessage() {}
	public OpenAccountMessage(String bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password)
	{
		this.setBank(bank);
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setEmailAddress(emailAddress);
		this.setPhoneNumber(phoneNumber);
		this.setPassword(password);
	}

	public OperationType getOperationType()
	{
		return OperationType.OpenAccount;
	}
	
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
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
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getResultAccountID() {
		return resultAccountID;
	}
	public void setResultAccountID(int resultAccountID) {
		this.resultAccountID = resultAccountID;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	@Override
	public String getMachineName() {
		return machineName;
	}
}
