package shared.udp.message.client;

import java.io.Serializable;

import shared.data.Loan;
import shared.udp.IOperationMessage;
import shared.udp.OperationType;

@SuppressWarnings("serial")
public class SynchronizeLoan implements Serializable, IOperationMessage 
{
	private String bank;
	private Loan loan;
	private int posLoan;
	private int amountLoans;
	private boolean isRequested;
	private int nextLoanID;
	private int nextSequenceID;
	private String machineName;

	public SynchronizeLoan() {}
	public SynchronizeLoan(String machineName, String bank, Loan loan, int posLoan,
			int amountLoans, boolean isRequested, int nextLoanID, int nextSequenceID) 
	{
		this.machineName = machineName;
		this.setBank(bank);
		this.setLoan(loan);
		this.setPosLoan(posLoan);
		this.setAmountLoans(amountLoans);
		this.setRequested(isRequested);
		this.nextLoanID = nextLoanID;
		this.nextSequenceID = nextSequenceID;
	}

	public OperationType getOperationType()
	{
		return OperationType.SynchronizeCustomer;
	}

	public String getBank() {
		return bank;
	}

	@Override
	public String getMachineName() {
		return this.machineName;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}
	public Loan getLoan() {
		return loan;
	}
	public void setLoan(Loan loan) {
		this.loan = loan;
	}
	public int getPosLoan() {
		return posLoan;
	}
	public void setPosLoan(int posLoan) {
		this.posLoan = posLoan;
	}
	public int getAmountLoans() {
		return amountLoans;
	}
	public void setAmountLoans(int amountLoans) {
		this.amountLoans = amountLoans;
	}
	public boolean isRequested() {
		return isRequested;
	}
	public void setRequested(boolean isRequested) {
		this.isRequested = isRequested;
	}
	public int getNextLoanID() {
		return nextLoanID;
	}
	public void setNextLoanID(int nextLoanID) {
		this.nextLoanID = nextLoanID;
	}
	public int getNextSequenceID() {
		return nextSequenceID;
	}
	public void setNextSequenceID(int nextSequenceID) {
		this.nextSequenceID = nextSequenceID;
	}
}
