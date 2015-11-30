package dlms.replica.client;

import dlms.replica.exception.AppException;

/**
 * This is the customer client application for the DLMS
 * 
 * @author mat
 *
 */
public class CustomerClient extends Client {

	protected static int instances = 1;
	protected final int id;
	
	/**
	 * Constructor
	 */
	public CustomerClient() {
		
		super();
		this.id = instances++;
		this.setUpLogger(this.id);
	}
	
	/**
	 * Request a loan at the given Bank
	 * 
	 * @param bank
	 * @param accountNumber
	 * @param password
	 * @param loanAmount
	 * @return
	 */
	public int getLoan(String bank, int accountNumber, String password, int loanAmount) {

		int newLoanId = -1;
		
		try {
			newLoanId = server.getLoan(bank, accountNumber, password, loanAmount);
			if (newLoanId > 0) {
				logger.info(this.getTextId() + ": Account " + accountNumber + " successfully got a loan of "
						+ loanAmount + " at bank " + bank + " with loanId " + newLoanId);
			} else {
				logger.info(this.getTextId() + ": Account " + accountNumber + " was refused a loan of " + loanAmount
						+ " at bank " + bank);
			}
		} catch (AppException e) {
			logger.info(this.getTextId() + ": " + e.getMessage());
		}
		
		return newLoanId;
	}
	
	/**
	 * Opens an account at the provided bank, provided the account doesn't already exist
	 * 
	 * @param bank
	 * @param firstName
	 * @param lastName
	 * @param emailAddress
	 * @param phoneNumber
	 * @param password
	 * @return
	 */
	public int openAccount(String bank, String firstName, String lastName, String emailAddress, String phoneNumber, String password) {
		
	    logger.info(this.getTextId() + ": Opening an account at " + bank + " for user " + emailAddress);
	    
		int accountNbr = -1;
		
		try {
			accountNbr = server.openAccount(bank, firstName, lastName, emailAddress, phoneNumber, password);
			if (accountNbr > 0) {
				logger.info(this.getTextId() + ": Account " + emailAddress + " created successfully at bank " + bank
						+ " with account number " + accountNbr);
			} else {
				logger.info(this.getTextId() + ": Could not open account " + emailAddress + " at bank " + bank + ".");
			}
		} catch (AppException e) {
			logger.info(this.getTextId() + ": " + e.getMessage());
		}

		return accountNbr;
	}

	/**
	 * Transfer a loan from one bank to another
	 * 
	 * @param bank
	 * @param currentBankId
	 * @param newBankId
	 * @return
	 */
	public int transferLoan(int loanId, String currentBankId, String newBankId) {
		
		int newLoanId = -1;
		
		try {
			newLoanId = server.transferLoan(currentBankId, loanId, currentBankId, newBankId);
			if (newLoanId > 0) {
				logger.info(this.getTextId() + ": Loan transfered successfully to " + newBankId + " New loan ID: "
						+ newLoanId);
			} else {
				logger.info(this.getTextId() + ": Loan transfer to " + newBankId + " failed.");
			}
		} catch (AppException e) {
			logger.info(this.getTextId() + ": " + e.getMessage());
		}
		
		return newLoanId;
	}
	
	/**
	 * Get the text ID of this client e.g. CustomerClient-1
	 * 
	 * @return
	 */
	protected String getTextId() {
		return this.getClass().getSimpleName() + "-" + this.id;
	}
	
}

