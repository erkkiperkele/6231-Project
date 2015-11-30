package shared.udp.message.bank;

import java.io.Serializable;
import java.util.Date;

import shared.udp.IOperationMessage;
import shared.udp.OperationType;

@SuppressWarnings("serial")
public class TransferringLoanMessage implements Serializable, IOperationMessage 
{
	private String bank;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String phoneNumber;
	private String password;
	private int accountID;
	private int loanID;
	private long loanAmount;
	private Date loanDueDate;
	
	private int resultLoanID;
	private Exception exception;

	public TransferringLoanMessage() {}	
	public TransferringLoanMessage(String bank, String firstName, 
			String lastName, String emailAddress, String phoneNumber, String password,
			int accountID, int loanID, long loanAmount, Date loanDueDate)
	{
		this.setBank(bank);
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setEmailAddress(emailAddress);
		this.setPhoneNumber(phoneNumber);
		this.setPassword(password);
		this.setAccountID(accountID);
		this.setLoanID(loanID);
		this.setLoanAmount(loanAmount);
		this.setLoanDueDate(loanDueDate);
	}

	public OperationType getOperationType()
	{
		return OperationType.GetLoanAmount;
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
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	public int getAccountID() {
		return accountID;
	}
	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}
	public long getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(long loanAmount) {
		this.loanAmount = loanAmount;
	}
	public int getResultLoanID() {
		return resultLoanID;
	}
	public void setResultLoanID(int resultLoanID) {
		this.resultLoanID = resultLoanID;
	}
	public int getLoanID() {
		return loanID;
	}
	public void setLoanID(int loanID) {
		this.loanID = loanID;
	}
	public Date getLoanDueDate() {
		return loanDueDate;
	}
	public void setLoanDueDate(Date loanDueDate) {
		this.loanDueDate = loanDueDate;
	}
}
