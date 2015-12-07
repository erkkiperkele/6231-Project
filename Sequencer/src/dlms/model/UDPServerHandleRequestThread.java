package dlms.model;

import shared.udp.UDPMessage;
import shared.util.Env;

/**
 * @author Pascal Tozzi 27664850
 * UDPServerHandleRequestThread
 * Process all message for the sequencer
 */
public class UDPServerHandleRequestThread extends shared.udp.UDPServerHandleRequestThread
{
	private static long sequencerNumber = 0;
	
	/**
	 * Method can be overwritten for FE, Sequencer, Replica, ReplicaManager
	 * @param udpMessage
	 */
	protected void processRequest(UDPMessage udpMessage)
	{
		udpMessage.setSequenceNumber(incrementCount());

		// Forward message to all replica with override to send back to FrontEnd
		// Otherwise the message will be sent back to the sequencer
		udpMessage.overwriteDestination(Env.getFrontEndServerInfo().getIpAddress(), Env.getFrontEndServerInfo().getPort());
		
		int nCount = this.sendToAll(udpMessage);
		System.out.println("Message was forward to " 
				+ nCount + " hosts with sequence number " 
				+ udpMessage.getSequenceNumber());
	}
	
	/**
	 * Since this is an asynchronous system,
	 * we increment the sequence number with synchronized
	 * @return the current sequencer number
	 */
    public static synchronized long incrementCount() {
    	sequencerNumber++;
    	return sequencerNumber;
    }
}
