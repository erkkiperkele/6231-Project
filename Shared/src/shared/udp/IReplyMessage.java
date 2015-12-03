package shared.udp;

import java.io.Serializable;

/**
 * Generic interface for UDP reply messages
 * 
 * @author mat
 *
 * @param <T> A serializable return type
 */
public interface IReplyMessage<T> extends Serializable {

	/**
	 * Whether the request operation was completed successfully or not
	 * 
	 * @return
	 */
	public boolean isSuccessful();

	/**
	 * The custom result value of the request
	 * 
	 * @return
	 */
	public T getResult();

	/**
	 * Any message to accompany the reply result (e.g. error, exception,
	 * success, info, etc)
	 * 
	 * @return
	 */
	public String getMessage();
}
