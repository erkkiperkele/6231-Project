package dlms.frontend;

import java.util.HashMap;

import shared.udp.UDPMessage;

public class ResultSet<T> {

	private boolean hasValidResult = false;
	private T result = null;
	

	HashMap<String, UDPMessage> messages;
	HashMap<String, UDPMessage> results;
	
	
	public void addResult(String key, UDPMessage msg) {
		
		results.put(key, msg);
		
		// Check if there is a valid result
		
	}
	
//	
//	private T getValidResult() {
//		
//		
//		
//	}
	
	
	
	
	//public hasValidResult
}
