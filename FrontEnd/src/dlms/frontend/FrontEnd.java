package dlms.frontend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import dlms.corba.AppException;
import dlms.corba.FrontEndPOA;
import shared.udp.FailedProcessMessage;
import shared.udp.IOperationMessage;
import shared.udp.Serializer;
import shared.udp.UDPMessage;
import shared.udp.message.client.PrintCustomerInfoMessage;

/**
 * The DLMS CORBA front end
 * 
 * @author mat
 *
 */
public class FrontEnd extends FrontEndPOA {

	// These should be coming from a config
	public static final String SEQ_HOST = "localhost";
	public static final int SEQ_PORT = 5000;
	public static final int MSG_BUF = 4096;
	public static final int MAX_QUEUE_SIZE = 8;
	
	private Logger logger = null;
	private volatile QueuePool opQueuePool = null;
	private volatile BlockingQueue<UDPMessage> queue = null;
	
	private ProcessStubGroup replicaManagerGroup;
	private ProcessStub sequencerStub;
	
	/**
	 * Constructor
	 */
	public FrontEnd(Logger logger, QueuePool opQueuePool) {
		
		super();
		
		this.logger = logger;
		this.opQueuePool = opQueuePool;
		this.queue = new ArrayBlockingQueue<UDPMessage>(MAX_QUEUE_SIZE);

		// Set up the config of processes to which this component must connect
		this.replicaManagerGroup = new ProcessStubGroup();
		this.replicaManagerGroup.put("rm1", new ProcessStub("rm1", new InetSocketAddress("localhost", 21000)));
		this.replicaManagerGroup.put("rm2", new ProcessStub("rm2", new InetSocketAddress("localhost", 22000)));
		this.replicaManagerGroup.put("rm3", new ProcessStub("rm3", new InetSocketAddress("localhost", 23000)));
		this.replicaManagerGroup.put("rm4", new ProcessStub("rm4", new InetSocketAddress("localhost", 24000)));
		
		ProcessStub sequencerStub = new ProcessStub("sequencer", new InetSocketAddress("localhost", 5000));
	}

	@Override
	public boolean delayPayment(String bankId, int loanId, String currentDueDate, String newDueDate)
			throws AppException {

		return false;
	}

	@Override
	public String printCustomerInfo(String bankId) throws AppException {

		logger.info("FrontEnd: Client invoked printCustomerInfo(" + bankId + ")");
		
		// Create the operation message and send it to the sequencer. the
		// sequencer will reply with the sequence number
		PrintCustomerInfoMessage opMessage = new PrintCustomerInfoMessage(bankId);
		long sequenceNbr = 0;
		
		try {
			sequenceNbr = this.forwardToSequencer(opMessage);
		} catch (AppException e) {
			logger.info("FrontEnd: " + e.getMessage());
			throw e;
		}

		// Add this thread's blocking queue to the queue pool for the replica
		// listener thread
		opQueuePool.put(sequenceNbr, this.queue);
		
		logger.info("FrontEnd: Sequencer replied to printCustomerInfo operation with sequence number " + sequenceNbr);

		// Now we just have to wait for the messages to go around and come back from the replica
		
		//flagProcessFailure(String BankId, long opSequenceNbr)
		
		
		
		
		
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

	/**
	 * Forwards the operation to the sequencer and gets the sequence number of
	 * the operation
	 * 
	 * @param opMessage
	 * @return
	 * @throws AppException
	 */
	private synchronized long forwardToSequencer(IOperationMessage opMessage) throws AppException {

		UDPMessage udpMessage = new UDPMessage(opMessage);
		DatagramSocket clientSocket = null;
		InetSocketAddress remoteAddr = new InetSocketAddress(SEQ_HOST, SEQ_PORT); // The Sequencer IP address
		long opSequenceNbr = 0;
		int retryCntr = 3;
		final byte[] incomingDataBuffer = new byte[MSG_BUF];	
		DatagramPacket incomingPacket = new DatagramPacket(incomingDataBuffer, incomingDataBuffer.length);
		DatagramPacket outgoingPacket = null;
		final byte[] outgoingData;
		
		try {
			outgoingData = Serializer.serialize(udpMessage);
			outgoingPacket = new DatagramPacket(outgoingData, outgoingData.length, remoteAddr);

		} catch (final IOException e) {
			throw new AppException(e.getMessage());
		}
		
		try {
			clientSocket = new DatagramSocket();
			clientSocket.setSoTimeout(1000);
		} catch (SocketException e) {
			if (clientSocket != null) {
				clientSocket.close();
			}
			throw new AppException(e.getMessage());
		}

		while (true) {
			try {
				clientSocket.send(outgoingPacket);
			} catch (IOException e) {
				if (clientSocket != null) {
					clientSocket.close();
				}
				throw new AppException(e.getMessage());
			}
		
			// Get back the operation sequence number		
			incomingPacket = new DatagramPacket(incomingDataBuffer, incomingDataBuffer.length);

			// Receive the packet
			try {
				clientSocket.receive(incomingPacket);
				
			} catch (SocketTimeoutException e) {
				
				if (retryCntr > 0) {
					retryCntr--;
					continue;
				}
				
				if (clientSocket != null) {
					clientSocket.close();
				}
				throw new AppException("FrontEnd: Sequencer ack timed out");
				
			} catch (SocketException e) {
				if (clientSocket != null) {
					clientSocket.close();
				}
				throw new AppException(e.getMessage());
				
			} catch (IOException e) {
				if (clientSocket != null) {
					clientSocket.close();
				}
				throw new AppException(e.getMessage());
			}
			
			break;
		}
		
		// Parse the response data
		try {

			byte[] incomingData = new byte[incomingPacket.getLength()];
			System.arraycopy(incomingPacket.getData(), incomingPacket.getOffset(), incomingData, 0,
					incomingPacket.getLength());
			opSequenceNbr = (long) Serializer.deserialize(incomingData);
	
		} catch (ClassNotFoundException | IOException e) {
			if (clientSocket != null) {
				clientSocket.close();
			}
			throw new AppException(e.getMessage());
			
		} finally {
			if (clientSocket != null)
				clientSocket.close();
		}
		
		return opSequenceNbr;
	}
	
	/**
	 * Sends a message to each replica manager saying that a replica failed
	 * 
	 * @param BankId
	 * @param opSequenceNbr
	 */
	private void flagProcessFailure(String BankId, long opSequenceNbr) {
		
		ExecutorService pool;
		Set<Future<Boolean>> set;
		
		// Prepare the threads to call other banks to get the loan sum for this account
		pool = Executors.newFixedThreadPool(this.replicaManagerGroup.size());
	    set = new HashSet<Future<Boolean>>();
	    
	    FailedProcessMessage udpMessage = new FailedProcessMessage(BankId, opSequenceNbr);
	    byte[] outgoingData;
		try {
			outgoingData = Serializer.serialize(udpMessage);
		} catch (IOException e1) {
			logger.info("FrontEnd: Unable to serialize UDP message to replica managers");
			return;
		}

		try {

			// Get the loan sum for all banks and approve or not the new loan
			for (ProcessStub rmStub : this.replicaManagerGroup.values()) {
				Callable<Boolean> callable = new UdpSend(outgoingData, rmStub.addr);
				logger.info("FrontEnd: Sending failed process message to replica manager " + rmStub.id);
				Future<Boolean> future = pool.submit(callable);
				set.add(future);
			}

			for (Future<Boolean> future : set) {

				try {
					//Boolean result = future.get();
					future.get();
				} catch (ExecutionException | InterruptedException e) {
					logger.info("FrontEnd: Exception in sending message to replica managers. "  + e.getCause().getMessage());
					//throw e.getCause();
				}
			}

		} finally {
			pool.shutdown();
		}
	}

	/**
	 * Sends a message to each replica manager saying that a replica result was different than others
	 * 
	 * @param BankId
	 * @param opSequenceNbr
	 */
	private void flagProcessBug(String BankId, long opSequenceNbr) {
		
		ExecutorService pool;
		Set<Future<Boolean>> set;
		
		// Prepare the threads to call other banks to get the loan sum for this account
		pool = Executors.newFixedThreadPool(this.replicaManagerGroup.size());
	    set = new HashSet<Future<Boolean>>();
	    
	    FailedProcessMessage udpMessage = new FailedProcessMessage(BankId, opSequenceNbr);
	    byte[] outgoingData;
		try {
			outgoingData = Serializer.serialize(udpMessage);
		} catch (IOException e1) {
			logger.info("FrontEnd: Unable to serialize UDP message to replica managers");
			return;
		}

		try {

			// Get the loan sum for all banks and approve or not the new loan
			for (ProcessStub rmStub : this.replicaManagerGroup.values()) {
				Callable<Boolean> callable = new UdpSend(outgoingData, rmStub.addr);
				logger.info("FrontEnd: Sending failed process message to replica manager " + rmStub.id);
				Future<Boolean> future = pool.submit(callable);
				set.add(future);
			}

			for (Future<Boolean> future : set) {

				try {
					//Boolean result = future.get();
					future.get();
				} catch (ExecutionException | InterruptedException e) {
					logger.info("FrontEnd: Exception in sending message to replica managers. "  + e.getCause().getMessage());
					//throw e.getCause();
				}
			}

		} finally {
			pool.shutdown();
		}
	}
	
	
	
	
	
	
	
	
}
