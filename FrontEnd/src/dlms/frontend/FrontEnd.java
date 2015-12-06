package dlms.frontend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import dlms.corba.AppException;
import dlms.corba.FrontEndPOA;
import shared.data.ServerInfo;
import shared.udp.IOperationMessage;
import shared.udp.Serializer;
import shared.udp.UDPMessage;
import shared.udp.message.client.DelayPaymentMessage;
import shared.udp.message.client.OpenAccountMessage;
import shared.udp.message.client.PrintCustomerInfoMessage;
import shared.util.Env;

/**
 * The DLMS CORBA front end
 * 
 * @author mat
 *
 */
public class FrontEnd extends FrontEndPOA {

	// These should be coming from a config
	public static final int MAX_DATAGRAM_SIZE = 4096;
	
	private Logger logger = null;
	private ServerInfo sqInfo = null;
	private volatile QueuePool opQueuePool = null;
	private volatile HashMap<String, HashMap<String, Integer>> faultyReplicas = null;
	
	/**
	 * Constructor
	 */
	public FrontEnd(Logger logger, QueuePool opQueuePool , HashMap<String, HashMap<String, Integer>> faultyReplicas) {
		
		super();

		this.logger = logger;
		this.opQueuePool = opQueuePool;
		this.faultyReplicas = faultyReplicas;
		this.sqInfo = Env.getSequencerServerInfo();
	}

	@Override
	public boolean delayPayment(String bankId, int loanId, String currentDueDate, String newDueDate)
			throws AppException {
		
		//
		logger.info("FrontEnd: Client invoked delayPayment(" + bankId + ", " + loanId + ", " + currentDueDate + ", " + newDueDate + ")");
		
		// Convert Dates
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy");
		Date dateCurrent = null;
		Date dateNew = null;
		
		try {
			dateCurrent = dateFormat.parse(currentDueDate);
			dateNew = dateFormat.parse(newDueDate);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		BlockingQueue<Boolean> resultQueue = new ArrayBlockingQueue<Boolean>(1);
		DelayPaymentMessage opMessage = new DelayPaymentMessage(bankId, loanId, dateCurrent, dateNew);
		ResultSetListener<Boolean> resultsListener = null;
		long opSequenceNbr = 0;
		Boolean result = null;
		
		// Send the operation to the sequencer and get the sequence number
		try {
			opSequenceNbr = this.forwardToSequencer(opMessage);
		} catch (AppException e) {
			logger.info("FrontEnd: " + e.reason);
			throw e;
		}
		
		// Listen for the operation result on the blocking queue - resultQueue
		resultsListener = new ResultSetListener<Boolean>(logger, this.opQueuePool, opSequenceNbr, resultQueue, faultyReplicas);
		resultsListener.start();
		
		//
		logger.info("FrontEnd: Sequencer replied to delayPayment operation with sequence number " + opSequenceNbr);

		try {
			result = resultQueue.poll(5000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new AppException("FrontEnd: InterruptedException while waiting for the operation result");
		}

		// Timeout!
		if (result == null) {
			throw new AppException("FrontEnd: The backend timed out");
		}
		
		return result;
	}

	@Override
	public String printCustomerInfo(String bankId) throws AppException {
		
		//
		logger.info("FrontEnd: Client invoked printCustomerInfo(" + bankId + ")");
		
		BlockingQueue<String> resultQueue = new ArrayBlockingQueue<String>(1);
		PrintCustomerInfoMessage opMessage = new PrintCustomerInfoMessage(bankId);
		ResultSetListener<String> resultsListener = null;
		long opSequenceNbr = 0;
		String result = null;
		
		// Send the operation to the sequencer and get the sequence number
		try {
			opSequenceNbr = this.forwardToSequencer(opMessage);
		} catch (AppException e) {
			logger.info("FrontEnd: " + e.reason);
			throw e;
		}
		
		// Listen for the operation result on the blocking queue - resultQueue
		resultsListener = new ResultSetListener<String>(logger, this.opQueuePool, opSequenceNbr, resultQueue, faultyReplicas);
		resultsListener.start();
		
		//
		logger.info("FrontEnd: Sequencer replied to printCustomerInfo operation with sequence number " + opSequenceNbr);

		try {
			result = resultQueue.poll(5000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new AppException("FrontEnd: InterruptedException while waiting for the operation result");
		}

		// Timeout!
		if (result == null) {
			throw new AppException("FrontEnd: The backend timed out");
		}
		
		return result;
	}
	
	@Override
	public int transferLoan(String bankId, int loanId, String currentBankId, String otherBankId) throws AppException {
		
		return 11;
	}

	@Override
	public int openAccount(String bankId, String firstName, String lastName, String emailAddress, String phoneNumber,
			String password) throws AppException {
		
		//
		logger.info("FrontEnd: Client invoked openAccount(" + bankId + ", " + firstName + ", " + emailAddress + ", " + phoneNumber + ", " + password + ")");
		
		BlockingQueue<Integer> resultQueue = new ArrayBlockingQueue<Integer>(1);
		OpenAccountMessage opMessage = new OpenAccountMessage(bankId, firstName, lastName, emailAddress, phoneNumber,
				password);
		ResultSetListener<Integer> resultsListener = null;
		long opSequenceNbr = 0;
		Integer result = null;
		
		// Send the operation to the sequencer and get the sequence number
		try {
			opSequenceNbr = this.forwardToSequencer(opMessage);
		} catch (AppException e) {
			logger.info("FrontEnd: " + e.reason);
			throw e;
		}
		
		// Listen for the operation result on the blocking queue - resultQueue
		resultsListener = new ResultSetListener<Integer>(logger, this.opQueuePool, opSequenceNbr, resultQueue, faultyReplicas);
		resultsListener.start();
		
		//
		logger.info("FrontEnd: Sequencer replied to openAccount operation with sequence number " + opSequenceNbr);

		try {
			result = resultQueue.poll(5000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			throw new AppException("FrontEnd: InterruptedException while waiting for the operation result");
		}

		// Timeout!
		if (result == null) {
			throw new AppException("FrontEnd: The backend timed out");
		}
		
		return result;
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
		InetSocketAddress remoteAddr = new InetSocketAddress(sqInfo.getIpAddress(), sqInfo.getPort()); // The Sequencer IP address
		long opSequenceNbr = 0;
		int retryCntr = 3;
		final byte[] incomingDataBuffer = new byte[MAX_DATAGRAM_SIZE];	
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
}
