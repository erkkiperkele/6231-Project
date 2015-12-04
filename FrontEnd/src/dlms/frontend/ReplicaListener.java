package dlms.frontend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import shared.udp.Serializer;
import shared.udp.UDPMessage;

/**
 * This is the UDP listener class/thread which receives the operation results
 * from the replicas and makes it available to the thread corresponding to the
 * original request from the client
 * 
 * @author mat
 * 
 */
public class ReplicaListener extends Thread {

	private static final String FE_HOST = "localhost";
	private static final int FE_PORT = 15000;
	private static final int UDP_PACKET_SIZE = 4096;

	protected Logger logger;
	private volatile QueuePool opThreadPool = null;

	/**
	 * Constructor
	 * 
	 * @param bank
	 * @param logger
	 * @return 
	 */
	public ReplicaListener(Logger logger, QueuePool opThreadPool) {
		
		super();
		this.logger = logger;
		this.opThreadPool = opThreadPool;
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
				
				logger.info("FrontEnd: Received a UDP message from a replica");
				
				// Received a request. Place it in the receiving data variable and parse it
				byte[] data = new byte[receivePacket.getLength()];
		        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), data, 0, receivePacket.getLength());

		        UDPMessage message = null;
				try {
					message = (UDPMessage) Serializer.deserialize(data);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		        // Get the sequence number for this message and add it to the appropriate queue
		        long sequenceNbr = message.getSequenceNumber();
		        BlockingQueue<UDPMessage> queue = opThreadPool.get(sequenceNbr);
		        // If the queue doesn't exist, then this packet is probably a duplicate and late. Just discard it.
				if (queue == null) {
					logger.info("FrontEnd: Received a UDP message from a replica with sequence number " + sequenceNbr
							+ " but no queue exists");
					continue;
				}

				// Add the message to the queue and move on
				try {
					queue.put(message);
				} catch (InterruptedException e) {
					// TODO: Catch this properly
					e.printStackTrace();
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
	 * Front end UDP responder to the replicas to acknowledge the reception of an operation result
	 */
//	private void ReplyAck(DatagramSocket serverSocket, InetAddress remoteAddr, int remotePort) {
//
//		//
//		// RESPONDER
//		//
//
//		// Create the ACK object message
//		ReplyMessage<Integer> replyMsg = new ReplyMessage<Integer>(true, "", 0);
//		
//        // Prepare the response
//		byte[] sendData = new byte[UDP_PACKET_SIZE];
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ObjectOutputStream oos;
//		try {
//			oos = new ObjectOutputStream(baos);
//			oos.writeObject(replyMsg);
//			sendData = baos.toByteArray();
//			baos.close();
//	        oos.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return;
//		}
//
//		final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, remoteAddr, remotePort);
//		
//		try {
//			serverSocket.send(sendPacket);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
