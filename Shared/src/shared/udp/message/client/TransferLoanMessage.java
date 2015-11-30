package shared.udp.message.client;

import java.io.Serializable;

import shared.udp.IOperationMessage;
import shared.udp.OperationType;

@SuppressWarnings("serial")
public class TransferLoanMessage implements Serializable, IOperationMessage 
{
	private int loanID;
	private String currentBank;
	private String otherBank;
	private boolean isTransferSuccessful;
	private Exception exception;
	
	public TransferLoanMessage() {}
	public TransferLoanMessage(int loanID, String currentBank, String otherBank)
	{
		this.loanID = loanID;
		this.currentBank = currentBank;
		this.otherBank = otherBank;
	}
	
	public OperationType getOperationType()
	{
		return OperationType.TransferLoan;
	}
	
	public int getLoanID() {
		return loanID;
	}
	public void setLoanID(int loanID) {
		this.loanID = loanID;
	}
	public String getCurrentBank() {
		return currentBank;
	}
	public void setCurrentBank(String currentBank) {
		this.currentBank = currentBank;
	}
	public String getOtherBank() {
		return otherBank;
	}
	public void setOtherBank(String otherBank) {
		this.otherBank = otherBank;
	}
	public boolean isTransferSuccessful() {
		return isTransferSuccessful;
	}
	public void setTransferSuccessful(boolean isTransferSuccessful) {
		this.isTransferSuccessful = isTransferSuccessful;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
}
