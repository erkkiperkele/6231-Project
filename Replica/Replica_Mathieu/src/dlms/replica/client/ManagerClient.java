package dlms.replica.client;

import dlms.replica.exception.AppException;

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
		this.setUpLogger(this.id);
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
	public boolean delayPayment(String bankId, int loanId, String currentDueDate, String newDueDate) {
		
		try {
			boolean result = server.delayPayment(bankId, loanId, currentDueDate, newDueDate);
			if (result) {
				logger.info(this.getTextId() + ": Delay payment successfully for loan " + loanId);	
				return result;
			}
		} catch (AppException e) {
			System.out.println(e.getMessage());
			logger.info(this.getTextId() + ": AppException. " + e.getMessage());
		}
		
		logger.info(this.getTextId() + ": Delay payment failed for loan " + loanId);
		
		return false;
	}
	
	/**
	 * Prints the customer info of the provided bank
	 * 
	 * @param bank
	 * @return
	 */
	public void printCustomerInfo(String bank) {
		
		try {
			String result = server.printCustomerInfo(bank);
			logger.info(this.getTextId() + ": Bank-" + bank + "\n" + result);
		} catch (AppException e) {
			System.out.println(e.getMessage());
			logger.info(this.getTextId() + ": AppException. " + e.getMessage());
		}
	}

	/**
	 * Get the text ID of this client e.g. ManagerClient-1
	 * 
	 * @return
	 */
	protected String getTextId() {
		return this.getClass().getSimpleName() + "-" + this.id;
	}
	
}
