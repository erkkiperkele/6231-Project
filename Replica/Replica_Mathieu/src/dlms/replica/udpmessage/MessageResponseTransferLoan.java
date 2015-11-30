package dlms.replica.udpmessage;

import java.io.Serializable;

/**
 * The UDP response object for the transfer loan operation
 * 
 * @author mat
 *
 */
public class MessageResponseTransferLoan implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int sequenceNbr;
	public int loanId;
	public int accountNbr;
	public boolean status;
	public String message;
	
}
