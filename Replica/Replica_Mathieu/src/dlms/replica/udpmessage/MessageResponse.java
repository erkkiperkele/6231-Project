package dlms.replica.udpmessage;

import java.io.Serializable;

/**
 * A generic UDP message response indicating success/failure and a verbal description of the status
 * 
 * @author mat
 *
 */
public class MessageResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int sequenceNbr;
	public boolean status;
	public String message;
	
}
