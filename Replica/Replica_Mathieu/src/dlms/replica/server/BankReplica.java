package dlms.replica.server;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import shared.udp.UdpReplicaServiceThread;
import dlms.replica.exception.ValidationException;
import dlms.replica.model.Account;
import dlms.replica.model.Loan;
import dlms.replica.model.ThreadSafeHashMap;
import dlms.replica.udpmessage.MessageResponseLoanSum;
import dlms.replica.udpmessage.MessageResponseTransferLoan;
import shared.data.AbstractServerBank;
import shared.data.BankState;
import shared.data.Customer;

/**
 * The bank replica acts as a bank server and proxy for the underlying bank
 * entity
 * 
 * @author mat
 * 
 */
public class BankReplica extends AbstractServerBank {

	public static final int MAX_DATAGRAM_SIZE = 4096;
	public static final boolean ENABLE_FILE_LOGGING = false;
	
	private BankReplicaStubGroup group = null;
	private Logger logger = null;
	private Thread udpListenerThread;
	private UdpListener udpListener;
	private volatile Bank bank;
	private UdpReplicaServiceThread udpServer;
	/**
	 * Constructor
	 * 
	 * @param id
	 * @param addr
	 */
	public BankReplica(String bankId, BankReplicaStubGroup replicaStubs, int sequencerListenerPort) {

		if (replicaStubs.get(bankId) == null) {
			throw new RuntimeException("Replica: " + bankId + " does not belong to the group of replicas provided");
		}
		
		this.group = replicaStubs;
		this.bank = new Bank(bankId, replicaStubs.get(bankId).addr);
		
		// Make sure bankId is a key of replicaStubs
		
		// Set up the logger
		this.logger = Logger.getLogger(this.bank.getId());

		if (ENABLE_FILE_LOGGING) {
		    FileHandler fh;  
		    try {
		        fh = new FileHandler(this.bank.id + "-log.txt");  
		        logger.addHandler(fh);
		        SimpleFormatter formatter = new SimpleFormatter();  
		        fh.setFormatter(formatter);  
		        logger.info(this.bank.id + " logger started");  
		    } catch (SecurityException e) {  
		        e.printStackTrace();
		        System.exit(1);
		    } catch (IOException e) {  
		        e.printStackTrace(); 
		        System.exit(1);
		    }
		}

		// Start the bank's sequencer's UDP listener
		try {
			udpServer = new UdpReplicaServiceThread("Mat's Replica Implementation of " + bankId + " bank", sequencerListenerPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		udpServer.start();

	    // Start the bank's internal UDP listener
		udpListener = new UdpListener(this.bank, this.logger);
		udpListenerThread = new Thread(udpListener);
		udpListenerThread.start();
	}
	
	/**
	 * Gets the bank object of this replica
	 * 
	 * @return
	 */
	public Bank getBank() {
		
		return this.bank;
	}
	
	/**
	 * Gets the stub of this replica
	 * 
	 * @return
	 */
	public BankReplicaStub getStub() {
		
		return new BankReplicaStub(this.bank.getId(), this.bank.getUdpAddress());
	}
	
	/**
	 * Set the group of replicas to which this replica belongs
	 * 
	 * @return
	 */
	public void setGroup(BankReplicaStubGroup group) {
		
		this.group = group;
	}
	
	/**
	 * 
	 * @param firstName
	 * @param lastName
	 * @param emailAddress
	 * @param phoneNumber
	 * @param password
	 * @return
	 */
	public int openAccount(String firstName, String lastName, String emailAddress, String phoneNumber,
			String password) throws Exception {

		int newAccountNbr = -1;
		
		logger.info("-------------------------------\n" + this.bank.id + ": Client invoked openAccount(firstName:"
				+ firstName + ", lastName:" + lastName + ", emailAddress:" + emailAddress + ", phoneNumber:"
				+ phoneNumber + ", password:" + password + ")");
		
		synchronized(this) {
	
			try {
				newAccountNbr = this.bank.createAccount(firstName, lastName, emailAddress, phoneNumber, password);
			} catch (ValidationException e) {
				logger.info(this.bank.id + ": " + e.getMessage());
				throw new Exception(this.bank.id + ": " + e.getMessage());
			}
			
			if (newAccountNbr > 0) {
				logger.info(this.bank.id + ": successfully opened an account for user " + emailAddress
						+ " with account number " + newAccountNbr);
			}
			else {
				logger.info(this.bank.id + ": Invalid new account number created");
				throw new Exception(this.bank.id + ": Unable to create new account.");
			}
		}

		return newAccountNbr;
	}
	
	/**
	 * Returns customer information as a string
	 * 
	 * @return
	 */
	public String printCustomerInfo() {

		logger.info("-------------------------------\n" + this.bank.getId() + ": Client invoked printCustomerInfo()");

		String result = new String();
		result = "------ ACCOUNTS ------\n";
		
		for (String key : this.bank.accounts.keySet()) {
			ThreadSafeHashMap<Integer, Account> accountsByLetter = this.bank.accounts.get(key);
			for (Integer accountId : accountsByLetter.keySet()) {
				Account account = accountsByLetter.get(accountId);
				result += account.toString() + "\n";
			}
		}
		
		result += "------ LOANS ------\n";
		for (String key : this.bank.loans.keySet()) {
			ThreadSafeHashMap<Integer, Loan> loansByLetter = this.bank.loans.get(key);
			for (Integer loanId : loansByLetter.keySet()) {
				Loan loan = loansByLetter.get(loanId);
				result += loan.toString() + "\n";
			}
		}

		return result;
	}
	
	/**
	 * Delays the due date of a loan
	 * 
	 * @param loanId
	 * @param currentDueDate
	 * @param newDueDate
	 * @return
	 */
	public boolean delayPayment(int loanID, Date currentDueDate, Date newDueDate) throws Exception {

		logger.info(
				"-------------------------------\n" + this.bank.id + ": Client invoked delayPayment(loanId:"
						+ loanID + " currentDate: " + currentDueDate.toString() + " newDueDate: " + newDueDate.toString() + ")");

		Loan loan;
		loan = this.bank.getLoan(loanID);
		if (loan == null) {
			logger.info(this.bank.id + ": Loan id " + loanID + " does not exist");
			throw new Exception(this.bank.id + ": Loan id " + loanID + " does not exist");
		}
		
		this.bank.getLockObject(loan.getEmailAddress());

		synchronized (this) {
			
			loan = this.bank.getLoan(loanID);
			if (loan == null) {
				logger.info(this.bank.id + ": Loan id " + loanID + " does not exist");
				throw new Exception(this.bank.id + ": Loan id " + loanID + " does not exist");
			}
			// Uncomment when using date validation
			/*if (!loan.getDueDate().equals(dateCurrent)) {
				logger.info(this.bank.id + ": Loan id " + loanId + " - currentDate argument mismatch");
				return new ServerResponse(false, "", "Loan id " + loanId + " - currentDate argument mismatch");
			}*/
			if (!loan.getDueDate().before(newDueDate)) {
				logger.info(this.bank.id + ": Loan id " + loanID
						+ " - currentDueDate argument must be later than the actual current due date of the loan");
				throw new Exception(this.bank.id + ": Loan id " + loanID
						+ " - currentDueDate argument must be later than the actual current due date of the loan");
			}
			
			loan.setDueDate(newDueDate);
		}

		logger.info(this.bank.id + " loan " + loanID + " successfully delayed");
		return true;
	}

	/**
	 * 
	 * @param accountNbr
	 * @param password
	 * @param requestedLoanAmount
	 * @return
	 * @throws Exception 
	 */

	public int getLoan(int accountNumber, String password, long loanAmount) throws Exception {

		int newLoanId = -1; 
		int externalLoanSum = 0; // The total sum of loans at other banks for accountNumber
		Object lock = null;
		Account account; // The account corresponding to the account number
		ExecutorService pool;
		Set<Future<MessageResponseLoanSum>> set;
		
		logger.info("-------------------------------\n" + this.bank.id + ": Client invoked getLoan(accountNumber:"
				+ accountNumber + ", password:" + password + ", requestedLoanAmount:" + loanAmount + ")");

		// We need to get the lock object from the account number, so essentially, 
		// we're testing for account existence
		account = this.bank.getAccount(accountNumber);
		if (account == null) {
			String msg = "Loan refused at bank " + this.bank.getId() + ". Account " + accountNumber + " does not exist.";
			logger.info(this.bank.id + ": " + msg);
			throw new Exception(msg);
		}

		lock = this.bank.getLockObject(account.getEmailAddress());
	    	
		synchronized (lock) {

			// Test the existence of the account (again, now that we're in
			// the critical section)
			// The account could have gotten deleted just before the
			// synchronized block
			account = this.bank.getAccount(accountNumber);
			if (account == null) {
				String message = "Loan refused at bank " + this.bank.getId() + ". Account " + accountNumber
						+ " does not exist.";
				logger.info(this.bank.id + ": " + message);
				throw new Exception(message);
			}

			// Validate that passwords match
			if (!account.getPassword().equals(password)) {
				String message = "Loan refused at bank " + this.bank.getId() + ". Invalid credentials.";
				logger.info(this.bank.id + ": " + message);
				throw new Exception(message);
			}

			// Avoid making UDP requests if the loan amount is already
			// bigger than the credit limit of the local account
			int currentLoanAmount = this.bank.getLoanSum(account.getEmailAddress());
			if (currentLoanAmount + loanAmount > account.getCreditLimit()) {
				String message = "Loan refused at bank " + this.bank.getId() + ". Local credit limit exceeded";
				logger.info(this.bank.id + ": " + message);
				throw new Exception(message);
			}

			// Prepare the threads to call other banks to get the loan sum for this account
			pool = Executors.newFixedThreadPool(this.group.size()-1);
		    set = new HashSet<Future<MessageResponseLoanSum>>();
		    
			try {

				// Get the loan sum for all banks and approve or not the new loan
				for (BankReplicaStub destinationBank : this.group.values()) {
					if (!this.getStub().equals(destinationBank)) {
						Callable<MessageResponseLoanSum> callable = new UdpGetLoanCallable(this.bank, destinationBank,
								account.getEmailAddress(), 0, this.logger);
						Future<MessageResponseLoanSum> future = pool.submit(callable);
						set.add(future);
					}
				}

				for (Future<MessageResponseLoanSum> future : set) {

					try {
						MessageResponseLoanSum loanSumResponse = future.get();
						if (loanSumResponse == null) {
							String message = "Loan refused at bank " + this.bank.getId()
									+ ". Unable to obtain a status for the original loan request.";
							logger.info(this.bank.id + ": " + message);
							throw new Exception(message);
						} else if (loanSumResponse.status) {
							externalLoanSum += loanSumResponse.loanSum;
						} else {
							String message = "Loan refused at bank " + this.bank.getId() + ". "
									+ loanSumResponse.message;
							logger.info(this.bank.id + ": " + message);
							throw new Exception(message);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						String message = "Loan request failed for user " + account.getEmailAddress()
								+ ". InterruptedException";
						logger.info(this.bank.id + ": " + message);
						throw new Exception(message);

					} catch (ExecutionException e) {
						e.printStackTrace();
						String message = "Loan request failed for user " + account.getEmailAddress()
								+ ". ExecutionException";
						logger.info(this.bank.id + ": " + message);
						throw new Exception(message);
					}
				}

			} finally {
				pool.shutdown();
			}
			
			// Refuse the loan request if the sum of all loans is greater than the credit limit
			if ((loanAmount + externalLoanSum) > account.getCreditLimit()) {
				String message = "Loan refused at bank " + this.bank.getId() + ". Total credit limit exceeded.";
				logger.info(this.bank.id + ": " + message);
				throw new Exception(message);
			}
			
			// Loan is approved at this point
			newLoanId = this.bank.createLoan(account.getEmailAddress(), accountNumber, loanAmount);

			String message = "Loan approved for user " + account.getEmailAddress() + ", amount " + loanAmount + " at bank " + this.bank.getId() + ".";
			logger.info(this.bank.id + ": " + message);

			return newLoanId;
		}
	}
	
	/**
	 * Transfers a loan from one bank to another
	 * 
	 * @param loan
	 * @param otherBankId
	 * @return
	 * @throws Exception 
	 */
	public boolean transferLoan(int loanID, String otherBank) throws Exception {
	//public int transferLoan(Loan loan, BankReplicaStub otherBankStub) throws Exception {
		
		Loan loan = this.bank.getLoan(loanID);
		if (loan == null) {
			logger.info(this.bank.id + ": Loan transfer failed. LoanId " + loanID + " does not exist");
			throw new Exception(this.bank.id + ": Loan transfer failed. LoanId " + loanID + " does not exist");
		}
		
		BankReplicaStub otherBankStub = group.get(otherBank);
		Object lock = this.bank.getLockObject(loan.getEmailAddress());
		
		synchronized (lock) {

			// Re-check that the loan still exists
			int loanId = loan.getId();
			loan = this.bank.getLoan(loan.getId());
			if (loan == null) {
				logger.info(this.bank.id + ": Loan transfer " + loanId + " failed. LoanId " + loanId + " does not exist");
				throw new Exception("Loan transfer " + loanId + " failed. LoanId " + loanId + " does not exist");
			}
			
			ExecutorService executor = Executors.newSingleThreadExecutor();
			UdpTransferLoanCallable callable = new UdpTransferLoanCallable(this.bank, otherBankStub, loanId, 0, this.logger);
			Future<MessageResponseTransferLoan> future = executor.submit(callable);

			try {
				MessageResponseTransferLoan resp = future.get(5, TimeUnit.SECONDS);
				if (resp.status) {
				
					// Loan transfered successfully. It must now be deleted locally.
					// If it can't be deleted, we have to roll back!
					if (!this.bank.deleteLoan(loanId)) {
						// Of course, this one too could fail...
						rollbackLoanTransfer(otherBankStub, resp.loanId);
					}
					
					logger.info(this.bank.id + ": Loan transfer " + loanId + " to " + otherBankStub.id + " successful");
					//return resp.loanId;
					return true;
				}
				else {
					logger.info(this.bank.id + ": Loan transfer failed. " + resp.message);
					throw new Exception("Loan transfer failed. " + resp.message);
				}
			} catch (ExecutionException ee) {
				throw new Exception("Callable threw an execution exception: " + ee.getMessage());
			} catch (InterruptedException e) {
				throw new Exception("Callable was interrupted: " + e.getMessage());
			} catch (TimeoutException e) {
				throw new Exception("Callable transfer loan timed out: " + e.getMessage());
			} finally {
				executor.shutdown();
			}
		}
	}
	
	/**
	 * 
	 * @param otherBankStub
	 * @param loanId
	 * @return
	 */
	private boolean rollbackLoanTransfer(BankReplicaStub otherBankStub, int loanId) {
		return true;
	}

	@Override
	public String getServerName() {
		return this.bank.id;
	}

	@Override
	public BankState getCurrentState() {

		logger.info("Init getting the current bank state");
		
		// Get the list of all loans
		List<shared.data.Loan> loanList = new ArrayList<shared.data.Loan>();
		List<Customer> customerList = new ArrayList<Customer>();

		synchronized(this) {

			// Export the list of loans
			for (String firstLetter : this.bank.loans.keySet()) {
				ThreadSafeHashMap<Integer, Loan> loansByLetter = this.bank.loans.get(firstLetter);
				
				for (Loan loan : loansByLetter.values()) {
					loanList.add(new shared.data.Loan(loan.getId(), loan.getAccountNbr(), loan.getAmount(), loan.getDueDate()));
				}
			}
			
			//Get the list of accounts
			for (String firstLetter : this.bank.accounts.keySet()) {
				ThreadSafeHashMap<Integer, Account> accountsByLetter = this.bank.accounts.get(firstLetter);
				for (Account account : accountsByLetter.values()) {
					customerList.add(new Customer(account.getAccountNbr(), account.getAccountNbr(), account.getFirstName(),
							account.getLastName(), account.getPassword(), account.getEmailAddress(),
							account.getPhoneNbr()));
				}
			}
		}

		logger.info("Finished getting the current bank state");
		
		return new BankState(loanList, customerList, this.bank.nextAccountNbr, this.bank.nextLoanId);
	}

	// ACCOUNT SIGNATURE
	// mine(int accountNbr, String firstName, String lastName, String emailAddress, String phoneNbr,  String password)
	// theirs (int id, int accountNumber, String firstName, String lastName, String password, String email, String phone)
	
	// LOAN SIGNATURE
	// mine (int accountNbr, String emailAddress, long amount, Date dueDate, int id) 
	// theirs (int loanNumber, int customerAccountNumber, long amount, Date dueDate) {

	@Override
	public void setCurrentState(BankState state) {

		logger.info("Init setting the current bank state");
			
		// Get the list of all loans
		List<shared.data.Loan> loanList = state.getLoanList();
		List<Customer> customerList = state.getCustomerList();
		HashMap<Integer, String> mapAccNbrToEmail = new HashMap<Integer, String>();

		synchronized(this) {

			this.bank.resetData();
			
			// Set the list of accounts
			for (Customer customer : customerList) {
				
				// Add a mapping of account number to email address
				mapAccNbrToEmail.put(customer.getAccountNumber(), customer.getEmail());
				
				String firstLetter = customer.getUserName().substring(0, 1).toUpperCase();
				
				Account account = new Account(customer.getAccountNumber(), customer.getFirstName(), customer.getLastName(),
						customer.getEmail(), customer.getPassword(), customer.getPhone());

				ThreadSafeHashMap<Integer, Account> accountList = this.bank.accounts.get(firstLetter);
				accountList.put(customer.getAccountNumber(), account);
			}
			
			// Set the list of loans
			for (shared.data.Loan loan : loanList) {
				
				// Get the email address for this loan/account number
				String email = mapAccNbrToEmail.get(loan.getCustomerAccountNumber());
				String firstLetter = email.substring(0, 1).toUpperCase();

				Loan newLoan = new Loan(loan.getCustomerAccountNumber(), email, loan.getAmount(), loan.getDueDate(), loan.getLoanNumber());

				ThreadSafeHashMap<Integer, Loan> loansList = this.bank.loans.get(firstLetter);
				loansList.put(loan.getLoanNumber(), newLoan);
			}

			this.bank.nextAccountNbr = state.getNextCustomerID();
			this.bank.nextLoanId = state.getNextLoanID();
		}
		
		logger.info("Finished setting the current bank state");
	}
}
