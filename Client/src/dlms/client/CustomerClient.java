package dlms.client;

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

		return 0;
	}
	
	/**
	 * Opens an account at the provided bank, assuming the account doesn't already exist
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

		return 0;
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

		return 0;
	}
	
	/**
	 * Get the text ID of this client e.g. CustomerClient-1
	 * 
	 * @return
	 */
	public String getTextId() {
		
		return this.getClass().getSimpleName() + "-" + this.id;
	}
	
}

