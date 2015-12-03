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

import shared.udp.IOperationMessage;
import shared.udp.ReplyMessage;

/**
 * 
 * @author mat
 * 
 */
public class UdpListener extends Thread {

	private static final String FE_HOST = "localhost";
	private static final int FE_PORT = 15000;
	private static final int MSG_BUF = 4096;

	//protected volatile Bank bank;
	//protected Logger logger;
	
//	/**
//	 * Constructor
//	 * 
//	 * @param bank
//	 * @param logger
//	 */
//	UdpListener(Bank bank, Logger logger) {
//
//		this.bank = bank;
//		this.logger = logger;
//	}

	@Override
	public void run() {

		DatagramSocket serverSocket = null;
		InetSocketAddress localAddr = new InetSocketAddress(FE_HOST, FE_PORT);

		try {

			serverSocket = new DatagramSocket(localAddr);
			byte[] receiveData = new byte[MSG_BUF];

			while (true) {

				//
				// LISTENER
				//
				
				receiveData = new byte[MSG_BUF];
				final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

				// Wait for the packet
				serverSocket.receive(receivePacket);
				
				// Received a request. Parse it.
				byte[] data = new byte[receivePacket.getLength()];
		        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), data, 0, receivePacket.getLength());

				// Extract the receiver's message into the appropriate object
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
	            ObjectInputStream ois = new ObjectInputStream(bais);
	            IOperationMessage obj;

				try {
					obj = (IOperationMessage) ois.readObject();
				} catch (ClassNotFoundException e) {
					continue;
				}
	            bais.close();
	            ois.close();
	            
	            this.ReplyAck(serverSocket, receivePacket.getAddress(), receivePacket.getPort());
	            
	            // What to do with the response object!
	            
	            
	            // Take appropriate action based on the request
//				if (obj instanceof MessageRequestLoanSum) {
//					this.RespondGetLoan((MessageRequestLoanSum) obj, sendData, remoteIpAddress, remotePort, serverSocket);
//				} 
//				else if (obj instanceof MessageRequestTransferLoan) {
//					this.RespondTransferLoan((MessageRequestTransferLoan) obj, sendData, remoteIpAddress, remotePort, serverSocket);
//				} else {
//					continue;
//				}
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
	protected void ReplyAck(DatagramSocket serverSocket, InetAddress remoteAddr, int remotePort) {

		//
		// RESPONDER
		//

		// Create the ACK object message
		ReplyMessage<Integer> replyMsg = new ReplyMessage<Integer>(true, "", 0);
		
        // Prepare the response
		byte[] sendData = new byte[MSG_BUF];
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
