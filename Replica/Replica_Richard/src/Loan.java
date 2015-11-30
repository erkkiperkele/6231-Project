import java.io.Serializable;

public class Loan implements Serializable {
	
	private static int AccountAccumulator = 1000;
	
	private String LoanID;
	private String CustomerAccountNumber;
	private double Amount;
	private String DueDate;
	
	public Loan(String CustomerAccountNumber, double Amount, String DueDate) {
		this.LoanID = String.format("%c%d", CustomerAccountNumber.charAt(0), ++AccountAccumulator);
		this.CustomerAccountNumber = CustomerAccountNumber;
		this.Amount = Amount;
		this.DueDate = DueDate;
	}
	
	public String getID() {
		return LoanID;
	}
	
	public String getAccountID() {
		return CustomerAccountNumber;
	}
	
	public void setDueDate(String DueDate) {
		this.DueDate = DueDate;
	}
	
	public String getDueDate() {
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
