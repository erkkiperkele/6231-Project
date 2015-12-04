package dlms.frontend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.logging.Logger;

import dlms.replica.udpmessage.Marshaller;
import shared.udp.IOperationMessage;
import shared.udp.IReplyMessage;
import shared.udp.ReplyMessage;
import shared.util.Constant;

/**
 * This class makes the request to the sequencer and receives the sequence
 * number from it
 * 
 * This is the UDP listener class/thread which receives the operation results
 * from the replicas
 * 
 * @author mat
 * 
 */
public class ReplicaListener extends Thread {

	private static final String FE_HOST = "localhost";
	private static final int FE_PORT = 15000;
	private static final int UDP_PACKET_SIZE = 4096;

	protected Logger logger;
	protected Marshaller<IReplyMessage<Integer>, IOperationMessage> marshaller;

	/**
	 * Constructor
	 * 
	 * @param bank
	 * @param logger
	 * @return 
	 */
	public ReplicaListener(Logger logger) {
		
		super();
		this.logger = logger;
		this.marshaller = new Marshaller<IReplyMessage<Integer>, IOperationMessage>();
	}

	@Override
	public void run() {

		DatagramSocket serverSocket = null;
		InetSocketAddress localAddr = new InetSocketAddress(FE_HOST, FE_PORT);

		try {

			serverSocket = new DatagramSocket(localAddr);
			byte[] receiveData = new byte[UDP_PACKET_SIZE];

			while (true) {

				//
				// LISTENER
				//
				
				logger.info("FrontEnd: Waiting for replica messages");
				
				receiveData = new byte[UDP_PACKET_SIZE];
				final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

				// Wait for the packet
				serverSocket.receive(receivePacket);
				
				logger.info("FrontEnd: Received an operation result message from a replica");
				
				// Received a request. Place it in the receiving data variable and parse it
				byte[] data = new byte[receivePacket.getLength()];
		        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), data, 0, receivePacket.getLength());

		        IOperationMessage opResult = null;
		        
		        try {
		        	opResult = (IOperationMessage) this.marshaller.unmarshal(data);
		        	
				} catch (ClassNotFoundException e1) {
					// Could not unmarshall to the desired class. Keep on running, but log the error
					logger.info("FrontEnd: Failed to unmarshall operation result message. Skipping this packet");
					continue;
				}
	            
	            this.ReplyAck(serverSocket, receivePacket.getAddress(), receivePacket.getPort());
	            
	            // What to do with the response object!
	            
			}

		} catch (final SocketException e) {
			System.exit(1);
		} catch (final IOException e) {
			System.exit(1);
		} finally {if(serverSocket != null) serverSocket.close();}
	}

	/**
	 * Front end UDP responder to the replicas to acknowledge the reception of an operation result
	 */
	private void ReplyAck(DatagramSocket serverSocket, InetAddress remoteAddr, int remotePort) {

		//
		// RESPONDER
		//

		// Create the ACK object message
		ReplyMessage<Integer> replyMsg = new ReplyMessage<Integer>(true, "", 0);
		
        // Prepare the response
		byte[] sendData = new byte[UDP_PACKET_SIZE];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(replyMsg);
			sendData = baos.toByteArray();
			baos.close();
	        oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, remoteAddr, remotePort);
		
		try {
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
