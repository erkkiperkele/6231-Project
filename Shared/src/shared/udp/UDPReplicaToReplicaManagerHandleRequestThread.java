package shared.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import shared.data.AbstractServerBank;
import shared.data.BankState;
import shared.data.Customer;
import shared.data.Loan;
import shared.udp.message.client.RequestSynchronize;
import shared.udp.message.client.SynchronizeCustomer;
import shared.udp.message.client.SynchronizeLoan;
import shared.util.Env;

/**
 * @author Pascal Tozzi 27664850 UDPServerHandleRequestThread handle packet.
 */
public class UDPReplicaToReplicaManagerHandleRequestThread implements Runnable
{
	private Thread t = null;
	private String key;
	private List<DatagramPacket> receivedDatagramList;
	private List<UDPMessage> receivedUdpMessageList;
	private DatagramPacket receivedDatagram;
	private UDPMessage receivedUdpMessage;
	private DatagramSocket aSocket;
	private HashMap<String, UDPReplicaToReplicaManagerHandleRequestThread> dicHandleRequest;
	private Exception lastError = null;
	protected AbstractServerBank bank;
	private Loan[] receivedLoansList = null;
	private Customer[] receivedCustomersList = null;
	private UDPMessage[] sentLoansList = null;
	private UDPMessage[] sentCustomersList = null;
	private Timer timer;
	private int countCanTerminate = 0;
	private int delaySeconds = 0;
	
	private int nextCustomerID = 0;
	private int nextLoanID = 0;
	private int nextSequenceID = 0;
	
	private InetAddress requestAddr = null;
	private int requestPort = 0;
	
	public void initialize(String key, AbstractServerBank bank, DatagramSocket aSocket,
			DatagramPacket request, UDPMessage udpMessage, HashMap<String, 
			UDPReplicaToReplicaManagerHandleRequestThread> dicHandleRequest) {
		this.dicHandleRequest = dicHandleRequest;
		this.aSocket = aSocket;
		this.bank = bank;
		this.receivedDatagramList = new ArrayList<DatagramPacket>();
		this.receivedUdpMessageList = new ArrayList<UDPMessage>();
		this.receivedDatagram = request;
		this.receivedUdpMessage = udpMessage;
		this.key = key;
		this.timer = new Timer();		
		
		UDPReplicaToReplicaManagerHandleRequestThread current = this;
		timer.schedule(new TimerTask() {

            @Override
            public void run() {
            	delaySeconds++;
            	if(delaySeconds > 6)
            	{
                	current.timerExec();
                	delaySeconds = 0;
            	}
            }
        }, 0, 1000);
		
		t = new Thread(this);
		t.start();
	}
	
	/**
	 * Not receiving any packet, verify if packet missing and validate
	 * If valid, terminate
	 */
	public void timerExec() 
	{
		if(receivedLoansList == null)
		{
			countCanTerminate++;
			if(countCanTerminate > 1)
			{
				// Returning answer, finished ;)
				terminateThread();
			}
		}
		else
		{
			if(canProcessEndOfSynchronise() == false)
			{
				requestMissingPacket();
			}
			else
			{
				// Returning answer, finished ;)
				terminateThread();
			}
		}
	}

	private void terminateThread() {
		timer.cancel();
		timer = null;
		// ending
		if(receivedLoansList == null)
		{
			// return the values
			List<Loan> loanList = new ArrayList<Loan>(Arrays.asList(receivedLoansList));
			List<Customer> customerList = new ArrayList<Customer>(Arrays.asList(receivedCustomersList));
			BankState state = new BankState(loanList, customerList, nextCustomerID, nextLoanID);
			state.setNextSequenceNumber(nextSequenceID);
			bank.setCurrentState(state);
		}
		
		if(dicHandleRequest != null)
		{
			synchronized (dicHandleRequest)
			{
				dicHandleRequest.remove(key);
			}
		}
	}

	private boolean lastPacketReceived() {
		if(receivedLoansList[receivedLoansList.length-1] == null)
			return false;
		
		if(receivedCustomersList[receivedCustomersList.length - 1] == null)
			return false;
		
		return true;
	}
	
	private boolean canProcessEndOfSynchronise() {
		if(receivedLoansList[receivedLoansList.length-1] == null)
			return false;
		
		if(receivedCustomersList[receivedCustomersList.length - 1] == null)
			return false;
		
		for(Loan l : receivedLoansList)
			if(l == null)
				return false;
		
		for(Customer c : receivedCustomersList)
			if(c == null)
				return false;

		return true;
	}
	
	private void requestMissingPacket() {		
		int nCount = 0;
		for(Loan l : receivedLoansList)
		{
			if(l == null)
			{
				SynchronizeLoan udpMsgLoan = new SynchronizeLoan(
						Env.getMachineName(),
						this.bank.getServerName(),
						null, 
						nCount++, 
						receivedLoansList.length, 
						true, 
						0, 
						0);
				try {
					send(new UDPMessage(udpMsgLoan), requestAddr, requestPort);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		nCount = 0;
		for(Customer c : receivedCustomersList)
		{
			if(c == null)
			{
				SynchronizeCustomer udpMsgCustomer = new SynchronizeCustomer(
						Env.getMachineName(),
						this.bank.getServerName(),
						null, 
						nCount++, 
						receivedCustomersList.length, 
						true, 
						0, 
						0);
				try {
					send(new UDPMessage(udpMsgCustomer), requestAddr, requestPort);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void appendNextUdpMessageReceived(DatagramPacket receivedDatagram, UDPMessage udpMessage) 
	{
		synchronized(this.receivedUdpMessageList)
		{
			this.receivedUdpMessageList.add(udpMessage);
			this.receivedDatagramList.add(receivedDatagram);
		}
	}
		
	/**
	 * Wait until there more data to process
	 */
	public void send(UDPMessage message) throws Exception
	{
		send(message, receivedDatagram.getAddress(), receivedDatagram.getPort());
	}
	
	/**
	 * Wait until there more data to process
	 */
	public void send(UDPMessage message, InetAddress addr, int port) throws Exception
	{
		byte[] response = Serializer.serialize(message);
		DatagramPacket reply = new DatagramPacket(response, response.length, addr, port);
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
		
	@Override
	public void run()
	{
		try
		{
			while(this.receivedUdpMessage != null)
			{
				UDPMessage udpMessage = this.receivedUdpMessage;
				processRequest(udpMessage);
				
				// process request that are in queue
				this.receivedUdpMessage = null;
				this.receivedDatagram = null;
				synchronized(this.receivedUdpMessageList)
				{
					if(this.receivedUdpMessageList.size() > 0)
					{
						this.receivedUdpMessage = this.receivedUdpMessageList.remove(0);
						this.receivedDatagram = this.receivedDatagramList.remove(0);
					}
				}
			}
		}
		catch (Exception e)
		{
			lastError = e;
		}
    }
	
	/**
	 * Method can be overwritten for FE, Sequencer, Replica, ReplicaManager
	 * @param udpMessage
	 * @throws Exception 
	 */
	protected void processRequest(UDPMessage udpMessage) throws Exception
	{
		delaySeconds = 0;
		switch (udpMessage.getOperation())
		{
			case SynchronizeCustomer:
				processRequestCustomer(udpMessage);
				break;
			case SynchronizeLoan:
				processRequestLoan(udpMessage);
				break;
			case RequestSynchronize:
				processRequestSynchronize(udpMessage);
				break;
			default:
				System.out.println("Unknown processRequest! " + udpMessage.getOperation().toString());
				break;
		}
		delaySeconds = 0;
		countCanTerminate = 0;
	}
	
	/**
	 * processRequestSynchronize
	 * @param udpMessage
	 * @throws Exception
	 */
	private void processRequestSynchronize(UDPMessage udpMessage) throws Exception
	{
		if(udpMessage.getMessage() instanceof RequestSynchronize)
		{
			RequestSynchronize msg = (RequestSynchronize)udpMessage.getMessage();
			if(this.bank.getServerName().equalsIgnoreCase(msg.getBank()))
			{
				requestAddr = InetAddress.getByName(msg.getIpAddress());
				requestPort = msg.getPort();
				BankState state = bank.getCurrentState();
				List<Loan> loanList = state.getLoanList();
				List<Customer> customerList = state.getCustomerList();
				sentLoansList = new UDPMessage[loanList.size()];
				sentCustomersList = new UDPMessage[customerList.size()];
				int nCount = 0;
				for(Loan loan : loanList)
				{
					SynchronizeLoan udpMsgLoan = new SynchronizeLoan(
							Env.getMachineName(),
							this.bank.getServerName(),
							loan, 
							nCount, 
							loanList.size(), 
							false, 
							state.getNextLoanID(), 
							state.getNextSequenceNumber());

					sentLoansList[nCount] = new UDPMessage(udpMsgLoan);
					send(sentLoansList[nCount++], requestAddr, requestPort);
				}

				nCount = 0;
				for(Customer customer : customerList)
				{
					SynchronizeCustomer udpMsgCustomer = new SynchronizeCustomer(
							Env.getMachineName(),
							this.bank.getServerName(),
							customer, 
							nCount, 
							loanList.size(), 
							false, 
							state.getNextCustomerID(), 
							state.getNextSequenceNumber());

					sentCustomersList[nCount] = new UDPMessage(udpMsgCustomer);
					send(sentCustomersList[nCount++], requestAddr, requestPort);
				}
			}
		}
		else
		{
			throw new Exception("Invalid Object processOpenAccount");
		}
	}

	/**
	 * processRequestLoan
	 * @param udpMessage
	 * @throws Exception
	 */
	private void processRequestLoan(UDPMessage udpMessage) throws Exception
	{
		if(udpMessage.getMessage() instanceof SynchronizeLoan)
		{
			SynchronizeLoan msg = (SynchronizeLoan)udpMessage.getMessage();
			if(this.bank.getServerName().equalsIgnoreCase(msg.getBank()))
			{
				if(msg.isRequested())
				{
					send(sentLoansList[msg.getPosLoan()], requestAddr, requestPort);
				}
				else
				{
					requestAddr = receivedDatagram.getAddress();
					requestPort = receivedDatagram.getPort();
					if(this.receivedLoansList == null)
					{
						this.receivedLoansList = new Loan[msg.getAmountLoans()];
					}
					this.receivedLoansList[msg.getPosLoan()] = msg.getLoan();
					nextLoanID = msg.getNextLoanID();
					nextSequenceID = msg.getNextSequenceID();
					
					verifyDataReceived();
				}
			}
		}
		else
		{
			throw new Exception("Invalid Object processOpenAccount");
		}
	}
	
	/**
	 * processRequestCustomer
	 * @param udpMessage
	 * @throws Exception
	 */
	private void processRequestCustomer(UDPMessage udpMessage) throws Exception
	{
		if(udpMessage.getMessage() instanceof SynchronizeCustomer)
		{
			SynchronizeCustomer msg = (SynchronizeCustomer)udpMessage.getMessage();
			if(this.bank.getServerName().equalsIgnoreCase(msg.getBank()))
			{
				if(msg.isRequested())
				{
					send(sentCustomersList[msg.getPosCustomer()], requestAddr, requestPort);
				}
				else
				{
					requestAddr = receivedDatagram.getAddress();
					requestPort = receivedDatagram.getPort();
					if(this.receivedCustomersList == null)
					{
						this.receivedCustomersList = new Customer[msg.getAmountCustomer()];
					}
					this.receivedCustomersList[msg.getPosCustomer()] = msg.getCustomer();
					verifyDataReceived();
					nextCustomerID = msg.getNextCustomerID();
					nextSequenceID = msg.getNextSequenceID();
				}
			}
		}
		else
		{
			throw new Exception("Invalid Object processOpenAccount");
		}
	}

	/**
	 * Request missing data if last packet was received
	 * if last packet not received, the timer will catch it and request missing data
	 */
	private void verifyDataReceived() 
	{
		if(lastPacketReceived())
		{
			if(canProcessEndOfSynchronise() == false)
			{
				requestMissingPacket();
			}
			else
			{
				terminateThread();
			}
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
