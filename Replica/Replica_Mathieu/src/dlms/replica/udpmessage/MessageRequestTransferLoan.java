package dlms.replica.udpmessage;

import java.io.Serializable;

import dlms.replica.model.Account;
import dlms.replica.model.Loan;

/**
 * 
 * @author mat
 *
 */
public class MessageRequestTransferLoan implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int sequenceNbr;
	public Loan loan = null;
	public Account account = null;
	
}
