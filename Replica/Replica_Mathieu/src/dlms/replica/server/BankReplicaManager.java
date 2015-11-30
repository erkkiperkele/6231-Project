package dlms.replica.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import dlms.replica.exception.AppException;
import dlms.replica.exception.ValidationException;
import dlms.replica.model.Account;
import dlms.replica.model.Loan;

/**
 * The bank replica manager manages the bank server, from creation, restoration,
 * synchronization. It acts as a proxy to the banks and is an endpoint to the
 * web services
 * 
 * @author mat
 *
 */
@WebService(endpointInterface = "ca.primat.comp6231a3.server.IBankServer")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public class BankReplicaManager implements IBankServer {

	private HashMap<String, BankReplica> bankReplicas;
	private BankReplicaStubGroup replicaGroup;
	private Logger logger = null;
	public HashMap<String, Object> locks;

	/**
	 * Constructor
	 */
	public BankReplicaManager(BankReplicaStubGroup replicaGroup) {

		super();
		
		this.bankReplicas = new HashMap<String, BankReplica>();
		this.locks = new HashMap<String, Object>();
		this.replicaGroup = replicaGroup;
		this.createBanks();
		
		// Initialize the object locks
		char ch;
		for (ch = 'A'; ch <= 'Z'; ++ch) {
			final Object obj = new Object();
			this.locks.put(String.valueOf(ch), obj); 
		}
		
		// Set up the logger
		this.logger = Logger.getLogger("BankReplicaManager");  
	    FileHandler fh;  
	    try {
	        fh = new FileHandler("BankReplicaManager-log.txt");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  
	        logger.info("BankReplicaManager logger started");  
	    } catch (SecurityException e) {  
	        e.printStackTrace();
	        System.exit(1);
	    } catch (IOException e) {  
	        e.printStackTrace(); 
	        System.exit(1);
	    }
	}

	/**
	 * Delay a loan payment deadline date
	 * @throws AppException 
	 */
	@Override
	@WebMethod
	public boolean delayPayment(String bankId, int loanId, String currentDueDate, String newDueDate) throws AppException {

		logger.info("-------------------------------\nReplicaManager: Client invoked delayPayment(loanId:" + loanId
				+ " currentDate: " + currentDueDate + " newDueDate: " + newDueDate + ")");
		
		BankReplica bankReplica = bankReplicas.get(bankId);
		if (bankReplica == null) {
			logger.info("BankReplicaManager: Bank " + bankId + " doesn't exist.");
			throw new AppException("Bank " + bankId + " doesn't exist.");
		}
		
		return bankReplica.delayPayment(loanId, currentDueDate, newDueDate);
	}
	
	/**
	 * Get a loan
	 */
	@Override
	@WebMethod
	public int getLoan(String bankId, int accountNbr, String password, int requestedLoanAmount) throws AppException {
		
		int newLoanId = -1;
		
		logger.info("-------------------------------\nBankReplicaManager: Client invoked getLoan(bankId: " + bankId
				+ " accountNbr:" + accountNbr + ", password:" + password + ", requestedLoanAmount:" + requestedLoanAmount
				+ ")");
		
		BankReplica bankReplica = bankReplicas.get(bankId);
		if (bankReplica == null) {
			logger.info("BankReplicaManager: Bank " + bankId + " doesn't exist.");
			throw new AppException("Bank " + bankId + " doesn't exist.");
		}
		
		Account account = bankReplica.getBank().getAccount(accountNbr);
		if (account == null) {
			logger.info("BankReplicaManager: Account " + accountNbr + " does not exist");
			throw new AppException("Account " + accountNbr + " does not exist");
		}

		Object bankLock = this.getLockObject(account.getEmailAddress());

		synchronized (bankLock) {
			newLoanId = bankReplica.getLoan(accountNbr, password, requestedLoanAmount);
		}

		return newLoanId;
	}

	/**
	 * Open a bank account
	 * @throws AppException 
	 */
	@Override
	@WebMethod
	public int openAccount(String bankId, String firstName, String lastName, String emailAddress, 
			String phoneNumber, String password) throws AppException {

		logger.info("-------------------------------\n" + "ReplicaManager: Client invoked openAccount(bankId: " + bankId
				+ " emailAddress:" + emailAddress + ")");

		BankReplica bankReplica = bankReplicas.get(bankId);
		if (bankReplica == null) {
			return -1; // Bank doesn't exist
		}
		
		try {
			return bankReplica.getBank().createAccount(firstName, lastName, emailAddress, phoneNumber, password);
		} catch (ValidationException e) {
			throw new AppException(e.getMessage());
		}
	}

	/**
	 * Returns a dump of customer data as a readable string
	 */
	@Override
	@WebMethod
	public String printCustomerInfo(String bankId) throws AppException {
		
		BankReplica bankReplica = bankReplicas.get(bankId);
		if (bankReplica == null) {
			logger.info(bankId + ": Bank " + bankId + " doesn't exist.");
			throw new AppException("Bank " + bankId + " doesn't exist.");
		}
		
		return bankReplica.printCustomerInfo();
	}

	@Override
	@WebMethod
	public int transferLoan(String bankId, int loanId, String currentBankId, String otherBankId) throws AppException {

		Object bankLock;
		
		logger.info("-------------------------------\nBankReplicaManager: Client invoked transferLoan(bankId: " + bankId
				+ " loanId:" + loanId + ", currentBankId:" + currentBankId + ", otherBankId:" + otherBankId + ")");
		
		BankReplica bankReplica = bankReplicas.get(bankId);
		if (bankReplica == null) {
			logger.info(bankId + ": Bank " + bankId + " doesn't exist.");
			throw new AppException("Bank " + bankId + " doesn't exist.");
		}
		
		Loan loan = bankReplica.getBank().getLoan(loanId);
		if (loan == null) {
			logger.info(bankReplica.getBank().id + ": Loan transfer " + loanId + " failed. LoanId " + loanId + " does not exist");
			throw new AppException("Loan transfer " + loanId + " failed. LoanId " + loanId + " does not exist");
		}
		
		bankLock = this.getLockObject(loan.getEmailAddress());

		synchronized (bankLock) {
			return bankReplica.transferLoan(loan, replicaGroup.get(otherBankId));
		}
	}
	
	/**
	 * Create a group of replica stubs and share it amongst the replicas
	 */
	private void createBanks() {

		// Create the replicas
		for (BankReplicaStub stub : replicaGroup.values()) {
			
			BankReplica bankReplica = new BankReplica(replicaGroup.get(stub.id));
			this.bankReplicas.put(stub.id, bankReplica);
			
			// Banks need to have knowledge of one another to accommodate some operations
			bankReplica.setGroup(this.replicaGroup);
		}
	}

	/**
	 * Gets the "letter" lock, used to lock the 2nd level HashMaps of accounts and loans
	 * 
	 * @param firstLetter
	 * @return
	 */
	private Object getLockObject(String firstLetter) {
		firstLetter = firstLetter.substring(0, 1).toUpperCase();
		return locks.get(firstLetter);
	}
}
