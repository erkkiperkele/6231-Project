package dlms.client;

/**
 * This is the manager client application for the DLMS
 * 
 * @author mat
 * 
 */
public class ManagerClient extends Client {

	protected static int instances = 1;
	protected final int id;
	
	/**
	 * Constructor
	 */
	public ManagerClient() {
		
		super();
		this.id = instances++;
	}
	
	/**
	 * Delays a payment on a loan
	 * 
	 * @param bank
	 * @param loanId
	 * @param currentDueDate
	 * @param NewDueDate
	 * @return
	 */
	public Boolean delayPayment(String bank, int loanId, String currentDueDate, String newDueDate) {

		return false;
	}
	
	/**
	 * Prints the customer info of the provided bank
	 * 
	 * @param bank
	 * @return
	 */
	public void printCustomerInfo(String bank) {

	}

	/**
	 * Get the text ID of this client e.g. ManagerClient-1
	 * 
	 * @return
	 */
	public String getTextId() {
		
		return this.getClass().getSimpleName() + "-" + this.id;
	}
}
