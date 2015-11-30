package dlms.replica.server;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import dlms.replica.exception.AppException;  

/**
 * The interface for the web service
 * 
 * @author mat
 *
 */
@WebService(endpointInterface = "ca.primat.comp6231a3.server.IBankServer")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface IBankServer {

	/**
	 * Delays the due date for payment of a loan
	 * 
	 * @param loanId
	 * @param currentDueDate
	 * @param newDueDate
	 * @return
	 * @throws AppException 
	 */
	@WebMethod
	public boolean delayPayment(
			@WebParam(name = "bankId") String bankId, 
			@WebParam(name = "loanId") int loanId,
			@WebParam(name = "currentDueDate") String currentDueDate, 
			@WebParam(name = "newDueDate") String newDueDate) throws AppException;

	/**
	 * Returns the customer info as a string, for a given bank
	 * 
	 * @param bankId
	 * @return
	 * @throws AppException 
	 */
	@WebMethod
	public String printCustomerInfo(@WebParam(name = "bankId") String bankId) throws AppException;

	/**
	 * Transfers a loan from one bank to another
	 * 
	 * @param bankId
	 * @param loanId
	 * @param currentBankId
	 * @param otherBankId
	 * @return
	 * @throws AppException 
	 */
	@WebMethod
	public int transferLoan(
			@WebParam(name = "bankId") String bankId,
			@WebParam(name = "loanId") int loanId,
			@WebParam(name = "currentBankId") String currentBankId,
			@WebParam(name = "otherBankId") String otherBankId) throws AppException;

	/**
	 * Opens a new account at bank "bankId" for user "emailAddress"
	 * 
	 * @param bankId
	 * @param firstName
	 * @param lastName
	 * @param emailAddress
	 * @param phoneNumber
	 * @param password
	 * @return
	 * @throws AppException 
	 */
	@WebMethod
	public int openAccount(
			@WebParam(name = "bankId") String bankId,
			@WebParam(name = "firstName") String firstName,
			@WebParam(name = "lastName") String lastName,
			@WebParam(name = "emailAddress") String emailAddress,
			@WebParam(name = "phoneNumber") String phoneNumber,
			@WebParam(name = "password")  String password) throws AppException;

	/**
	 * Requests a loan at a bank for a given account
	 * 
	 * @param bankId
	 * @param accountNbr
	 * @param password
	 * @param loanAmount
	 * @return
	 * @throws AppException 
	 */
	@WebMethod
	public int getLoan(
			@WebParam(name = "bankId") String bankId, 
			@WebParam(name = "accountNbr") int accountNbr,
			@WebParam(name = "password") String password,
			@WebParam(name = "loanAmount") int loanAmount) throws AppException;
	
}