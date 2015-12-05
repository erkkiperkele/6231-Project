package shared.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Iterator;
import shared.data.AbstractServerBank;
import shared.data.ServerInfo;
import shared.udp.message.client.DelayPaymentMessage;
import shared.udp.message.client.GetLoanMessage;
import shared.udp.message.client.OpenAccountMessage;
import shared.udp.message.client.PrintCustomerInfoMessage;
import shared.udp.message.client.TransferLoanMessage;
import shared.util.Env;

/**
 * @author Pascal Tozzi 27664850 UDPServerHandleRequestThread handle packet.
 */
public class UDPServerHandleRequestThread implements Runnable
{
	private Thread t = null;
	private String key;
	private boolean waitForNextDatagram = true;
	private DatagramPacket receivedDatagram;
	private UDPMessage receivedUdpMessage;
	private DatagramSocket aSocket;
	private HashMap<String, UDPServerHandleRequestThread> dicHandleRequest;
	private Exception lastError = null;
	protected AbstractServerBank bank;
	
	public void initialize(String key, AbstractServerBank bank, DatagramSocket aSocket,
			DatagramPacket request, UDPMessage udpMessage, HashMap<String, 
			UDPServerHandleRequestThread> dicHandleRequest) {
		this.dicHandleRequest = dicHandleRequest;
		this.aSocket = aSocket;
		this.bank = bank;
		this.receivedDatagram = request;
		this.receivedUdpMessage = udpMessage;
		this.key = key;
		t = new Thread(this);
		t.start();
	}

	public void resumeNextUdpMessageReceived(DatagramPacket receivedDatagram, UDPMessage udpMessage) 
	{
		this.receivedUdpMessage = udpMessage;
		this.receivedDatagram = receivedDatagram;
		waitForNextDatagram = false;
		this.notifyAll();
	}
	
	/**
	 * Wait until there more data to process
	 */
	public synchronized void sendAndWaitUntilNextUdpMessage(UDPMessage message) throws Exception
	{
		byte[] response = Serializer.serialize(message);
		DatagramPacket reply = new DatagramPacket(response, response.length, receivedDatagram.getAddress(), receivedDatagram.getPort());
		try
		{
			// lock to wait for next DatagramPacket which is the answer of this reply
			waitForNextDatagram = true;
			
			aSocket.send(reply);
			
			// wait for the response before continuing
			while (waitForNextDatagram)
			{
				try
				{
					this.wait();
				}
				catch (Exception e)
				{

				}
			}
			waitForNextDatagram = true;
		}
		catch (Exception e)
		{
			// handled by the parent function
			throw e;
		}
	}
	
	/**
	 * Wait until there more data to process
	 */
	public void send(UDPMessage message) throws Exception
	{
		byte[] response = Serializer.serialize(message);
		DatagramPacket reply = new DatagramPacket(response, response.length, receivedDatagram.getAddress(), receivedDatagram.getPort());
		try
		{
			aSocket.send(reply);
		}
		catch (Exception e)
		{
			// handled by the parent function
			throw e;
		}
	}
	
	/**
	 * Wait until there more data to process
	 */
	public int sendToAll(UDPMessage message)
	{
		byte[] response;
		try
		{
			response = Serializer.serialize(message);
		}
		catch (Exception e)
		{
			System.out.println("sendToAll Serializer.serialize(message) Exception: " + e.getMessage());
			return 0;
		}
		
		int nCount = 0;
		
		for (Iterator<String> iterator = Env.getListMachineName(); iterator.hasNext();) {
			String machineName = iterator.next();
			for(ServerInfo replica : Env.getReplicaServerInfoList(machineName))
			{
				DatagramPacket reply = new DatagramPacket(response, response.length, replica.getAddress());
				try
				{
					aSocket.send(reply);
					nCount++;
				}
				catch (Exception e)
				{
					// handled by the parent function
					System.out.println("sendToAll Exception: " + e.getMessage());
				}
			}
		}
		return nCount;
	}
	
	@Override
	public void run()
	{
		try
		{
			UDPMessage udpMessage = this.receivedUdpMessage;
			processRequest(udpMessage);
		}
		catch (Exception e)
		{
			lastError = e;
		}
		finally
		{
			if(dicHandleRequest != null)
			{
				synchronized (dicHandleRequest)
				{
					dicHandleRequest.remove(key);
				}
			}
		}
    }
	
	/**
	 * Method can be overwritten for FE, Sequencer, Replica, ReplicaManager
	 * @param bank
	 * @param udpMessage
	 * @throws Exception 
	 */
	protected void processRequest(UDPMessage udpMessage) throws Exception
	{
		switch (udpMessage.getOperation())
		{
			case OpenAccount:
				processOpenAccount(udpMessage);
				break;
			case GetLoan:
				processGetLoan(udpMessage);
				break;
			case DelayPayment:
				processDelayPayment(udpMessage);
				break;
			case PrintCustomerInfo:
				processPrintCustomerInfo(udpMessage);
				break;
			case TransferLoan:
				processTransferLoan(udpMessage);
				break;
			default:
				System.out.println("Unknown processRequest! " + udpMessage.getOperation().toString());
				break;
		}
	}

	/**
	 * processOpenAccount
	 * @param udpMessage is an instance of OpenAccountMessage
	 * @throws Exception
	 */
	private void processOpenAccount(UDPMessage udpMessage) throws Exception 
	{
		if(udpMessage.getMessage() instanceof OpenAccountMessage)
		{
			OpenAccountMessage msg = (OpenAccountMessage)udpMessage.getMessage();
			if(!this.bank.getServerName().equalsIgnoreCase(msg.getBank()))
			{
				msg.setException(new Exception("Wrong Bank Name! Request denied. processOpenAccount"));
			}
			else
			{
				int accountNumber = -1;
				try {
					accountNumber = bank.openAccount(
											msg.getFirstName(), 
											msg.getLastName(), 
											msg.getEmailAddress(), 
											msg.getPhoneNumber(), 
											msg.getPassword());
				} 
				catch (Exception e) 
				{
					msg.setException(e);
					accountNumber = -1;
				}
				msg.setResultAccountID(accountNumber);
			}
			this.send(udpMessage);
		}
		else
		{
			throw new Exception("Invalid Object processOpenAccount");
		}
	}

	/**
	 * processGetLoan
	 * @param udpMessage is an instance of GetLoanMessage
	 * @throws Exception
	 */
	private void processGetLoan(UDPMessage udpMessage) throws Exception 
	{
		if(udpMessage.getMessage() instanceof GetLoanMessage)
		{
			GetLoanMessage msg = (GetLoanMessage)udpMessage.getMessage();
			if(!this.bank.getServerName().equalsIgnoreCase(msg.getBank()))
			{
				msg.setException(new Exception("Wrong Bank Name! Request denied. processGetLoan"));
			}
			else
			{
				int loanNumber = -1;
				try {
					loanNumber = bank.getLoan(
							msg.getAccountNumber(),
							msg.getPassword(),
							msg.getLoanAmount());
				} catch (Exception e) {
					msg.setException(e);
				}
				msg.setResultLoanID(loanNumber);
			}
			this.send(udpMessage);
		}
		else
		{
			throw new Exception("Invalid Object processGetLoan");
		}
	}

	/**
	 * processDelayPayment
	 * @param udpMessage is an instance of DelayPaymentMessage
	 * @throws Exception
	 */
	private void processDelayPayment(UDPMessage udpMessage) throws Exception 
	{
		if(udpMessage.getMessage() instanceof DelayPaymentMessage)
		{
			DelayPaymentMessage msg = (DelayPaymentMessage)udpMessage.getMessage();
			if(!this.bank.getServerName().equalsIgnoreCase(msg.getBank()))
			{
				msg.setException(new Exception("Wrong Bank Name! Request denied. processDelayPayment"));
			}
			else
			{
				boolean isDelayed = false;
				try {
					isDelayed = bank.delayPayment(msg.getLoanID(), msg.getCurrentDueDate(), msg.getNewDueDate());
				} catch (Exception e) {
					msg.setException(e);
				}
				msg.setDelaySuccessful(isDelayed);
			}
			this.send(udpMessage);
		}
		else
		{
			throw new Exception("Invalid Object processDelayPayment");
		}
	}

	/**
	 * processPrintCustomerInfo
	 * @param udpMessage is an instance of PrintCustomerInfoMessage
	 * @throws Exception
	 */
	private void processPrintCustomerInfo(UDPMessage udpMessage) throws Exception 
	{
		if(udpMessage.getMessage() instanceof PrintCustomerInfoMessage)
		{
			PrintCustomerInfoMessage msg = (PrintCustomerInfoMessage)udpMessage.getMessage();
			if(!this.bank.getServerName().equalsIgnoreCase(msg.getBank()))
			{
				msg.setException(new Exception("Wrong Bank Name! Request denied. processPrintCustomerInfo"));
			}
			else
			{
				String result = "";
				try {
					result = bank.printCustomerInfo();
				} catch (Exception e) {
					msg.setException(e);
				}
				msg.setResult(result);
			}
			this.send(udpMessage);
		}
		else
		{
			throw new Exception("Invalid Object processPrintCustomerInfo");
		}
	}

	/**
	 * processTransferLoan
	 * @param udpMessage is an instance of TransferLoanMessage
	 * @throws Exception
	 */
	private void processTransferLoan(UDPMessage udpMessage) throws Exception 
	{
		if(udpMessage.getMessage() instanceof TransferLoanMessage)
		{
			TransferLoanMessage msg = (TransferLoanMessage)udpMessage.getMessage();
			if(!this.bank.getServerName().equalsIgnoreCase(msg.getCurrentBank()))
			{
				msg.setException(new Exception("Wrong Bank Name! Request denied. processTransferLoan"));
			}
			else
			{
				boolean isTransferred = false;
				try {
					isTransferred = bank.transferLoan(msg.getLoanID(), msg.getOtherBank());
				} catch (Exception e) {
					msg.setException(e);
				}
				msg.setTransferSuccessful(isTransferred);
			}
			this.send(udpMessage);
		}
		else
		{
			throw new Exception("Invalid Object processTransferLoan");
		}
	}

	public boolean isError()
	{
		return lastError != null;
	}
	
	public Exception getLastError()
	{
		return lastError;
	}

	public String getKey() {
		return key;
	}

	public UDPMessage getReceivedUdpMessage() {
		return receivedUdpMessage;
	}
}
