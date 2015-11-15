package dlms.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

import dlms.exception.*;
import dlms.util.*;

/**
 * @author Pascal Tozzi 27664850 ServerBank containing all the logic of the
 *         service
 */
public class ServerBank
{
	private String name;
	private ConcurrentHashMapLists<Customer> accounts = new ConcurrentHashMapLists<Customer>();
	private ConcurrentHashMapLists<Loan> loans = new ConcurrentHashMapLists<Loan>();
	private static final double LOAN_LIMIT = 1000;
	private int loanCounter = 0;

	private UDPServerThread udpServer = null;

	public ServerBank(ServerInfo server)
	{
		this.name = server.getServerName();
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
	public ConcurrentHashMapLists<Customer> getAccounts()
	{
		return accounts;
	}

	/**
	 * @return the loans
	 */
	public ConcurrentHashMapLists<Loan> getLoans()
	{
		return loans;
	}

	/**
	 * Constructor
	 * 
	 * @param server
	 * @param isServerInstance
	 * @throws Exception
	 */
	public ServerBank(ServerInfo server, boolean isServerInstance) throws Exception
	{
		this(server);

		if (isServerInstance == true)
		{
			int port = server.getPort();
			Env.log(Level.FINE, "Starting UDP port " + port, true);
			udpServer = new UDPServerThread(this, port);
			udpServer.start();

			String loan_filename = Env.getServerLoansFile(this.getName(), "*");
			String loan_filename_left = loan_filename.substring(0, loan_filename.indexOf("*"));
			String loan_filename_right = loan_filename.substring(loan_filename_left.length() + 1);

			String customer_filename = Env.getServerCustomersFile(this.getName(), "*");
			String customer_filename_left = customer_filename.substring(0, customer_filename.indexOf("*"));
			String customer_filename_right = customer_filename.substring(customer_filename_left.length() + 1);

			// remove ./
			loan_filename_left = loan_filename_left.substring(2);
			customer_filename_left = customer_filename_left.substring(2);
			loan_filename = loan_filename.substring(2);
			customer_filename = customer_filename.substring(2);

			File folder = new File("./");
			File[] listOfFiles = folder.listFiles();

			Env.log(Level.FINE, "Loading Loans and Customers files", true);
			for (int i = 0; i < listOfFiles.length; i++)
			{
				if (listOfFiles[i].isFile() && listOfFiles[i].getName().length() == loan_filename.length()
						&& (listOfFiles[i].getName().startsWith(loan_filename_left)) && (listOfFiles[i].getName().endsWith(loan_filename_right)))
				{
					Env.log(Level.FINE, "Loading Loans: " + listOfFiles[i].getName(), true);
					ArrayList<Loan> lstLoans = XMLHelper.readLoans(listOfFiles[i].getName());
					for (Loan loan : lstLoans)
					{
						this.loans.put(loan.getAccountNumber(), loan);

						if (loan.getLoanID() > loanCounter)
						{
							// Re-initialize the LoanCounterID to the max Loan
							// from this bank.
							loanCounter = loan.getLoanID();
						}
					}
				}

				if (listOfFiles[i].isFile() && listOfFiles[i].getName().length() == customer_filename.length()
						&& (listOfFiles[i].getName().startsWith(customer_filename_left)) && (listOfFiles[i].getName().endsWith(customer_filename_right)))
				{
					Env.log(Level.FINE, "Loading Customers: " + listOfFiles[i].getName(), true);
					ArrayList<Customer> lstCustomers = XMLHelper.readCustomers(listOfFiles[i].getName());
					for (Customer customer : lstCustomers)
					{
						this.accounts.put(customer.getAccountNumber(), customer);
					}
				}
			}
		}
	}

	/**
	 * openAccount
	 * 
	 * @param firstName
	 * @param lastName
	 * @param emailAddress
	 * @param phoneNumber
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public String openAccount(String firstName, String lastName, String emailAddress, String phoneNumber, String password) throws Exception
	{
		/*
		 * When a customer invokes this method through a CustomerClient program,
		 * the server associated with the bank (Bank) attempts to create an
		 * account with the information passed if the customer does not already
		 * have an account by inserting the account at the appropriate location
		 * in the hash table. The server returns the created account number to
		 * the customer.
		 */

		Env.log(Level.FINE, "openAccount(" + firstName + "," + lastName + ")", true);

		Customer customer = new Customer();
		customer.setFirstName(firstName);
		customer.setLastName(lastName);
		customer.setEmailAddress(emailAddress);
		customer.setPhoneNumber(phoneNumber);
		customer.setPassword(password);
		customer.setCreditLimit(LOAN_LIMIT);

		// Verify if customer already exist with first name and last name
		if (this.accounts.get(customer.getUserName()) != null)
		{
			Env.log(Level.WARNING, firstName + "," + lastName + ": Customer Already Exist", true);
			throw new ExceptionCustomerAlreadyExist(customer.getUserName());
		}

		String accountNumber = customer.getUserName(); // Env.getRandomAccountNumber();

		customer.setAccountNumber(accountNumber);
		Env.log(Level.FINE, firstName + "," + lastName + ": Adding the account.", true);
		this.accounts.put(accountNumber, customer);
		// Release
		Env.log(Level.FINE, firstName + "," + lastName + ": Commit() to XML", true);
		this.accounts.commit(Env.getServerCustomersFile(this.getName(), accountNumber), accountNumber, true);

		Env.log(Level.FINE, firstName + "," + lastName + ": Created.", true);
		return accountNumber;
	}

	/**
	 * getLoan
	 * 
	 * @param accountNumber
	 * @param password
	 * @param loanAmount
	 * @return
	 * @throws Exception
	 */
	public int getLoan(String accountNumber, String password, double loanAmount) throws Exception
	{
		/*
		 * When a customer invokes this method through a CustomerClient program,
		 * the server associated with the specified bank (Bank) attempts to find
		 * if the customer account exist. If the account exists and if the
		 * outstanding loans at all the banks do not exceed the customer’s
		 * credit limit then the specified bank server (Bank) issues to loan by
		 * creating an appropriate loan object. A bank finds out whether the
		 * customer has outstanding loans in the other banks using UDP/IP
		 * messages.
		 */

		Env.log(Level.FINE, "getLoan(" + accountNumber + ")", true);

		Customer customer = (Customer) this.accounts.get(accountNumber);
		if (customer == null)
		{
			Env.log(Level.SEVERE, accountNumber + " NotValidCustomerAccountID", true);
			throw new ExceptionNotValidCustomerAccountID();
		}

		if (!customer.getPassword().equals(password))
		{
			Env.log(Level.SEVERE, accountNumber + " InvalidPassword", true);
			throw new ExceptionInvalidPassword();
		}

		int loanApprovedID = -1;
		synchronized (customer)
		{
			Loan loan = (Loan) this.loans.get(accountNumber);
			if (loan == null)
			{
				Env.log(Level.FINE, accountNumber + " Creating Loan", true);
				loan = new Loan(customer.getUserName());
			}
			else
			{
				Env.log(Level.SEVERE, accountNumber + " OnlyOneLoanPerCustomer", true);
				throw new ExceptionOnlyOneLoanPerCustomer();
			}

			Env.log(Level.FINE, accountNumber + " Starting Credit Check.", true);
			double totalLoanAmount = loan.getLoanAmount();
			ArrayList<UDPRequestThreadWithRetry> threads = new ArrayList<UDPRequestThreadWithRetry>();
			for (ServerInfo sv : Env.getLstServers())
			{
				if (!sv.getServerName().equalsIgnoreCase(this.getName()))
				{
					UDPRequestThreadWithRetry request = new UDPRequestThreadWithRetry(new UDPRequestThread(sv.getIpAddress(), sv.getPort(),
							customer.getUserName()), Constant.AMOUNT_OF_RETRY_UDP);
					threads.add(request);
					request.start();

					Env.log(Level.FINE, accountNumber + " Credit Check UDP: " + sv.getServerName(), true);
				}
			}

			for (UDPRequestThreadWithRetry request : threads)
			{
				try
				{
					request.join();
				}
				catch (InterruptedException e)
				{
					Env.log(Level.SEVERE, accountNumber + " Credit Check UDPBankNotAvailableRetryLater", true);
					throw new ExceptionUDPBankNotAvailableRetryLater(e.getMessage());
				}

				if (request.isError())
				{
					Env.log(Level.SEVERE, accountNumber + " Credit Check UDPBankNotAvailableRetryLater", true);
					throw new ExceptionUDPBankNotAvailableRetryLater(request.getErrorMessage());
				}
				else
				{
					totalLoanAmount += request.getLoanAmount();
				}
			}

			if (customer.getCreditLimit() >= totalLoanAmount + loanAmount)
			{
				loan.setLoanAmount(loanAmount);
				synchronized (this)
				{
					loan.setLoanID(++loanCounter);
				}
				this.loans.put(customer.getUserName(), loan);
				this.loans.commit(Env.getServerLoansFile(this.getName(), customer.getUserName()), customer.getUserName(), false);

				loanApprovedID = loan.getLoanID();
				Env.log(Level.FINE, accountNumber + " Credit Check Accepted.", true);
			}
			else
			{
				Env.log(Level.FINE, accountNumber + " Credit Check Refused." + customer.getCreditLimit() + "<" + (totalLoanAmount + loanAmount), true);
			}
		}

		return loanApprovedID;
	}

	/**
	 * delayPayment
	 * 
	 * @param loanID
	 * @param currentDueDate
	 * @param newDueDate
	 * @return
	 * @throws Exception
	 */
	public boolean delayPayment(int loanID, Date currentDueDate, Date newDueDate) throws Exception
	{
		Env.log(Level.FINE, "delayPayment(" + loanID + ")", true);
		boolean isPaymentDelayed = false;
		Loan loan = getLoans().getLoan(loanID);
		if (loan == null)
		{
			Env.log(Level.SEVERE, "delayPayment(" + loanID + ") InvalidLoanID", true);
			throw new ExceptionInvalidLoanID();
		}

		synchronized (loan)
		{
			ArrayList<Loan> list = getLoans().getList(loan.getAccountNumber());
			synchronized (list)
			{
				Date loanDate = loan.getLoanDueDate();
				// prevent the list to commit while changing a value
				if (loanDate.getTime() == currentDueDate.getTime())
				{
					loan.setLoanDueDate(newDueDate);
					// Commit data + history
					isPaymentDelayed = true;
					Env.log(Level.FINE, loan.getAccountNumber() + " delayPayment(" + loanID + ") DueDate modified.", true);
				}
				else
				{
					Env.log(Level.WARNING, loan.getAccountNumber() + " delayPayment(" + loanID + ") InvalidDueDate (Refresh issue)", true);
					throw new ExceptionInvalidDueDate();
				}
			}
		}

		Env.log(Level.FINE, loan.getAccountNumber() + " delayPayment(" + loanID + ") commit()", true);
		String username = loan.getAccountNumber();
		this.loans.commit(Env.getServerLoansFile(this.getName(), username), username, false);

		Env.log(Level.FINE, loan.getAccountNumber() + " delayPayment(" + loanID + ") commit() Successful", true);
		return isPaymentDelayed;
	}

	/**
	 * printCustomerInfo
	 * 
	 * @return bank with all user and loan informations
	 */
	public Bank printCustomerInfo()
	{
		Env.log(Level.FINE, "printCustomerInfo()", true);
		Bank bankInfo = new Bank(this.getName());
		bankInfo.loans.addAll(this.getLoans().getAllLoans());
		for (Customer customer : this.getAccounts().getAllCustomers())
		{
			bankInfo.accounts.put(customer.getUserName(), customer);
		}
		return bankInfo;
	}

	/**
	 * transferLoan
	 * 
	 * @param loanID
	 * @param currentBank
	 * @param otherBank
	 * @return
	 * @throws Exception
	 */
	public boolean transferLoan(int loanID, ServerInfo otherBank) throws Exception
	{
		/*
		 * This function is used by a customer to transfer a previously obtained
		 * loan (LoanID) from the CurrentBank to the OtherBank. When a customer
		 * invokes this method through a CustomerClient program, the server
		 * associated with the CurrentBank checks whether the customer has the
		 * specified loan (LoanID) and then transfers the loan (with the
		 * associated customer account, if necessary) to the OtherBank if
		 * possible using UDP/IP messages (instead of invoking getLoan
		 * function). The OtherBank first checks if there is a customer account
		 * in it’s hash map; if not, it creates a new account. Note that
		 * creating the customer account (if necessary), creating the required
		 * loan at the OtherBank and cancelling the existing loan at the
		 * CurrentBank should all be done atomically (that is, all should be
		 * done or none should be done) using UDP/IP messages.
		 */
		Env.log(Level.FINE, "transferLoan(loanID: " + loanID + ", currentBank: " + this.getName() + ", otherBank: " + otherBank.getServerName() + ")", true);

		Loan loan = this.loans.getLoan(loanID);
		if (loan == null)
		{
			Env.log(Level.SEVERE, loanID + " InvalidLoanID", true);
			throw new ExceptionInvalidLoanID();
		}

		String accountNumber = loan.getAccountNumber();
		Customer customer = (Customer) this.accounts.get(accountNumber);
		if (customer == null)
		{
			Env.log(Level.SEVERE, accountNumber + " NotValidCustomerAccountID", true);
			throw new ExceptionNotValidCustomerAccountID();
		}

		boolean isTransfered = false;
		synchronized (customer)
		{
			// Get the loan now that the customer is locked
			loan = (Loan) this.loans.get(accountNumber);
			if (loan == null)
			{
				Env.log(Level.SEVERE, loanID + " InvalidLoanID", true);
				throw new ExceptionInvalidLoanID();
			}

			Env.log(Level.FINE, loanID + " Removing the loan to transfer.", true);

			// Customer resource locked, removing the loan temporarily in memory
			// only
			if (this.loans.remove(accountNumber))
			{
				Env.log(Level.FINE, loanID + " Starting Loan transfer.", true);

				UDPRequestThreadWithRetry request = new UDPRequestThreadWithRetry(new UDPRequestThread(otherBank.getIpAddress(), otherBank.getPort(),
						customer.getUserName(), customer, loan), Constant.AMOUNT_OF_RETRY_UDP);

				// Doesn't actually need to be on a separate thread
				// We call run directly instead of start to skip the thread
				request.run();

				if (request.isLoanTransfered())
				{
					Env.log(Level.FINE, loanID + " Transfer successfull, committing the data to the database.", true);
					try
					{
						this.loans.commit(Env.getServerLoansFile(this.getName(), customer.getUserName()), customer.getUserName(), false);
						isTransfered = true;
					}
					catch (Exception e)
					{
						// Save to database should never fail, in case it fail,
						// we need to log the loanID to ensure it's removed at a
						// later time.
						Env.log(Level.SEVERE, loanID + " Database error, need to remove the transfered loan!", true);

						// this should never happen, I wont deal with this case
						// in assignment.
						// If this fail, it's because writing to file fail, thus
						// we need another method of writing to...
						// An example would be to write to a database running on
						// another machine

						// Assumption saving to disk doesn't have any failure,
						// otherwise assignment would be way harder.
						// Another assumption is that the UDP doesn't fail, we
						// need to implement TCP/IP or use TCP/IP instead for
						// more security
					}
				}
				else
				{
					Env.log(Level.FINE, loanID + " Failed transfering the loan, putting it back in the list.", true);
					// Adding the removed loan
					this.loans.put(customer.getUserName(), loan);
				}
			}
		}

		return isTransfered;
	}

	/**
	 * createTransferedLoan
	 * 
	 * @param customer
	 * @param loan
	 * @throws Exception
	 */
	public int createTransferedLoan(Customer customer, Loan loan) throws Exception
	{
		// The customer is locked from the parent calling this method
		// thus impossible to create a loan on that customer
		Env.log(Level.FINE, customer.getUserName() + " Creating Loan", true);
		Loan newLoan = new Loan(customer.getUserName());

		synchronized (this)
		{
			newLoan.setLoanID(++loanCounter);
		}
		newLoan.setLoanDueDate(loan.getLoanDueDate());
		newLoan.setLoanAmount(loan.getLoanAmount());

		this.loans.put(customer.getUserName(), newLoan);
		try
		{
			this.loans.commit(Env.getServerLoansFile(this.getName(), customer.getUserName()), customer.getUserName(), false);
		}
		catch (Exception e)
		{
			// remove the loan from the memory and return the error
			this.loans.remove(customer.getUserName());
			throw e;
		}

		return loan.getLoanID();
	}
}
