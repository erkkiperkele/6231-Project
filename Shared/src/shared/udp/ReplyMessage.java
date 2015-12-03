package shared.udp;

import java.io.Serializable;

/**
 * A high level class for representing the response message to a UDP request
 * 
 * @author mat
 *
 */
@SuppressWarnings("serial")
public class ReplyMessage<T extends Serializable> implements Serializable, IReplyMessage<T> {

	private boolean isSuccessful;
	private String message;
	private T result;
	
	/**
	 * Constructor
	 * 
	 * @param isMessageSuccessful
	 * @param message
	 */
	public ReplyMessage(boolean isSuccessful, String message, T result) {
		super();
		
		this.isSuccessful = isSuccessful;
		this.message = message;
		this.result = result;
	}
	
	//
	// Getters and setters
	//

	@Override
	public boolean isSuccessful() {
		return isSuccessful;
	}

	@Override
	public T getResult() {
		return result;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
