package dlms.frontend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import shared.data.ServerInfo;
import shared.udp.Serializer;
import shared.udp.UDPMessage;
import shared.util.Constant;
import shared.util.Env;

public class ReplicaManagerListener extends Thread {

//	private static final String FE_HOST = "localhost";
//	private static final int FE_PORT = 15000;
	private static final int UDP_PACKET_SIZE = 4096;

	protected Logger logger;
	private volatile QueuePool opThreadPool = null;

	/**
	 * Constructor
	 * 
	 * @param logger
	 * @return 
	 */
	public ReplicaManagerListener(Logger logger) {
		
		super();
		this.logger = logger;
		this.opThreadPool = opThreadPool;
	}

	public void run() {

		DatagramSocket serverSocket = null;
		InetSocketAddress localAddr = new InetSocketAddress(Constant.FE_TO_RM_LISTENER_HOST, Constant.FE_TO_RM_LISTENER_PORT);

		try {

			serverSocket = new DatagramSocket(localAddr);
			byte[] receiveData = new byte[UDP_PACKET_SIZE];

			while (true) {

				//
				// LISTENER
				//
				
				logger.info("FrontEnd: Waiting for replica manager messages on " + Constant.FE_TO_RM_LISTENER_HOST + ":" + Constant.FE_TO_RM_LISTENER_PORT);
				
				receiveData = new byte[UDP_PACKET_SIZE];
				final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

				// Wait for the packet
				serverSocket.receive(receivePacket);
				
				//logger.info("FrontEnd: Received a UDP message from a replica manager");
				
				// Received a request. Place it in the receiving data variable and parse it
				byte[] data = new byte[receivePacket.getLength()];
		        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), data, 0, receivePacket.getLength());

//		        UDPMessage message = null;
//				try {
//					message = (UDPMessage) Serializer.deserialize(data);
//				} catch (ClassNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//		        // Get the sequence number for this message and add it to the appropriate queue
//		        long sequenceNbr = message.getSequenceNumber();
//		        BlockingQueue<UDPMessage> queue = opThreadPool.get(sequenceNbr);
//		        // If the queue doesn't exist, then this packet is probably a duplicate and late. Just discard it.
//				if (queue == null) {
//					logger.info("FrontEnd: Received a UDP message from a replica with sequence number " + sequenceNbr
//							+ " but no queue exists");
//					continue;
//				}
//
//				// Add the message to the queue and move on
//				try {
//					queue.put(message);
//				} catch (InterruptedException e) {
//					// TODO: Catch this properly
//					e.printStackTrace();
//				}
			}

		} catch (final SocketException e) {
			// TODO: Catch this properly
			System.exit(1);
		} catch (final IOException e) {
			// TODO: Catch this properly
			System.exit(1);
		} finally {if(serverSocket != null) serverSocket.close();}
	}
}
