package dlms.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Pascal Tozzi 27664850 Bank class used by the client and server and
 *         even serialized as a return parameter for the printCustomerInfo
 */
public class Bank implements java.io.Serializable, Cloneable
{
	/**
	 * Generated serial version
	 */
	private static final long serialVersionUID = 296844460390158187L;

	protected String name;
	protected HashMap<String, Customer> accounts = new HashMap<String, Customer>();
	protected ArrayList<Loan> loans = new ArrayList<Loan>();

	public Bank()
	{
	}

	public Bank(String name)
	{
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @return the accounts
	 */
	public HashMap<String, Customer> getAccounts()
	{
		return accounts;
	}

	/**
	 * @return the loans
	 */
	public ArrayList<Loan> getLoans()
	{
		return loans;
	}

	@Override
	public String toString()
	{
		String message = "[" + getName() + "]";

		for (Loan loan : this.getLoans())
		{
			Customer customer = this.getAccounts().get(loan.getAccountNumber());
			message += System.lineSeparator() + customer.getFirstName() + " " + customer.getLastName() + ": " + loan.toString();
		}

		return message;
	}
}
