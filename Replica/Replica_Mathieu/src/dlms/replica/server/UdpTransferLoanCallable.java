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

import dlms.replica.model.Account;
import dlms.replica.model.Loan;
import dlms.replica.udpmessage.MessageRequestTransferLoan;
import dlms.replica.udpmessage.MessageResponseTransferLoan;

/**
 * This class performs a UDP/IP request to another bank. Specifically, it is a worker thread
 * for the transfer loan operation between bank servants
 * 
 * @author mat
 *
 */
public class UdpTransferLoanCallable implements Callable<MessageResponseTransferLoan> {

	private volatile Bank sourceBank;
	private volatile BankReplicaStub destinationBank;
	private volatile int loanId;
	private volatile int sequenceNbr;
	private Logger logger;
	
	/**
	 * Constructor
	 * 
	 * @param sourceBank
	 * @param destinationBank
	 * @param loanId
	 * @param sequenceNbr
	 * @param logger
	 */
	public UdpTransferLoanCallable(Bank sourceBank, BankReplicaStub destinationBank, int loanId, int sequenceNbr, Logger logger) {
		
		this.sourceBank = sourceBank;
		this.destinationBank = destinationBank;
		this.loanId = loanId;
		this.sequenceNbr = sequenceNbr;
		this.logger = logger;
	}

	/**
	 * Makes a UDP request to another bank to transfer a loan
	 */
	public MessageResponseTransferLoan call() {
		
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
	        
	        // Get the account and loan objects and send them to the other bank
			Loan loan = sourceBank.getLoan(loanId);
			Account account = sourceBank.getAccount(loan.getAccountNbr());
	        
	        // Prepare the loan request message
			MessageRequestTransferLoan message = new MessageRequestTransferLoan();
			message.loan = loan;
			message.account = account;
			message.sequenceNbr = this.sequenceNbr;
			
			// Serialize the message
			oos = new ObjectOutputStream(baos);
			oos.writeObject(message);
			byte[] sendData = baos.toByteArray();

			logger.info(this.sourceBank.getTextId() + ": Requesting loan transfer for loan ID " + this.loanId + " to bank " + this.destinationBank.id+ ". (sendData.length=" + sendData.length);
	        
			final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.destinationBank.addr);
			clientSocket.send(sendPacket);
	
			//
			// GETTING RESPONSE
			//
			clientSocket.setSoTimeout(10000);
			
			try {

				clientSocket.receive(receivePacket);
				
				// Parse the response data
				byte[] recvData = new byte[receivePacket.getLength()];
		        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), recvData, 0, receivePacket.getLength());

				ByteArrayInputStream bais = new ByteArrayInputStream(recvData);
	            ObjectInputStream ois;
	            Object obj = null;
	            MessageResponseTransferLoan resp = null;
	            
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

	            if(obj instanceof MessageResponseTransferLoan) {
	                resp = (MessageResponseTransferLoan) obj;
	            } else {
	                System.out.println("UDP request was not a MessageResponseLoan object");
	                //System.exit(1);
	            }
				
				logger.info(this.sourceBank.getTextId() + ": Received loan transfer response from  " + this.destinationBank.id + " for user loan ID " + loanId);

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
