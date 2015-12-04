package dlms.frontend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

import dlms.corba.AppException;
import dlms.corba.FrontEndPOA;
import shared.udp.IOperationMessage;
import shared.udp.IReplyMessage;
import shared.udp.ReplyMessage;
import shared.udp.UDPMessage;
import shared.udp.message.client.PrintCustomerInfoMessage;

/**
 * The DLMS CORBA front end
 * 
 * @author mat
 *
 */
public class FrontEnd extends FrontEndPOA {

	public static final String SEQ_HOST = "localhost";
	public static final int SEQ_PORT = 5000;
	public static final int MSG_BUF = 4096;
	public static final int MAX_QUEUE_SIZE = 8;
	
	private Logger logger = null;
	private volatile QueuePool opQueuePool = null;
	private volatile BlockingQueue<UDPMessage> queue = null;

	/**
	 * Constructor
	 */
	public FrontEnd(Logger logger, QueuePool opQueuePool) {
		
		super();
		
		this.logger = logger;
		this.opQueuePool = opQueuePool;
		this.queue = new ArrayBlockingQueue<UDPMessage>(MAX_QUEUE_SIZE);
	}

	@Override
	public boolean delayPayment(String bankId, int loanId, String currentDueDate, String newDueDate)
			throws AppException {

		return false;
	}

	@Override
	public String printCustomerInfo(String bankId) throws AppException {

		// Create the operation message
		PrintCustomerInfoMessage msgObj = new PrintCustomerInfoMessage(bankId);
		UDPMessage udpMessage = new UDPMessage(msgObj);
		
		logger.info("FrontEnd: Forwarding printCustomerInfo operation to the sequencer");
		
		// Send the operation message to the sequencer and get the sequence number for the operation
		int sequenceNbr = this.forwardToSequencer(udpMessage);

		// Add this thread's blocking queue to the queue pool for the replica listener thread
		opQueuePool.put(sequenceNbr, this.queue);
		
		logger.info("FrontEnd: Sequencer replied to printCustomerInfo operation with sequence number " + sequenceNbr);
		
		
		// Now we just have to wait for the messages to go around and come back from the replica
		
		
		
		// Create a thread to handle the reception of replica results
//		ReplicaMessageHandler responseThread = new ReplicaMessageHandler(sequenceNbr, OperationType.PrintCustomerInfo);
//		responseThread.start();
		
		
		// Add this thread to the thread pool
		
		// Wait for a the response thread to to get two valid results from the replicas then forward the result to the client
//		synchronized (this) {
//
//			try {
//				responseThread.wait();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
		
//		int r = randomWithRange(1,5);
//		System.out.println("Random: " + r);
//		try {
//			Thread.sleep(r*1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		
//		for (int i = 0; i < 1000000; i++) {
//			int j = i % 2;
//		}
//		
//		long threadId = Thread.currentThread().getId();
//		System.out.println("Thread # " + threadId + " is doing this task");
        
		// clean up the blocking queue from the pool
		opQueuePool.remove(sequenceNbr);
		
		return "Op printCustomerInfo: Sequence Number: " + sequenceNbr++;
	}

//	public static void test() {
//		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
//		 
//		String jvmName = runtimeBean.getName();
//		System.out.println("JVM Name = " + jvmName);
//		long pid = Long.valueOf(jvmName.split("@")[0]);
//		System.out.println("JVM PID  = " + pid);
// 
//		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
// 
//		int peakThreadCount = bean.getPeakThreadCount();
//		System.out.println("Peak Thread Count = " + peakThreadCount);
//	}

//	public int randomWithRange(int min, int max)
//	{
//	   int range = (max - min) + 1;     
//	   return (int)(Math.random() * range) + min;
//	}
	
	@Override
	public int transferLoan(String bankId, int loanId, String currentBankId, String otherBankId) throws AppException {
		
		return 11;
	}

	@Override
	public int openAccount(String bankId, String firstName, String lastName, String emailAddress, String phoneNumber,
			String password) throws AppException {
		
		return 22;
	}

	@Override
	public int getLoan(String bankId, int accountNbr, String password, int loanAmount) throws AppException {
		// TODO Auto-generated method stub
		return 33;
	}

//	/**
//	 * 
//	 * @param orb_val
//	 */
//	public void setORB(ORB orb_val) {
//		this.orb = orb_val;
//	}
	
	/**
	 * Forwards the operation to the sequencer and gets the sequence number of
	 * the operation
	 * @param <T>
	 * 
	 * @param message The operation message to send to the sequencer
	 * @return the sequence number of the operation
	 * @throws AppException 
	 */
	private int forwardToSequencer(UDPMessage message) throws AppException {
		
		return 1;
	}
	
	/**
	 * A generic method for making a UDP response and getting a reply
	 * 
	 * @param requestObj
	 * @return
	 * @throws AppException
	 */
	@SuppressWarnings("unchecked")
	private <T extends Serializable> IReplyMessage<T> makeUdpRequest(IOperationMessage requestObj) throws AppException {

		DatagramSocket clientSocket = null;
		InetSocketAddress remoteAddr = new InetSocketAddress(SEQ_HOST, SEQ_PORT); // The Sequencer IP address

		try {

			//
			// REQUESTING
			//

			// Init data structures
			clientSocket = new DatagramSocket();
			final byte[] receiveData = new byte[MSG_BUF];
			final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos;

			// Serialize the message
			oos = new ObjectOutputStream(baos);
			oos.writeObject(requestObj);
			byte[] sendData = baos.toByteArray();

			final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, remoteAddr);
			clientSocket.send(sendPacket);

			//
			// GETTING RESPONSE
			//
			clientSocket.setSoTimeout(5000);

			try {

				clientSocket.receive(receivePacket);

				// Parse the response data
				byte[] recvData = new byte[receivePacket.getLength()];
				System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), recvData, 0,
						receivePacket.getLength());

				ByteArrayInputStream bais = new ByteArrayInputStream(recvData);
				ObjectInputStream ois;
				ReplyMessage<T> replyMessage = null;

				try {
					ois = new ObjectInputStream(bais);
					replyMessage = (ReplyMessage<T>) ois.readObject();
					bais.close();
					ois.close();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (replyMessage.isSuccessful()) {
					return replyMessage;
				} else {
					throw new AppException(replyMessage.getMessage());
				}

			} catch (final SocketTimeoutException e) {
				throw new AppException(e.getMessage());
			} finally {
				if (clientSocket != null)
					clientSocket.close();
			}

		} catch (final SocketException e) {
			throw new AppException(e.getMessage());
		} catch (final IOException e) {
			throw new AppException(e.getMessage());
		} finally {
			if (clientSocket != null)
				clientSocket.close();
		}
	}
	
}
