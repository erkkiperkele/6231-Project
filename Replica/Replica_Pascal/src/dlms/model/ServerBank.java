package dlms.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import shared.exception.*;
import dlms.util.*;
import shared.data.AbstractServerBank;
import shared.data.BankState;
import shared.data.Customer;
import shared.data.Loan;
import shared.data.ServerInfo;
import shared.udp.UdpReplicaServiceThread;
import shared.util.Env;

/**
 * @author Pascal Tozzi 27664850 ServerBank containing all the logic of the
 *         service
 */
public class ServerBank extends AbstractServerBank
{
	private String name;
	private CustomerConcurrentHashMapLists accounts = new CustomerConcurrentHashMapLists();
	private LoanConcurrentHashMapLists loans = new LoanConcurrentHashMapLists();
	private static final long LOAN_LIMIT = 1000;
	private int loanCounter = 0;
	private int customerCounter = 0;

	private UdpReplicaServiceThread udpServer = null;

	public ServerBank(ServerInfo server)
	{
		this.name = Env.getCurrentBank().toString();
	}

	/**
	 * @return the name
	 */
	public String getServerName()
	{
		return this.name;
	}

	/**
	 * @return the accounts
	 */
	public CustomerConcurrentHashMapLists getAccounts()
	{
		return accounts;
	}

	/**
	 * @return the loans
	 */
	public LoanConcurrentHashMapLists getLoans()
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
			Env.log(Level.FINE, "Starting server service for " + server.getServerName());
			String loan_filename = EnvP.getServerLoansFile(this.getServerName(), "*");
			String loan_filename_left = loan_filename.substring(0, loan_filename.indexOf("*"));
			String loan_filename_right = loan_filename.substring(loan_filename_left.length() + 1);

			String customer_filename = EnvP.getServerCustomersFile(this.getServerName(), "*");
			String customer_filename_left = customer_filename.substring(0, customer_filename.indexOf("*"));
			String customer_filename_right = customer_filename.substring(customer_filename_left.length() + 1);

			// remove ./
			loan_filename_left = loan_filename_left.substring(2);
			customer_filename_left = customer_filename_left.substring(2);
			loan_filename = loan_filename.substring(2);
			customer_filename = customer_filename.substring(2);

			File folder = new File("./");
			File[] listOfFiles = folder.listFiles();

			Env.log(Level.FINE, "Loading Loans and Customers files");
			for (int i = 0; i < listOfFiles.length; i++)
			{
				if (listOfFiles[i].isFile() && listOfFiles[i].getName().length() == customer_filename.length()
						&& (listOfFiles[i].getName().startsWith(customer_filename_left)) && (listOfFiles[i].getName().endsWith(customer_filename_right)))
				{
					Env.log(Level.FINE, "Loading Customers: " + listOfFiles[i].getName());
					ArrayList<Customer> lstCustomers = XMLHelper.readCustomers(listOfFiles[i].getName());
					for (Customer customer : lstCustomers)
					{
						this.accounts.put(customer);
					}
				}
				
				if (listOfFiles[i].isFile() && listOfFiles[i].getName().length() == loan_filename.length()
						&& (listOfFiles[i].getName().startsWith(loan_filename_left)) && (listOfFiles[i].getName().endsWith(loan_filename_right)))
				{
					Env.log(Level.FINE, "Loading Loans: " + listOfFiles[i].getName());
					ArrayList<Loan> lstLoans = XMLHelper.readLoans(listOfFiles[i].getName());
					for (Loan loan : lstLoans)
					{
						this.loans.put(getStringUsername(loan), loan);

						if (loan.getLoanNumber() > loanCounter)
						{
							// Re-initialize the LoanCounterID to the max Loan
							// from this bank.
							loanCounter = loan.getLoanNumber();
						}
					}
				}
			}
			
			int port = server.getPort();
			Env.log(Level.FINE, "Starting UDP port " + port);
			udpServer = new UdpReplicaServiceThread("Pascal Replica Implementation", port, this);
			udpServer.start();
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
	public int openAccount(String firstName, String lastName, String emailAddress, String phoneNumber, String password) throws Exception
	{
		/*
		 * When a customer invokes this method through a CustomerClient program,
		 * the server associated with the bank (Bank) attempts to create an
		 * account with the information passed if the customer does not already
		 * have an account by inserting the account at the appropriate location
		 * in the hash table. The server returns the created account number to
		 * the customer.
		 */

		Env.log(Level.FINE, "openAccount(" + firstName + "," + lastName + ")");

		Customer customer = new Customer(firstName, lastName, password, emailAddress, phoneNumber);
		customer.setCreditLimit(LOAN_LIMIT);

		// Verify if customer already exist with first name and last name
		if (this.accounts.get(customer.getUserName()) != null)
		{
			Env.log(Level.WARNING, firstName + "," + lastName + ": Customer Already Exist");
			throw new ExceptionCustomerAlreadyExist(customer.getUserName());
		}

		String accountName = getStringUsername(customer);
		Env.log(Level.FINE, firstName + "," + lastName + ": Adding the account.");
		this.accounts.put(customer);
		// Release
		Env.log(Level.FINE, firstName + "," + lastName + ": Commit() to XML");
		this.accounts.commit(EnvP.getServerCustomersFile(this.getServerName(), accountName), accountName);

		synchronized(this)
		{
			customer.setAccountNumber(++customerCounter);
		}
		Env.log(Level.FINE, firstName + "," + lastName + ": Created.");
		return customer.getAccountNumber();
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
	public int getLoan(int accountNumber, String password, long loanAmount) throws Exception
	{
		/*
		 * When a customer invokes this method through a CustomerClient program,
		 * the server associated with the specified bank (Bank) attempts to find
		 * if the customer account exist. If the account exists and if the
		 * outstanding loans at all the banks do not exceed the customer�s
		 * credit limit then the specified bank server (Bank) issues to loan by
		 * creating an appropriate loan object. A bank finds out whether the
		 * customer has outstanding loans in the other banks using UDP/IP
		 * messages.
		 */

		Env.log(Level.FINE, "getLoan(" + accountNumber + ")");

		Customer customer = (Customer) this.accounts.getCustomer(accountNumber);
		if (customer == null)
		{
			Env.log(Level.SEVERE, accountNumber + " NotValidCustomerAccountID");
			throw new ExceptionNotValidCustomerAccountID();
		}

		if (!customer.getPassword().equals(password))
		{
			Env.log(Level.SEVERE, accountNumber + " InvalidPassword");
			throw new ExceptionInvalidPassword();
		}

		int loanApprovedID = -1;
		synchronized (customer)
		{
			Loan loan = (Loan) this.loans.getLoan(accountNumber);
			if (loan == null)
			{
				Env.log(Level.FINE, accountNumber + " Creating Loan");
			}
			else
			{
				Env.log(Level.SEVERE, accountNumber + " OnlyOneLoanPerCustomer");
				throw new ExceptionOnlyOneLoanPerCustomer();
			}

			Env.log(Level.FINE, accountNumber + " Starting Credit Check.");
			double totalLoanAmount = 0;
			ArrayList<UDPRequestThread> threads = new ArrayList<UDPRequestThread>();
			for (ServerInfo sv : Env.getReplicaServerInfoList())
			{
				if (!sv.getServerName().equalsIgnoreCase(this.getServerName()))
				{
					UDPRequestThread request = new UDPRequestThread(sv.getIpAddress(), sv.getPort(),
							customer.getUserName());
					threads.add(request);
					request.start();

					Env.log(Level.FINE, accountNumber + " Credit Check UDP: " + sv.getServerName());
				}
			}

			for (UDPRequestThread request : threads)
			{
				try
				{
					request.join();
				}
				catch (InterruptedException e)
				{
					Env.log(Level.SEVERE, accountNumber + " Credit Check UDPBankNotAvailableRetryLater");
					throw new ExceptionUDPBankNotAvailableRetryLater(e.getMessage());
				}

				if (request.isError(null))
				{
					Env.log(Level.SEVERE, accountNumber + " Credit Check UDPBankNotAvailableRetryLater");
					throw new ExceptionUDPBankNotAvailableRetryLater(request.getErrorMessage());
				}
				else
				{
					totalLoanAmount += request.getLoanAmount();
				}
			}

			if (customer.getCreditLimit() >= totalLoanAmount + loanAmount)
			{
				int loanID;
				synchronized (this)
				{
					loanID = ++loanCounter;
				}
				loan = new Loan(loanID, accountNumber, loanAmount, EnvP.getNewLoanDueDate());
				this.loans.put(customer.getUserName(), loan);
				this.loans.commit(EnvP.getServerLoansFile(this.getServerName(), customer.getUserName()), customer.getUserName());

				loanApprovedID = loan.getLoanNumber();
				Env.log(Level.FINE, accountNumber + " Credit Check Accepted.");
			}
			else
			{
				Env.log(Level.FINE, accountNumber + " Credit Check Refused." + customer.getCreditLimit() + "<" + (totalLoanAmount + loanAmount));
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
		Env.log(Level.FINE, "delayPayment(" + loanID + ")");
		boolean isPaymentDelayed = false;
		Loan loan = getLoans().getLoan(loanID);
		if (loan == null)
		{
			Env.log(Level.SEVERE, "delayPayment(" + loanID + ") InvalidLoanID");
			throw new ExceptionInvalidLoanID();
		}

		synchronized (loan)
		{
			ArrayList<Loan> list = getLoans().getList(getStringUsername(loan));
			synchronized (list)
			{
				Date loanDate = loan.getDueDate();
				// prevent the list to commit while changing a value
				if (loanDate.getTime() == currentDueDate.getTime())
				{
					loan.setDueDate(newDueDate);
					// Commit data + history
					isPaymentDelayed = true;
					Env.log(Level.FINE, loan.getCustomerAccountNumber() + " delayPayment(" + loanID + ") DueDate modified.");
				}
				else
				{
					Env.log(Level.WARNING, loan.getCustomerAccountNumber() + " delayPayment(" + loanID + ") InvalidDueDate (Refresh issue)");
					throw new ExceptionInvalidDueDate();
				}
			}
		}

		Env.log(Level.FINE, loan.getCustomerAccountNumber() + " delayPayment(" + loanID + ") commit()");
		String username = getStringUsername(loan);
		this.loans.commit(EnvP.getServerLoansFile(this.getServerName(), username), username);

		Env.log(Level.FINE, loan.getCustomerAccountNumber() + " delayPayment(" + loanID + ") commit() Successful");
		return isPaymentDelayed;
	}

	/**
	 * printCustomerInfo
	 * 
	 * @return bank with all user and loan informations
	 */
	public String printCustomerInfo()
	{
		Env.log(Level.FINE, "printCustomerInfo()");
		Bank bankInfo = new Bank(this.getServerName());
		bankInfo.loans.addAll(this.getLoans().getAllLoans());
		for (Customer customer : this.getAccounts().getAllCustomers())
		{
			bankInfo.accounts.put(customer.getUserName(), customer);
		}
		return bankInfo.toString();
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
	public boolean transferLoan(int loanID, String otherBankName) throws Exception
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
		 * in it�s hash map; if not, it creates a new account. Note that
		 * creating the customer account (if necessary), creating the required
		 * loan at the OtherBank and cancelling the existing loan at the
		 * CurrentBank should all be done atomically (that is, all should be
		 * done or none should be done) using UDP/IP messages.
		 */
		Env.log(Level.FINE, "transferLoan(loanID: " + loanID + ", currentBank: " + this.getServerName() + ", otherBank: " + otherBankName + ")");

		ServerInfo otherBank = dlms.StartBankServer.getServerInformation(otherBankName);
		
		Loan loan = this.loans.getLoan(loanID);
		if (loan == null)
		{
			Env.log(Level.SEVERE, loanID + " InvalidLoanID");
			throw new ExceptionInvalidLoanID();
		}

		String accountNumber = getStringUsername(loan);
		Customer customer = (Customer) this.accounts.get(accountNumber);
		if (customer == null)
		{
			Env.log(Level.SEVERE, accountNumber + " NotValidCustomerAccountID");
			throw new ExceptionNotValidCustomerAccountID();
		}

		boolean isTransfered = false;
		synchronized (customer)
		{
			// Get the loan now that the customer is locked
			loan = (Loan) this.loans.get(accountNumber, loanID);
			if (loan == null)
			{
				Env.log(Level.SEVERE, loanID + " InvalidLoanID");
				throw new ExceptionInvalidLoanID();
			}

			Env.log(Level.FINE, loanID + " Removing the loan to transfer.");

			// Customer resource locked, removing the loan temporarily in memory
			// only
			if (this.loans.remove(accountNumber, loanID))
			{
				Env.log(Level.FINE, loanID + " Starting Loan transfer.");

				UDPRequestThread request = new UDPRequestThread(otherBank.getIpAddress(), otherBank.getPort(),
						customer.getUserName(), customer, loan);

				// Doesn't actually need to be on a separate thread
				// We call run directly instead of start to skip the thread
				request.run();

				if (request.isLoanTransfered())
				{
					Env.log(Level.FINE, loanID + " Transfer successfull, committing the data to the database.");
					try
					{
						this.loans.commit(EnvP.getServerLoansFile(this.getServerName(), customer.getUserName()), customer.getUserName());
						isTransfered = true;
					}
					catch (Exception e)
					{
						// Save to database should never fail, in case it fail,
						// we need to log the loanID to ensure it's removed at a
						// later time.
						Env.log(Level.SEVERE, loanID + " Database error, need to remove the transfered loan!");

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
					Env.log(Level.FINE, loanID + " Failed transfering the loan, putting it back in the list.");
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
	public int createTransferedLoan(shared.data.Customer customer, shared.data.Loan loan) throws Exception
	{
		// The customer is locked from the parent calling this method
		// thus impossible to create a loan on that customer
		Env.log(Level.FINE, customer.getUserName() + " Creating Loan");

		int loanNumber;
		synchronized (this)
		{
			loanNumber = ++loanCounter;
			Loan newLoan = new Loan(loanNumber, loan.getCustomerAccountNumber(), loan.getAmount(), loan.getDueDate());
			
			this.loans.put(customer.getUserName(), newLoan);
			try
			{
				this.loans.commit(EnvP.getServerLoansFile(this.getServerName(), customer.getUserName()), customer.getUserName());
			}
			catch (Exception e)
			{
				// remove the loan from the memory and return the error
				this.loans.remove(customer.getUserName(), loanNumber);
				loanCounter--;
				throw e;
			}
		}

		return loan.getLoanNumber();
	}
	
	/**
	 * Changing the key to email,
	 * before it was <lastname+firstname>
	 * @param loan
	 * @return
	 */
	private String getStringUsername(Loan loan) 
	{
		// Search the customer with a specific customerID
		Customer customer = (Customer)this.accounts.getCustomer(loan.getCustomerAccountNumber());
		return getStringUsername(customer);
	}
	
	/**
	 * Changing the key to email,
	 * before it was <lastname+firstname>
	 * @param loan
	 * @return
	 */
	private String getStringUsername(Customer customer) 
	{
		return customer.getEmail();
	}


	/**
	 * Loop that wait forever based on udpServer thread
	 * @throws InterruptedException 
	 */
	public void waitUntilUDPServiceEnd() throws InterruptedException 
	{
		udpServer.join();
	}

	@Override
	public BankState getCurrentState() {
		List<Loan> loanList = new ArrayList<Loan>();
		List<Customer> customerList = new ArrayList<Customer>();
		loanList.addAll(loans.getAllLoans());
		customerList.addAll(accounts.getAllCustomers());
		int nextCustomerID = customerCounter + 1;
		int nextLoanID = loanCounter + 1;
		return new BankState(loanList, customerList, nextCustomerID, nextLoanID);
	}

	@Override
	public void setCurrentState(BankState state) {
		// TODO Auto-generated method stub
		
	}
}
