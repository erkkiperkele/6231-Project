package dlms.replica.udpmessage;

import java.io.Serializable;

/**
 * A message object representing a loan request
 * 
 * @author mat
 *
 */
public class MessageRequestLoanSum implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int sequenceNbr;
	public String emailAddress;
	
}
