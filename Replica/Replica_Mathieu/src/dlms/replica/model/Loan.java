package dlms.replica.model;

import java.io.Serializable;
import java.util.Date;

/**
 * The loan data object
 * 
 * @author mat
 *
 */
public class Loan implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int accountNbr;
	private String emailAddress;
	private long amount;
	private Date dueDate;
	private int id;
	
	/**
	 * Constructor
	 * 
	 * @param accountNbr the account number of the load owner
	 * @param amount The amount of the load in dollars
	 * @param dueDate The due date of the loan
	 * @param id the id of this loan
	 */
	public Loan(int accountNbr, String emailAddress, long amount, Date dueDate, int id) {
		super();
		this.accountNbr = accountNbr;
		this.emailAddress = emailAddress;
		this.amount = amount;
		this.dueDate = dueDate;
		this.id = id;
	}

	public int getAccountNbr() {
		return accountNbr;
	}

	public long getAmount() {
		return amount;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public int getId() {
		return id;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getEmailAddress() {
		return emailAddress;
	}
	
	public String toString() {
		return "id: " + this.id + ", accountNbr: "+ this.accountNbr + ", emailAddress: " + 
				this.emailAddress + ", amount: " + this.amount + ", dueDate: " + this.dueDate; 
	}
}
