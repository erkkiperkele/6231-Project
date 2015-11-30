package shared.udp.message.bank;

import java.io.Serializable;

import shared.udp.IOperationMessage;
import shared.udp.OperationType;

@SuppressWarnings("serial")
public class GetLoanAmountMessage implements Serializable, IOperationMessage 
{
	private String bank;
	private String username;
	private long loanAmount;
	private Exception exception;

	public GetLoanAmountMessage() {}	
	public GetLoanAmountMessage(String bank, String username)
	{
		this.bank = bank;
		this.username = username;
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
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public long getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(long loanAmount) {
		this.loanAmount = loanAmount;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
}
