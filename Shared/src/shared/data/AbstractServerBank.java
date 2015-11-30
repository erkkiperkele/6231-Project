package shared.data;

import java.util.Date;

public abstract class AbstractServerBank {

	/**
	 * @return the name
	 */
	public abstract String getServerName();

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
	public abstract int openAccount(String firstName, String lastName, String emailAddress, String phoneNumber, String password) throws Exception;

	/**
	 * getLoan
	 * 
	 * @param accountNumber
	 * @param password
	 * @param loanAmount
	 * @return
	 * @throws Exception
	 */
	public abstract int getLoan(int accountNumber, String password, long loanAmount) throws Exception;

	/**
	 * delayPayment
	 * 
	 * @param loanID
	 * @param currentDueDate
	 * @param newDueDate
	 * @return
	 * @throws Exception
	 */
	public abstract boolean delayPayment(int loanID, Date currentDueDate, Date newDueDate) throws Exception;

	/**
	 * printCustomerInfo
	 * 
	 * @return bank with all user and loan informations
	 */
	public abstract String printCustomerInfo();

	/**
	 * transferLoan
	 * 
	 * @param loanID
	 * @param currentBank
	 * @param otherBank
	 * @return
	 * @throws Exception
	 */
	public abstract boolean transferLoan(int loanID, String otherBank) throws Exception;
}
