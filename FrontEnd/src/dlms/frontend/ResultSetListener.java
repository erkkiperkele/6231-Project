package dlms.frontend;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import shared.data.Bank;
import shared.data.ServerInfo;
import shared.entities.FailureType;
import shared.udp.IOperationResult;
import shared.udp.ReplicaStatusMessage;
import shared.udp.Serializer;
import shared.udp.UDPMessage;
import shared.util.Constant;
import shared.util.Env;

public class ResultSetListener<T> extends Thread {

	private static final int MAX_QUEUE_SIZE = 8;
	
	protected Logger logger;
	protected long opSequenceNbr;
	private volatile QueuePool opQueuePool = null; // Shared with all threads
	private volatile BlockingQueue<T> resultQueue = null; // Shared with the client thread
	private volatile BlockingQueue<UDPMessage> queue = null;
	private boolean isResultSentToClient = false;
	private HashMap<String, ServerInfo> replicaManagerGroup;
	private volatile HashMap<String, HashMap<String, Integer>> faultyReplicas = null;

	/**
	 * Constructor
	 * 
	 * @param logger
	 * @return 
	 */
	public ResultSetListener(Logger logger, QueuePool opQueuePool, long opSequenceNbr, BlockingQueue<T> resultQueue,
			HashMap<String, HashMap<String, Integer>> faultyReplicas) {

		super();
		this.logger = logger;
		this.opQueuePool = opQueuePool;
		this.opSequenceNbr = opSequenceNbr;
		this.resultQueue = resultQueue;
		this.faultyReplicas = faultyReplicas;
		
		this.queue = new ArrayBlockingQueue<UDPMessage>(MAX_QUEUE_SIZE);
		opQueuePool.put(opSequenceNbr, this.queue);
		
		this.replicaManagerGroup = new HashMap<String, ServerInfo>();
		
		ServerInfo rm1Info = Env.getReplicaManagerServerInfo(Constant.MACHINE_NAME_RICHARD);
		ServerInfo rm2Info = Env.getReplicaManagerServerInfo(Constant.MACHINE_NAME_AYMERIC);
		ServerInfo rm3Info = Env.getReplicaManagerServerInfo(Constant.MACHINE_NAME_PASCAL);
		ServerInfo rm4Info = Env.getReplicaManagerServerInfo(Constant.MACHINE_NAME_MATHIEU);
		
		replicaManagerGroup.put(Constant.MACHINE_NAME_RICHARD, rm1Info);
		replicaManagerGroup.put(Constant.MACHINE_NAME_AYMERIC, rm2Info);
		replicaManagerGroup.put(Constant.MACHINE_NAME_PASCAL, rm3Info);
		replicaManagerGroup.put(Constant.MACHINE_NAME_MATHIEU, rm4Info);
	}

	
	@Override
	public void run() {

		try {
//			boolean timedOut = false;
//			long startTotalTime = System.currentTimeMillis();
//			long startTime = 0;
//			long totalTime = 0;
//			long endTime = 0;
//			long longestBlockingTime = 0;
//			long maxTimeout = 5000;
			UDPMessage msg = null;
			HashMap<String, UDPMessage> messages = new HashMap<String, UDPMessage>();

//			startTime = System.currentTimeMillis();

			// Wait for messages to arrive...

			while(messages.size() < 4) {

				try {
					msg = this.queue.poll(3000, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
					continue;
				}

				// Timed out
				if (msg == null) {
					break;
				}
				
				String msgKey = this.getUdpMessageKey(msg);
				messages.put(msgKey, msg);
				
				if (messages.size() < 2) {
					continue;
				}

				// At the first opportunity, find two identical results. Send one to the client
				this.sendToClient(messages);
			}
			
			// If not done already...
			this.sendToClient(messages);
			
			// Maybe no value was sent to the client. Too bad, let it time out
			// Flag any error messages and possible process failures
			this.handleErrors(messages);
			
//			long blockingTime = System.currentTimeMillis() - startTime;
//			totalTime += blockingTime;
//			long blockingTimeDoubled = blockingTime * 2;
//			if (blockingTimeDoubled > longestBlockingTime) {
//				longestBlockingTime = blockingTimeDoubled;
//			}
	
		} finally {
			opQueuePool.remove(this.opSequenceNbr);
		}
	}
	
	/**
	 * 
	 * @param messages
	 * @return
	 */
	private boolean handleErrors(HashMap<String, UDPMessage> messages) {
		
		String bankName = null;

		if (messages.size() < 2 || messages.size() > 4) {
			logger.info("FrontEnd: Fatal error. Received " + messages.size() + " responses from replicas for operation " + opSequenceNbr);
			return false;
		}

		ArrayList<UDPMessage> list = new ArrayList<UDPMessage>(messages.values());
		
		// Handle process crashes
		
		// Figure out which bank we are dealing with
		for (UDPMessage msg : messages.values()) {
			bankName = msg.getMessage().getBank();
			break;
		}
		
		if (messages.size() < 4) {
			
			// Get the list of machines which are missing a UDP Message
			HashMap<String, Integer> missingMachines = new HashMap<String, Integer>();
			missingMachines.put(Constant.MACHINE_NAME_RICHARD, 0);
			missingMachines.put(Constant.MACHINE_NAME_AYMERIC, 0);
			missingMachines.put(Constant.MACHINE_NAME_PASCAL, 0);
			missingMachines.put(Constant.MACHINE_NAME_MATHIEU, 0);
			
			for (UDPMessage msg : messages.values()) {
				String machineName = msg.getMessage().getMachineName();
				missingMachines.remove(machineName);
			}
			
			if (missingMachines.size() > 1) {
				logger.info("FrontEnd: Mulitple replicas failed to reply to an operation. Only sending notification of the first failure to the RM - op: " + opSequenceNbr);
			}

			for (String machineName : missingMachines.keySet()) {
				this.multicastCrashedProcess(machineName, bankName, FailureType.failure);
				break; // Exit after the first multicast
			}
			
			// If there are two correct values at this point, it should have already
			// been conveyed to the client. If there are two errors, we can't handle
			// this situation .Exit if there is is missing two or more Messages, 
			// because we won't be able to compare results
			if (missingMachines.size() > 1) {
				return false;
			}
		}

		
		// Handle process bugs - At this point, there should be at least three results
		
		HashMap<String, Boolean> validMessages = new HashMap<String, Boolean>();
		HashMap<String, UDPMessage> invalidMessages = new HashMap<String, UDPMessage>();

		boolean foundValidResult = false;

		for (int i = 0; i < messages.size(); i++) {

			UDPMessage msg1 = list.get(i);
			String msgKey1 = this.getUdpMessageKey(msg1);
			IOperationResult<T> firstMessage = (IOperationResult<T>) msg1.getMessage();
			
			for (int j = 0; j < messages.size(); j++) {
				
				if (i == j) { continue; }
				
				UDPMessage msg2 = list.get(j);
				String msgKey2 = this.getUdpMessageKey(msg2);
				IOperationResult<T> secondMessage = (IOperationResult<T>) msg2.getMessage();
				
				if (firstMessage.isResultEqual(secondMessage)) {

					validMessages.put(msgKey2, true);
					
					if (!foundValidResult) {
						
						validMessages.put(msgKey1, true);
						foundValidResult = true;
					}
				}
			}
			
			// If we found a valid result, break out and flag all other messages as incorrect
			if (foundValidResult) {
				break;
			}
		}
		
		for (String key : messages.keySet()) {
			if (validMessages.get(key) == null) {
				// Flag this message as being invalid
				invalidMessages.put(key, messages.get(key));
			}
		}
		
		// Now we have all invalid messages. Add them to the list of replicas that failed
		for (UDPMessage msg : invalidMessages.values()) {
		
			String machineName = msg.getMessage().getMachineName();

			HashMap<String, Integer> failuresPerMachine = faultyReplicas.get(machineName);
			Integer failureCnt = failuresPerMachine.get(bankName);
			if (failureCnt == null) {
				// Flag a first failure
				failuresPerMachine.put(bankName, 1);
			}
			
			failureCnt++;
			
			// Send a message to the replica managers that MAchineName/BankName bugged out
			if (failureCnt > 2) {
				failuresPerMachine.remove(bankName);
				this.multicastCrashedProcess(machineName, bankName, FailureType.error);
			}
			else {
				// Update the entry
				failuresPerMachine.put(bankName, failureCnt);
			}
		}

		return false;
	}

	

	/**
	 * Checks to see if there is a valid value in the result set and makes it
	 * available to the parent thread if so.
	 * 
	 * @param messages
	 * @return
	 */
	private boolean sendToClient(HashMap<String, UDPMessage> messages) {
		
		if (!this.isResultSentToClient) {
			T result = this.getValidResult(messages);
			if (result != null) {
				resultQueue.add(result);
				isResultSentToClient = true;
			}
		}
		
		return isResultSentToClient;
	}
	
	
	/**
	 * Gets a valid result form a set of UDP messages by comparing values with
	 * one another. When we find two values that are identical, return it.
	 * Otherwise return null.
	 * 
	 * @param messages
	 * @return
	 */
	private T getValidResult(HashMap<String, UDPMessage> messages) {

		ArrayList<UDPMessage> list = new ArrayList<UDPMessage>(messages.values());

		for (int i = 0; i < messages.size(); i++) {

			IOperationResult<T> firstMessage = (IOperationResult<T>) list.get(i).getMessage();
			
			for (int j = i; j < messages.size(); j++) {
				
				IOperationResult<T> secondMessage = (IOperationResult<T>) list.get(j).getMessage();
				if (firstMessage.isResultEqual(secondMessage)) {
					return firstMessage.getResult();
				}
			}
		}
		
		return null;
	}
	
	
	/**
	 * Sends a message to each replica manager saying that a replica failed
	 * 
	 * @param BankId
	 * @param opSequenceNbr
	 */
	private void multicastCrashedProcess(String machineName, String bankName, FailureType failureType) {
		
		ExecutorService pool;
		Set<Future<Boolean>> set;

		// Prepare the threads to call other banks to get the loan sum for this account
		pool = Executors.newFixedThreadPool(this.replicaManagerGroup.size());
	    set = new HashSet<Future<Boolean>>();

	    ReplicaStatusMessage udpMessage = new ReplicaStatusMessage(Bank.fromString(bankName), "", failureType);
	    byte[] outgoingData;
		try {
			outgoingData = Serializer.serialize(udpMessage);
		} catch (IOException e1) {
			logger.info("FrontEnd: Unable to serialize UDP message to replica managers");
			return;
		}

		try {

			// 
			for (ServerInfo rmStub : this.replicaManagerGroup.values()) {
				Callable<Boolean> callable = new UdpSend(outgoingData,
						new InetSocketAddress(rmStub.getIpAddress(), rmStub.getPort()));
				logger.info("FrontEnd: Sending failed process message to " + rmStub.getServerName() + "'s replica manager");
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
	 * 
	 * @param msg
	 * @return
	 */
	protected String getUdpMessageKey(UDPMessage msg) {
		return msg.getMessage().getMachineName() + msg.getMessage().getBank();
	}
	
}
