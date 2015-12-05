package impl;
import java.io.Serializable;
import java.util.Date;

public class Loan implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -45963620214868611L;

	private static int AccountAccumulator;
	
	private int LoanID;
	private int CustomerAccountNumber;
	private double Amount;
	private Date DueDate;
	
	public Loan(int CustomerAccountNumber, double Amount, Date DueDate) {
		this.LoanID = ++AccountAccumulator;
		this.CustomerAccountNumber = CustomerAccountNumber;
		this.Amount = Amount;
		this.DueDate = DueDate;
	}
	
	public int getID() {
		return LoanID;
	}
	
	public int getAccountID() {
		return CustomerAccountNumber;
	}
	
	public void setDueDate(Date DueDate) {
		this.DueDate = DueDate;
	}
	
	public Date getDueDate() {
		return DueDate;
	}
	
	public double getAmount() {
		return this.Amount;
	}
	
	public String toString() {
		return "=== Loan ID: " + LoanID + " ===\n"
			 + "Customer Account #: " + CustomerAccountNumber + "\n"
			 + String.format("Amount: %.2f%n", Amount)
			 + "Due: " + DueDate + "\n";
	}
}
