package dlms.frontend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.logging.Logger;

import shared.udp.Serializer;
import shared.util.Constant;

/**
 * Listens for messages from the replica managers
 * 
 * @author mat
 *
 */
public class ReplicaManagerListener extends Thread {

	private static final int MAX_DATAGRAM_SIZE = 4096;
	protected Logger logger;
	private volatile FrontEndState feState = FrontEndState.RUNNING;
	
	/**
	 * Constructor
	 * 
	 * @param logger
	 * @return 
	 */
	public ReplicaManagerListener(Logger logger) {
		
		super();
		this.logger = logger;
	}

	public void run() {

		DatagramSocket serverSocket = null;
		InetSocketAddress localAddr = new InetSocketAddress(Constant.FE_TO_RM_LISTENER_HOST, Constant.FE_TO_RM_LISTENER_PORT);

		logger.info("FrontEnd: Waiting for replica manager messages on " + Constant.FE_TO_RM_LISTENER_HOST + ":" + Constant.FE_TO_RM_LISTENER_PORT);
		
		try {

			serverSocket = new DatagramSocket(localAddr);
			byte[] receiveData = new byte[MAX_DATAGRAM_SIZE];

			while (true) {

				//
				// LISTENER
				//
				
				receiveData = new byte[MAX_DATAGRAM_SIZE];
				final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

				// Wait for the packet
				serverSocket.receive(receivePacket);
				
				// Received a request. Place it in the receiving data variable and parse it
				byte[] data = new byte[receivePacket.getLength()];
		        String cmd = null;
		        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), data, 0, receivePacket.getLength());
		        
				final InetAddress remoteAddress = receivePacket.getAddress();
				final int remotePort = receivePacket.getPort();
		        
				// Unmarshall the message
		        try {
					cmd = (String) Serializer.deserialize(data);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
		        
		        if (cmd.equals(Constant.STOP_FE)) {
		        	feState = FrontEndState.STALLED;
		        	logger.info("FrontEnd: Received command to stop the FE");
		        	this.sendAckResponse(remoteAddress, remotePort, Constant.FE_STOPPED);
		        	
		        }
		        else if (cmd.equals(Constant.START_FE)) {
		        	feState = FrontEndState.RUNNING;
		        	logger.info("FrontEnd: Received command to start the FE");
		        	this.sendAckResponse(remoteAddress, remotePort, Constant.FE_STARTED);
		        	
		        }
		        else {
		        	logger.info("FrontEnd: Received unknown command in RM Listener. Discarding it.");
		        }
			}

		} catch (final SocketException e) {
			// TODO: Catch this properly
			System.exit(1);
		} catch (final IOException e) {
			// TODO: Catch this properly
			System.exit(1);
		} finally {if(serverSocket != null) serverSocket.close();}
	}
	
	/**
	 * Sends a state change ACK to the RMs
	 * 
	 * @param remoteAddress
	 * @param remotePort
	 * @param cmd
	 */
	public void sendAckResponse(InetAddress remoteAddress, int remotePort, String cmd) {
		
		// Serialize the message
		byte[] msg = new byte[MAX_DATAGRAM_SIZE];
		try {
			msg = Serializer.serialize(cmd);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		InetSocketAddress remoteAddr = new InetSocketAddress(remoteAddress, remotePort);
		UdpSend sender = new UdpSend(msg, remoteAddr);
		try {
			sender.call();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//
	// Getters and setters
	//
	
	public synchronized FrontEndState getFeState() {
		return feState;
	}

	public synchronized void setFeState(FrontEndState feState) {
		this.feState = feState;
	}
}
