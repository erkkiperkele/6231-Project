package dlms.replica.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Logger;

import dlms.replica.exception.ValidationException;
import dlms.replica.model.Account;
import dlms.replica.udpmessage.MessageRequestLoanSum;
import dlms.replica.udpmessage.MessageRequestTransferLoan;
import dlms.replica.udpmessage.MessageResponseLoanSum;
import dlms.replica.udpmessage.MessageResponseTransferLoan;

/**
 * The UdpListener class is a runnable associated with a specific bank servant 
 * that runs in an infinite loop to accept incoming UDP/IP packets or more 
 * appropriately, requests from other bank servants
 * 
 * @author mat
 *
 */
public class UdpListener implements Runnable {

	private static final int RECV_BUFFER_SIZE = 1024;

	protected volatile Bank bank;
	protected Logger logger;
	
	/**
	 * Constructor
	 * 
	 * @param bank
	 * @param logger
	 */
	UdpListener(Bank bank, Logger logger) {

		this.bank = bank;
		this.logger = logger;
	}

	@Override
	public void run() {

		DatagramSocket serverSocket = null;

		try {

			serverSocket = new DatagramSocket(this.bank.udpAddress);
			byte[] receiveData = new byte[RECV_BUFFER_SIZE];
			byte[] sendData = new byte[RECV_BUFFER_SIZE];

			while (true) {

				//
				// LISTENER
				//
				
				receiveData = new byte[RECV_BUFFER_SIZE];
				final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

				// Wait for the packet
				logger.info(this.bank.getTextId() + ": Waiting for bank UDP request on " + this.bank.udpAddress.toString());
				serverSocket.receive(receivePacket);
				
				// Received a request. Parse it.
				byte[] data = new byte[receivePacket.getLength()];
		        System.arraycopy(receivePacket.getData(), receivePacket.getOffset(), data, 0, receivePacket.getLength());
				final InetAddress remoteIpAddress = receivePacket.getAddress();
				final int remotePort = receivePacket.getPort();

				// Extract the receiver's message into the appropriate object
				ByteArrayInputStream bais = new ByteArrayInputStream(data);
	            ObjectInputStream ois = new ObjectInputStream(bais);
	            Object obj;
				try {
					obj = ois.readObject();
				} catch (ClassNotFoundException e) {
					logger.info(this.bank.getTextId() + ": Recieved an invalid packet from: " + remoteIpAddress + ":" + remotePort + ". Discarding it.");
					continue;
				}
	            bais.close();
	            ois.close();

	            // Take appropriate action based on the request
				if (obj instanceof MessageRequestLoanSum) {
					this.RespondGetLoan((MessageRequestLoanSum) obj, sendData, remoteIpAddress, remotePort, serverSocket);
				} 
				else if (obj instanceof MessageRequestTransferLoan) {
					this.RespondTransferLoan((MessageRequestTransferLoan) obj, sendData, remoteIpAddress, remotePort, serverSocket);
				} else {
					logger.info(this.bank.getTextId() + ": Received an unknown request packet from " + remoteIpAddress + ":" + remotePort + ". Discarding it.");
					continue;
				}
			}

		} catch (final SocketException e) {
			logger.info(this.bank.getTextId() + ": Unable to bind " + this.bank.getId() + " to UDP Port " + this.bank.udpAddress.getPort() + ". Port already in use.");
			System.exit(1);
		} catch (final IOException e) {
			e.printStackTrace();
			//System.exit(1);
		} finally {if(serverSocket != null) serverSocket.close();}
	}

	/**
	 * 
	 * @param req
	 * @param sendData
	 * @param remoteIpAddress
	 * @param remotePort
	 * @param serverSocket
	 */
	protected void RespondTransferLoan(MessageRequestTransferLoan req, byte[] sendData, InetAddress remoteIpAddress, 
			int remotePort, DatagramSocket serverSocket) {

		// Request parsed successfully
		logger.info(this.bank.getTextId() + ": Received loan transfer request from " + this.bank.udpAddress.toString() + " for user " + req.account.getEmailAddress());
		
		//
		// RESPONDER
		//

		// Check if the account exists already and get the account number so we can add the loan
		int accountNbr = 0;
		Account account = this.bank.getAccount(req.loan.getEmailAddress());
		if (account == null) {

			try {
				accountNbr = this.bank.createAccount(req.account.getFirstName(), req.account.getLastName(),
						req.account.getEmailAddress(), req.account.getPhoneNbr(), req.account.getPassword());
			} catch (ValidationException e) {

			}
			if (accountNbr < 1) {
				// Handle failed account creation
			}

			logger.info(this.bank.getTextId() + ": Loan transfer created a new account for user "
					+ req.account.getEmailAddress() + " + with number " + accountNbr);
		} else {
			accountNbr = account.getAccountNbr();
		}
		
		// Add the loan
		int newLoandId = this.bank.createLoan(accountNbr, req.loan.getEmailAddress(), req.loan.getAmount(), req.loan.getDueDate());
		logger.info(this.bank.getTextId() + ": Loan transfer created a new loan for user " + req.account.getEmailAddress() + " with ID " + newLoandId);
		
		MessageResponseTransferLoan resp = new MessageResponseTransferLoan();
		resp.message = "Loan added successfully";
		resp.status = true;
		resp.loanId = newLoandId;
		resp.accountNbr = accountNbr;
		resp.sequenceNbr = req.sequenceNbr;
		
        // Prep the response
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(resp);
			sendData = baos.toByteArray();
			baos.close();
	        oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		logger.info(this.bank.getTextId() + ": Responding to successful loan transfer request for user " + req.account.getEmailAddress());

		final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, remoteIpAddress, remotePort);
		
		try {
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param req
	 * @param sendData
	 * @param remoteIpAddress
	 * @param remotePort
	 * @param serverSocket
	 */
	protected void RespondGetLoan(MessageRequestLoanSum req, byte[] sendData, InetAddress remoteIpAddress, int remotePort, DatagramSocket serverSocket) {

		int loanSum;
		final DatagramPacket sendPacket;
		
		logger.info(this.bank.getTextId() + " received loan request from " + this.bank.udpAddress.toString() + " for user " + req.emailAddress);
		
		//
		// RESPONDER
		//

		// It is not permitted to make a loan from multiple banks simultaneously
		// Catch the problem here

//		if (!this.bank.contextEmailAddress.isEmpty() && this.bank.contextEmailAddress.equals(req.emailAddress)) {
//			
//			//logger.info(this.bank.getTextId() + ": Simultaneous send/receive loan info for user " + req.emailAddress);
//			
//	        MessageResponseLoanSum resp = new MessageResponseLoanSum();
//	        resp.loanSum = 99999999;
//	        resp.emailAddress = req.emailAddress;
//	        resp.sequenceNbr = req.sequenceNbr;
//	        resp.status = false;
//	        resp.message = "Simultaneous loan requests (" + req.emailAddress + ") are not permitted.";
//			
//	        // Prep the response
//	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//	        ObjectOutputStream oos;
//			try {
//				oos = new ObjectOutputStream(baos);
//				oos.writeObject(resp);
//				sendData = baos.toByteArray();
//				baos.close();
//		        oos.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			logger.info(this.bank.getTextId() + ": Loan refused for user " + req.emailAddress + ". " + resp.message);
//
//			sendPacket = new DatagramPacket(sendData, sendData.length, remoteIpAddress, remotePort);
//		}
//		else {

	        // Get the sum of all loans for this user and create the response
			loanSum = this.bank.getLoanSum(req.emailAddress);
			
	        MessageResponseLoanSum resp = new MessageResponseLoanSum();
	        resp.loanSum = loanSum;
	        resp.emailAddress = req.emailAddress;
	        resp.sequenceNbr = req.sequenceNbr;
	        resp.status = true;
	        //resp.message = "Success";
			
	        // Prep the response
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(resp);
				sendData = baos.toByteArray();
				baos.close();
		        oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			logger.info(this.bank.getTextId() + " responding to loan request for user " + req.emailAddress + " with loan sum: " + resp.loanSum);

			sendPacket = new DatagramPacket(sendData, sendData.length, remoteIpAddress, remotePort);
//		}
		
		try {
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
