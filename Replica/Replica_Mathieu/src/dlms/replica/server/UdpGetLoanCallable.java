package dlms.replica.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import dlms.replica.udpmessage.MessageRequestLoanSum;
import dlms.replica.udpmessage.MessageResponseLoanSum;

/**
 * Callable to run a UDP request to get loan info in a thread so that it doesn't block client operations
 * and be able to return a value to the parent thread
 * 
 * @author mat
 *
 */
public class UdpGetLoanCallable implements Callable<MessageResponseLoanSum> {

	private volatile Bank sourceBank;
	private volatile BankReplicaStub destinationBank;
	private String emailAddress;
	private volatile int sequenceNbr;
	private Logger logger;
	
	/**
	 * Constructor
	 * 
	 * @param sourceBank
	 * @param destinationBank
	 * @param emailAddress
	 * @param sequenceNbr
	 * @param logger
	 */
	public UdpGetLoanCallable(Bank sourceBank, BankReplicaStub destinationBank, String emailAddress, int sequenceNbr, Logger logger) {
		
		this.sourceBank = sourceBank;
		this.destinationBank = destinationBank;
		this.emailAddress = emailAddress;
		this.sequenceNbr = sequenceNbr;
		this.logger = logger;
	}

	/**
	 * Makes a UDP request to another bank get loan information on a particular user
	 */
	public MessageResponseLoanSum call() {
		
		DatagramSocket clientSocket = null;
		
		try {
			
			//
			// REQUESTING
			//
			
			// Init data structures
			clientSocket = new DatagramSocket(); // sourceBank.udpAddress.getPort()
			final byte[] receiveData = new byte[1024];
			final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ObjectOutputStream oos;
	        
	        // Prepare the loan request message
			MessageRequestLoanSum message = new MessageRequestLoanSum();
			message.emailAddress = this.emailAddress;
			message.sequenceNbr = this.sequenceNbr;
			
			// Serialize the message
			oos = new ObjectOutputStream(baos);
			oos.writeObject(message);
			byte[] sendData = baos.toByteArray();
			
			logger.info(this.sourceBank.getTextId() + " requesting loan info for user " + this.emailAddress + " at bank " + this.destinationBank.id);
			
			final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.destinationBank.addr);
			clientSocket.send(sendPacket);
	
			//
			// GETTING RESPONSE
			//
			clientSocket.setSoTimeout(5000);
			
			try {

				clientSocket.receive(receivePacket);
				
				// Parse the response data
				byte[] recvData = new byte[receivePacket.getLength()];
		        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), recvData, 0, receivePacket.getLength());

				ByteArrayInputStream bais = new ByteArrayInputStream(recvData);
	            ObjectInputStream ois;
	            Object obj = null;
	            MessageResponseLoanSum resp = null;
	            
				try {
					ois = new ObjectInputStream(bais);
					obj = ois.readObject();
		            bais.close();
		            ois.close();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

	            if(obj instanceof MessageResponseLoanSum) {
	                resp = (MessageResponseLoanSum) obj;
	            } else {
	                System.out.println("UDP request was not a MessageResponseLoanSum object");
	                //System.exit(1);
	            }
				
	            if (resp.status) {
	            	logger.info(this.sourceBank.getTextId() + ": Received loan info response from  " + 
	            			this.destinationBank.id + " {status: " + resp.status + 
	            			", user: " + this.emailAddress + ", loanSum: " + resp.loanSum + "}");
	            }
	            else {
	            	logger.info(this.sourceBank.getTextId() + " Received loan info response from  " + 
	            			this.destinationBank.id + " {status:  " + resp.status + 
	            			", user: " + this.emailAddress + "}");
	            }
				
				return resp;

			} catch (final SocketTimeoutException ste) {
				System.out.println("Timeout Occurred: Packet assumed lost");
			} finally {
				if (clientSocket != null)
					clientSocket.close();
			}
			
			clientSocket.close();

		} catch (final SocketException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (clientSocket != null)
				clientSocket.close();
		}
		
		return null;
	}
}
