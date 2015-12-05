package dlms.frontend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.Callable;

/**
 * 
 * 
 * @author mat
 *
 */
public class UdpSend implements Callable<Boolean> {

	private static final int MAX_BUFFER_SIZE = 4096;
	private byte[] payload = new byte[4096];
	private InetSocketAddress remoteAddr;

	/**
	 * 
	 * @param payload
	 * @param remoteAddr
	 */
	public UdpSend(byte[] payload, InetSocketAddress remoteAddr) {
		
		super();
		this.payload = payload;
		this.remoteAddr = remoteAddr;
	}

	@Override
	public Boolean call() throws IOException, SocketException {
		
		DatagramSocket clientSocket = null;
		DatagramPacket outgoingPacket = null;
		InetSocketAddress remoteAddr = new InetSocketAddress(this.remoteAddr.getAddress(), this.remoteAddr.getPort());

		try {
			clientSocket = new DatagramSocket();
			outgoingPacket = new DatagramPacket(payload, payload.length, remoteAddr);
			clientSocket.send(outgoingPacket);
		} finally {
			if (clientSocket != null) {
				clientSocket.close();
			}
		}

		
//		try {
//			
//			while (true) {
//				
//				clientSocket.send(outgoingPacket);
//					
//				// Get back the operation sequence number		
//				incomingPacket = new DatagramPacket(incomingDataBuffer, incomingDataBuffer.length);
//	
//				// Receive the packet
//				try {
//					clientSocket.receive(incomingPacket);
//					
//				} catch (SocketTimeoutException e) {
//					
//					if (retryCntr > 0) {
//						retryCntr--;
//						continue;
//					}
//					
//					if (clientSocket != null) {
//						clientSocket.close();
//					}
//					throw new SocketTimeoutException("Request ack timed out");
//					
//				}
//				
//				break;
//			}
//			
//			// Parse the response data
//
//			byte[] incomingData = new byte[incomingPacket.getLength()];
//			System.arraycopy(incomingPacket.getData(), incomingPacket.getOffset(), incomingData, 0,
//					incomingPacket.getLength());
//			
//			//opSequenceNbr = (long) Serializer.deserialize(incomingData);
//	
//		} finally {
//			if (clientSocket != null)
//				clientSocket.close();
//		}
		return true;
	}

	
//	public Boolean call() throws IOException {
//		
//		DatagramSocket clientSocket = null;
//		InetSocketAddress remoteAddr = new InetSocketAddress(this.remoteAddr.getAddress(), this.remoteAddr.getPort());
//		int retryCntr = 3;
//		final byte[] incomingDataBuffer = new byte[MAX_BUFFER_SIZE];	
//		DatagramPacket incomingPacket = new DatagramPacket(incomingDataBuffer, incomingDataBuffer.length);
//		DatagramPacket outgoingPacket = null;
//		final byte[] outgoingData;
//
//		
//		outgoingData = Serializer.serialize(payload);
//		outgoingPacket = new DatagramPacket(outgoingData, outgoingData.length, remoteAddr);
//		clientSocket = new DatagramSocket();
//		clientSocket.setSoTimeout(1000);
//
//		try {
//			
//			while (true) {
//				
//				clientSocket.send(outgoingPacket);
//					
//				// Get back the operation sequence number		
//				incomingPacket = new DatagramPacket(incomingDataBuffer, incomingDataBuffer.length);
//	
//				// Receive the packet
//				try {
//					clientSocket.receive(incomingPacket);
//					
//				} catch (SocketTimeoutException e) {
//					
//					if (retryCntr > 0) {
//						retryCntr--;
//						continue;
//					}
//					
//					if (clientSocket != null) {
//						clientSocket.close();
//					}
//					throw new SocketTimeoutException("Request ack timed out");
//					
//				}
//				
//				break;
//			}
//			
//			// Parse the response data
//
//			byte[] incomingData = new byte[incomingPacket.getLength()];
//			System.arraycopy(incomingPacket.getData(), incomingPacket.getOffset(), incomingData, 0,
//					incomingPacket.getLength());
//			
//			//opSequenceNbr = (long) Serializer.deserialize(incomingData);
//	
//		} finally {
//			if (clientSocket != null)
//				clientSocket.close();
//		}
//		return null;
//	}
	
}
