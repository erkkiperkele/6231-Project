package shared.data;

import java.util.List;

public class BankState 
{
	private List<Loan> loanList;
	private List<Customer> customerList;
	private int nextSequenceNumber;
	private int nextCustomerID;
	private int nextLoanID;
	
	public BankState(List<Loan> loanList, List<Customer> customerList, 
			int nextSequenceNumber, int nextCustomerID, int nextLoanID)
	{
		this.loanList = loanList;
		this.customerList = customerList;
		this.nextSequenceNumber = nextSequenceNumber;
		this.nextCustomerID = nextCustomerID;
		this.nextLoanID = nextLoanID;
	}

	//TODO: To Delete once I know what the replica should set for nextSequenceNumber, customerId and loanId
	public BankState(List<Loan> loanList, List<Customer> customerList) {
		this.loanList = loanList;
		this.customerList = customerList;
	}

	public List<Loan> getLoanList() {
		return loanList;
	}

	public List<Customer> getCustomerList() {
		return customerList;
	}

	public int getNextSequenceNumber() {
		return nextSequenceNumber;
	}

	public int getNextCustomerID() {
		return nextCustomerID;
	}

	public int getNextLoanID() {
		return nextLoanID;
	}
}
