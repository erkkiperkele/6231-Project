package dlms.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;

import dlms.util.Env;

/**
 * @author Pascal Tozzi 27664850 Loan class used by the client and server and
 *         even serialized as a return parameter within the Bank class
 */
public class Loan implements java.io.Serializable, Cloneable, KeyUserNameInterface
{
	private static final long serialVersionUID = 162314104715560157L;
	private int loanID;
	private String accountNumber;
	private double loanAmount;
	private Date loanDueDate;

	public Loan()
	{
		loanID = -1;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 6);
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try
		{
			loanDueDate = formatter.parse(formatter.format(cal.getTime()));
		}
		catch (ParseException e)
		{
			Env.log(Level.SEVERE, "Date Parse Exception: " + e.getMessage(), true);
			loanDueDate = cal.getTime();
		}
	}

	public Loan(String accountNumber)
	{
		this();
		this.accountNumber = accountNumber;
	}

	/**
	 * @return the loanID
	 */
	public int getLoanID()
	{
		return loanID;
	}

	/**
	 * @param loanID
	 *            the loanID to set
	 */
	public void setLoanID(int loanID)
	{
		this.loanID = loanID;
	}

	/**
	 * @return the accountNumber
	 */
	public String getAccountNumber()
	{
		return accountNumber;
	}

	/**
	 * @param accountNumber
	 *            the accountNumber to set
	 */
	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	/**
	 * @return the loanAmount
	 */
	public double getLoanAmount()
	{
		return loanAmount;
	}

	/**
	 * @param loanAmount
	 *            the loanAmount to set
	 */
	public void setLoanAmount(double loanAmount)
	{
		this.loanAmount = loanAmount;
	}

	/**
	 * @return the loanDueDate
	 */
	public Date getLoanDueDate()
	{
		return loanDueDate;
	}

	/**
	 * @param loanDueDate
	 *            the loanDueDate to set
	 */
	public void setLoanDueDate(Date loanDueDate)
	{
		this.loanDueDate = loanDueDate;
	}

	@Override
	public String getUserName()
	{
		return accountNumber;
	}

	@Override
	public String toString()
	{
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		return "LoanID:" + this.getLoanID() + " DueDate:" + df.format(this.getLoanDueDate()) + " Amount:" + this.getLoanAmount();
	}
}
