package shared.udp.message.client;

import java.io.Serializable;
import java.util.Date;

import shared.udp.IOperationMessage;
import shared.udp.OperationType;

@SuppressWarnings("serial")
public class DelayPaymentMessage implements Serializable, IOperationMessage 
{
	private String bank;
	private int loanID;
	private Date currentDueDate;
	private Date newDueDate;
	private boolean isDelaySuccessful;
	private Exception exception;

	public DelayPaymentMessage() {}	
	public DelayPaymentMessage(String bank, int loanID, Date currentDueDate, Date newDueDate)
	{
		this.bank = bank;
		this.loanID = loanID;
		this.currentDueDate = currentDueDate;
		this.newDueDate = newDueDate;
	}

	public OperationType getOperationType()
	{
		return OperationType.DelayPayment;
	}
	
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public int getLoanID() {
		return loanID;
	}
	public void setLoanID(int loanID) {
		this.loanID = loanID;
	}
	public Date getCurrentDueDate() {
		return currentDueDate;
	}
	public void setCurrentDueDate(Date currentDueDate) {
		this.currentDueDate = currentDueDate;
	}
	public Date getNewDueDate() {
		return newDueDate;
	}
	public void setNewDueDate(Date newDueDate) {
		this.newDueDate = newDueDate;
	}
	public boolean isDelaySuccessful() {
		return isDelaySuccessful;
	}
	public void setDelaySuccessful(boolean isDelaySuccessful) {
		this.isDelaySuccessful = isDelaySuccessful;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
}
