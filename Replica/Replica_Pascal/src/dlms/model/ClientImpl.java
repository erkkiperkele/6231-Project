package dlms.model;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import shared.exception.ExceptionWrongBank;
import dlms.util.Env;

/**
 * @author Pascal Tozzi 27664850 CustomerClient and ManagerClient Implementation
 */
public class ClientImpl
{
	private ServerBank bank;

	public ClientImpl(ServerBank bank) throws RemoteException
	{
		super();
		this.bank = bank;
	}

	public int transferLoan(int loanID, String currentBank, String otherBank)
	{
		int result;
		try
		{
			if (this.bank.getServerName().equalsIgnoreCase(currentBank) == false)
				throw new ExceptionWrongBank("Wrong server " + currentBank + " != " + this.bank.getServerName());

			if (this.bank.getServerName().equalsIgnoreCase(otherBank) == true)
				throw new ExceptionWrongBank("Destination bank is the same as current bank, not allowed");

			ServerInfo sv = Env.GetServerInfo(otherBank);
			if (sv == null)
				throw new ExceptionWrongBank("Wrong server " + otherBank + " doesn't exists.");

			System.out.println(" - transferLoan(" + loanID + "," + currentBank + "," + otherBank + ")");
			if (this.bank.transferLoan(loanID, sv.getServerName()))
			{
				result = 0;
			}
			else
			{
				result = -1;
			}
		}
		catch (Exception e)
		{
			result = -1;
		}
		return result;
	}

	public int delayPayment(String bank, int loanID, String currentDueDate, String newDueDate)
	{
		int result;
		try
		{
			if (this.bank.getServerName().equalsIgnoreCase(bank) == false)
				throw new ExceptionWrongBank("Wrong server " + bank + " != " + this.bank.getServerName());

			DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

			System.out.println(" - delayPayment(" + loanID + "," + currentDueDate + "," + newDueDate + ")");
			if (this.bank.delayPayment(loanID, df.parse(currentDueDate), df.parse(newDueDate)))
			{
				result = 0;
			}
			else
			{
				result = -1;
			}
		}
		catch (Exception e)
		{
			result = -1;
		}
		return result;
	}

	public String printCustomerInfo(String bank)
	{
		String result;
		try
		{
			if (this.bank.getServerName().equalsIgnoreCase(bank) == false)
				throw new ExceptionWrongBank("Wrong server " + bank + " != " + this.bank.getServerName());

			System.out.println(" - printCustomerInfo()");
			result = this.bank.printCustomerInfo().toString();
		}
		catch (Exception e)
		{
			result = null;
		}
		return result;
	}

	public int openAccount(String bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password)
	{
		int result;
		try
		{
			if (this.bank.getServerName().equalsIgnoreCase(bank) == false)
				throw new ExceptionWrongBank("Wrong server " + bank + " != " + this.bank.getServerName());

			System.out.println(" - openAccount");
			result = this.bank.openAccount(firstName, lastName, emailAddress, phoneNumber, password);
		}
		catch (Exception e)
		{
			result = -1;
		}
		return result;
	}

	public int getLoan(String bank, int accountNumber, String password, long loanAmount)
	{
		int loanID;
		try
		{
			if (this.bank.getServerName().equalsIgnoreCase(bank) == false)
				throw new ExceptionWrongBank("Wrong server " + bank + " != " + this.bank.getServerName());

			System.out.println(" - getLoan");
			loanID = this.bank.getLoan(accountNumber, password, loanAmount);
		}
		catch (Exception e)
		{
			loanID = -1;
		}
		return loanID;
	}
}
