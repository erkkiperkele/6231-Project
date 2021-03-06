package shared.udp;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

@SuppressWarnings("serial")
public class UDPMessage implements Serializable {

    private IOperationMessage message;

    private boolean isClientMessage;
    private long sequenceNumber = -1;

    private boolean isDestinationOverwritten = false;
    private String overwriteDestinationIP = null;
    private int overwriteDestinationPort = -1;

    /**
     * Communication received from FrontEnd
     * Need the IP and Port of the client connection for the response
     * @param message
     */
    public UDPMessage(IOperationMessage message) {
        this.message = message;
        isClientMessage = false;
    }

    public IOperationMessage getMessage() {
        return message;
    }

    public OperationType getOperation() {
        return message.getOperationType();
    }
    
    /**
     * Overwrite the message being replied to a new destination
     * @param ip
     * @param port
     */
    public void overwriteDestination(String ip, int port)
    {
        isDestinationOverwritten = true;
        overwriteDestinationIP = ip;
        overwriteDestinationPort = port;
    }

    /**
     * Apply the overwritten address to the original datagram used to reply
     * @param request
     */
	public void executeAddressOverwrite(DatagramPacket request) 
	{
		if(isDestinationOverwritten)
		{
			try
			{
				InetAddress aHost = InetAddress.getByName(this.overwriteDestinationIP);
		        request.setAddress(aHost);
		        request.setPort(this.overwriteDestinationPort);
			}
			catch (UnknownHostException e)
			{
				System.out.println("AddressOverwrite Failed: " + e.getMessage());
			}
		}
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	/**
	 * Message is from the client passing by the 
	 * front-end to the sequence, then replica, 
	 * back to front-end and finally back to the customer
	 * 
	 * If the value is false, it's a packet between server
	 * @return if it's a message coming from the client
	 */
	public boolean isClientMessage() {
		return isClientMessage;
	}
}
