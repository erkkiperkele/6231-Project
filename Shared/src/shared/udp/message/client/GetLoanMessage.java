package shared.udp.message.client;

import java.io.Serializable;

import shared.udp.IOperationMessage;
import shared.udp.OperationType;

@SuppressWarnings("serial")
public class GetLoanMessage implements Serializable, IOperationMessage 
{
	private String bank;
	private int accountNumber;
	private String password;
	private long loanAmount;
	private int resultLoanID;
	private Exception exception;
	private String machineName;

	public GetLoanMessage() {}	
	public GetLoanMessage(String bank, int accountNumber, String password, long loanAmount)
	{
		this.bank = bank;
		this.accountNumber = accountNumber;
		this.password = password;
		this.loanAmount = loanAmount;
	}

	public OperationType getOperationType()
	{
		return OperationType.GetLoan;
	}
	
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	public int getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(int accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	@Override
	public String getMachineName() {
		return machineName;
	}
}
