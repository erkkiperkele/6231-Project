package dlms.replica.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
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

import dlms.replica.exception.AppException;
import dlms.replica.exception.ValidationException;
import dlms.replica.model.Account;
import dlms.replica.model.Loan;
import dlms.replica.model.ThreadSafeHashMap;
import dlms.replica.udpmessage.MessageResponseLoanSum;
import dlms.replica.udpmessage.MessageResponseTransferLoan;


/**
 * The bank replica acts as a bank server and proxy for the underlying bank
 * entity
 * 
 * @author mat
 *
 */
public class BankReplica {
	
	private BankReplicaStubGroup group = null;
	private Logger logger = null;
	private Thread udpListenerThread;
	private UdpListener udpListener;
	private volatile Bank bank;
	
	/**
	 * Constructor
	 * 
	 * @param id
	 * @param addr
	 */
	BankReplica(String id, InetSocketAddress addr) {

		this.group = new BankReplicaStubGroup();
		this.bank = new Bank(id, addr);
		
		// Set up the logger
		this.logger = Logger.getLogger(this.bank.getId());  
	    FileHandler fh;  
	    try {
	        fh = new FileHandler(this.bank.getTextId() + "-log.txt");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  
	        logger.info(this.bank.getTextId() + " logger started");  
	    } catch (SecurityException e) {  
	        e.printStackTrace();
	        System.exit(1);
	    } catch (IOException e) {  
	        e.printStackTrace(); 
	        System.exit(1);
	    }

	    // Start the bank's UDP listener
		logger.info(this.bank.getId() + ": Starting UDP Listener");
		udpListener = new UdpListener(this.bank, this.logger);
		udpListenerThread = new Thread(udpListener);
		udpListenerThread.start();
	}

	/**
	 * Constructor
	 * 
	 * @param stub
	 */
	public BankReplica(BankReplicaStub stub) {
		
		this(stub.id, stub.addr);
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
			String password) {
		
		int newAccountNbr = -1;

		logger.info("-------------------------------\n" + this.bank.id
				+ ": Client invoked openAccount(bankId: " + " emailAddress:" + emailAddress + ")");
		
		try {
			newAccountNbr = this.bank.createAccount(firstName, lastName, emailAddress, phoneNumber, password);
		} catch (ValidationException e) {
			return -1; // Something went wrong when trying to create the account
		}
		if (newAccountNbr > 0) {
			logger.info("ReplicaManager: Bank " + this.bank.id + " successfully opened an account for user " + emailAddress
					+ " with account number " + newAccountNbr);
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
	public boolean delayPayment(int loanId, String currentDueDate, String newDueDate) {

		logger.info(
				"-------------------------------\n" + this.bank.getTextId() + ": Client invoked delayPayment(loanId:"
						+ loanId + " currentDate: " + currentDueDate + " newDueDate: " + newDueDate + ")");

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy");
		// Date dateCurrent = null;
		Date dateNew = null;
		Loan loan;
		Object lock;

		try {
			// dateCurrent = dateFormat.parse(currentDueDate);
			dateNew = dateFormat.parse(newDueDate);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		loan = this.bank.getLoan(loanId);
		if (loan == null) {
			logger.info(this.bank.getTextId() + ": Loan id " + loanId + " does not exist");
			return false;
		}
		
		lock = this.bank.getLockObject(loan.getEmailAddress());

		synchronized (lock) {
			
			loan = this.bank.getLoan(loanId);
			if (loan == null) {
				logger.info(this.bank.getTextId() + ": Loan id " + loanId + " does not exist");
				return false;
			}
			// Uncomment when using date validation
			/*if (!loan.getDueDate().equals(dateCurrent)) {
				logger.info(this.bank.getTextId() + ": Loan id " + loanId + " - currentDate argument mismatch");
				return new ServerResponse(false, "", "Loan id " + loanId + " - currentDate argument mismatch");
			}*/
			if (!loan.getDueDate().before(dateNew)) {
				logger.info(this.bank.getTextId() + ": Loan id " + loanId
						+ " - currentDueDate argument must be later than the actual current due date of the loan");
				return false;
			}
			
			loan.setDueDate(dateNew);
		}

		logger.info(this.bank.getTextId() + " loan " + loanId + " successfully delayed");
		return true;
	}
	
	/**
	 * 
	 * @param accountNbr
	 * @param password
	 * @param requestedLoanAmount
	 * @return
	 * @throws AppException 
	 */
	public int getLoan(int accountNbr, String password, int requestedLoanAmount) throws AppException {

		int newLoanId = -1; 
		int externalLoanSum = 0; // The total sum of loans at other banks for accountNbr
		Object lock = null;
		Account account; // The account corresponding to the account number
		ExecutorService pool;
		Set<Future<MessageResponseLoanSum>> set;
		
		logger.info("-------------------------------\n" + this.bank.getTextId() + ": Client invoked getLoan(accountNbr:"
				+ accountNbr + ", password:" + password + ", requestedLoanAmount:" + requestedLoanAmount + ")");

		// We need to get the lock object from the account number, so essentially, 
		// we're testing for account existence
		account = this.bank.getAccount(accountNbr);
		if (account == null) {
			String msg = "Loan refused at bank " + this.bank.getId() + ". Account " + accountNbr + " does not exist.";
			logger.info(this.bank.getTextId() + ": " + msg);
			throw new AppException(msg);
		}

		lock = this.bank.getLockObject(account.getEmailAddress());
	    	
		synchronized (lock) {

			// Test the existence of the account (again, now that we're in
			// the critical section)
			// The account could have gotten deleted just before the
			// synchronized block
			account = this.bank.getAccount(accountNbr);
			if (account == null) {
				String message = "Loan refused at bank " + this.bank.getId() + ". Account " + accountNbr
						+ " does not exist.";
				logger.info(this.bank.getTextId() + ": " + message);
				throw new AppException(message);
			}

			// Validate that passwords match
			if (!account.getPassword().equals(password)) {
				String message = "Loan refused at bank " + this.bank.getId() + ". Invalid credentials.";
				logger.info(this.bank.getTextId() + ": " + message);
				throw new AppException(message);
			}

			// Avoid making UDP requests if the loan amount is already
			// bigger than the credit limit of the local account
			int currentLoanAmount = this.bank.getLoanSum(account.getEmailAddress());
			if (currentLoanAmount + requestedLoanAmount > account.getCreditLimit()) {
				String message = "Loan refused at bank " + this.bank.getId() + ". Local credit limit exceeded";
				logger.info(this.bank.getTextId() + ": " + message);
				throw new AppException(message);
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
							logger.info(this.bank.getTextId() + ": " + message);
							throw new AppException(message);
						} else if (loanSumResponse.status) {
							externalLoanSum += loanSumResponse.loanSum;
						} else {
							String message = "Loan refused at bank " + this.bank.getId() + ". "
									+ loanSumResponse.message;
							logger.info(this.bank.getTextId() + ": " + message);
							throw new AppException(message);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						String message = "Loan request failed for user " + account.getEmailAddress()
								+ ". InterruptedException";
						logger.info(this.bank.getTextId() + ": " + message);
						throw new AppException(message);

					} catch (ExecutionException e) {
						e.printStackTrace();
						String message = "Loan request failed for user " + account.getEmailAddress()
								+ ". ExecutionException";
						logger.info(this.bank.getTextId() + ": " + message);
						throw new AppException(message);
					}
				}

			} finally {
				pool.shutdown();
			}
			
			// Refuse the loan request if the sum of all loans is greater than the credit limit
			if ((requestedLoanAmount + externalLoanSum) > account.getCreditLimit()) {
				String message = "Loan refused at bank " + this.bank.getId() + ". Total credit limit exceeded.";
				logger.info(this.bank.getTextId() + ": " + message);
				throw new AppException(message);
			}
			
			// Loan is approved at this point
			newLoanId = this.bank.createLoan(account.getEmailAddress(), accountNbr, requestedLoanAmount);

			String message = "Loan approved for user " + account.getEmailAddress() + ", amount " + requestedLoanAmount + " at bank " + this.bank.getId() + ".";
			logger.info(this.bank.getTextId() + ": " + message);

			return newLoanId;
		}
	}
	
	/**
	 * Transfers a loan from one bank to another
	 * 
	 * @param loan
	 * @param otherBankId
	 * @return
	 * @throws AppException 
	 */
	public int transferLoan(Loan loan, BankReplicaStub otherBankStub) throws AppException {
		
		Object lock = this.bank.getLockObject(loan.getEmailAddress());
		
		synchronized (lock) {

			// Re-check that the loan still exists
			int loanId = loan.getId();
			loan = this.bank.getLoan(loan.getId());
			if (loan == null) {
				logger.info(this.bank.id + ": Loan transfer " + loanId + " failed. LoanId " + loanId + " does not exist");
				throw new AppException("Loan transfer " + loanId + " failed. LoanId " + loanId + " does not exist");
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
					
					logger.info(this.bank.getTextId() + ": Loan transfer " + loanId + " to " + otherBankStub.id + " successful");
					return resp.loanId;
				}
				else {
					logger.info(this.bank.getTextId() + ": Loan transfer failed. " + resp.message);
					throw new AppException("Loan transfer failed. " + resp.message);
				}
			} catch (ExecutionException ee) {
				throw new AppException("Callable threw an execution exception: " + ee.getMessage());
			} catch (InterruptedException e) {
				throw new AppException("Callable was interrupted: " + e.getMessage());
			} catch (TimeoutException e) {
				throw new AppException("Callable transfer loan timed out: " + e.getMessage());
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
}
