package dlms.replica.udpmessage;

import java.io.Serializable;

/**
 * A message object representing a loan request response
 * 
 * @author mat
 *
 */
public class MessageResponseLoanSum implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int sequenceNbr;
	public int loanSum;
	public String emailAddress;
	public String message;
	public boolean status;
}
