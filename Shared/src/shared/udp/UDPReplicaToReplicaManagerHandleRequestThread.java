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
		//this.timer = new Timer();

		UDPReplicaToReplicaManagerHandleRequestThread current = this;
		/*timer.schedule(new TimerTask() {

            @Override
            public void run() {
            	delaySeconds++;
            	if(delaySeconds > 6)
            	{
                	current.timerExec();
                	delaySeconds = 0;
            	}
            }
        }, 0, 1000);*/
		
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
		//timer.cancel();
		//timer = null;
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
		if(receivedLoansList == null || (receivedLoansList.length > 0 && receivedLoansList[receivedLoansList.length-1] == null))
			return false;
		
		if(receivedCustomersList == null || (receivedCustomersList.length > 0 && receivedCustomersList[receivedCustomersList.length - 1] == null))
			return false;
		
		return true;
	}
	
	private boolean canProcessEndOfSynchronise() {
		if(receivedLoansList == null || (receivedLoansList.length > 0 && receivedLoansList[receivedLoansList.length-1] == null))
			return false;
		
		if(receivedCustomersList == null || (receivedCustomersList.length > 0 && receivedCustomersList[receivedCustomersList.length - 1] == null))
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

		if(receivedLoansList != null)
		{
			for(int i = 0; i < receivedLoansList.length; i++)
			{
				Loan l = receivedLoansList[i];
				if(l == null)
				{
					SynchronizeLoan udpMsgLoan = new SynchronizeLoan(
							Env.getMachineName(),
							this.bank.getServerName(),
							null, 
							i, 
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
		}
		
		if(receivedCustomersList != null)
		{
			synchronized (receivedCustomersList)
			{
				for(int i = 0; i < receivedCustomersList.length; i++)
				{
					Customer c = receivedCustomersList[i];
					if(c == null)
					{
						SynchronizeCustomer udpMsgCustomer = new SynchronizeCustomer(
								Env.getMachineName(),
								this.bank.getServerName(),
								null, 
								i, 
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
			Env.log("Error send " + e.getMessage());
			// handled by the parent function
			throw e;
		}
	}
		
	@Override
	public void run()
	{
		try
		{
			int nCountEndApp = 0;
			while(true)
			{
				UDPMessage udpMessage = this.receivedUdpMessage;
				//Env.log("[Sync] Received " + udpMessage.getOperation().toString() + " from " + receivedDatagram.getAddress().getHostAddress() + ":" + receivedDatagram.getPort());
				processRequest(udpMessage);
				
				// process request that are in queue
				this.receivedUdpMessage = null;
				this.receivedDatagram = null;

				if(this.receivedUdpMessageList.size() == 0)
				{
					Thread.sleep(5);
				}
				synchronized(this.receivedUdpMessageList)
				{
					if(this.receivedUdpMessageList.size() > 0)
					{
						this.receivedUdpMessage = this.receivedUdpMessageList.remove(0);
						this.receivedDatagram = this.receivedDatagramList.remove(0);
					}
				}
				
				if(this.receivedUdpMessage == null)
				{
					nCountEndApp++;
					if(canProcessEndOfSynchronise())
					{
						break;
					}
					if(nCountEndApp > 100)
					{
						requestMissingPacket();
						nCountEndApp = 0;
					}
				}
				else
				{
					nCountEndApp = 0;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			lastError = e;
		}
		Env.log("Thread Ended.");
    }
	
	/**
	 * Method can be overwritten for FE, Sequencer, Replica, ReplicaManager
	 * @param udpMessage
	 * @throws Exception 
	 */
	protected void processRequest(UDPMessage udpMessage) throws Exception
	{
		if(udpMessage == null)
			return;
		
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
				Env.log("Processing RequestSynchronize message...");
				processRequestSynchronize(udpMessage);
				break;
			default:
				Env.log("Unknown Sync processRequest! " + udpMessage.getOperation().toString());
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
		System.err.println("Starting synchronizing");
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
				while(loanList.remove(null));
				while(customerList.remove(null));
				sentLoansList = new UDPMessage[loanList.size()];
				sentCustomersList = new UDPMessage[customerList.size()];
				for(int i = 0; i < loanList.size(); i++)
				{
					Loan loan = loanList.get(i);
					SynchronizeLoan udpMsgLoan = new SynchronizeLoan(
							Env.getMachineName(),
							this.bank.getServerName(),
							loan, 
							i, 
							loanList.size(), 
							false, 
							state.getNextLoanID(), 
							state.getNextSequenceNumber());

					sentLoansList[i] = new UDPMessage(udpMsgLoan);
					Env.log("Send Loan #" + i);
					send(sentLoansList[i], requestAddr, requestPort);
				}
				if(loanList.size() == 0)
				{
					SynchronizeLoan udpMsgLoan = new SynchronizeLoan(
							Env.getMachineName(),
							this.bank.getServerName(),
							null, 
							0, 
							loanList.size(), 
							false, 
							state.getNextLoanID(), 
							state.getNextSequenceNumber());

					Env.log("Send Loan none");
					send(new UDPMessage(udpMsgLoan), requestAddr, requestPort);
				}

				for(int i = 0; i < customerList.size(); i++)
				{
					Customer customer = customerList.get(i);
					SynchronizeCustomer udpMsgCustomer = new SynchronizeCustomer(
							Env.getMachineName(),
							this.bank.getServerName(),
							customer, 
							i, 
							customerList.size(), 
							false, 
							state.getNextCustomerID(), 
							state.getNextSequenceNumber());

					sentCustomersList[i] = new UDPMessage(udpMsgCustomer);
					//Env.log("Send Customer #" + nCount);
					send(sentCustomersList[i], requestAddr, requestPort);
				}
				if(customerList.size() == 0)
				{
					SynchronizeCustomer udpMsgCustomer = new SynchronizeCustomer(
							Env.getMachineName(),
							this.bank.getServerName(),
							null, 
							0, 
							customerList.size(), 
							false, 
							state.getNextCustomerID(), 
							state.getNextSequenceNumber());

					Env.log("Send Customer none");
					send(new UDPMessage(udpMsgCustomer), requestAddr, requestPort);
				}
			}
		}
		else
		{
			throw new Exception("Invalid Object processOpenAccount");
		}
		System.err.println("Ended synchronizing");
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
					Env.log("[Sync] Re-Requestion missing Loan -> #" + msg.getPosLoan() + " of " + msg.getAmountLoans());
					send(sentLoansList[msg.getPosLoan()], requestAddr, requestPort);
				}
				else
				{
					Env.log("[Sync] Received Loan -> #" + msg.getPosLoan() + " of " + msg.getAmountLoans());
					requestAddr = receivedDatagram.getAddress();
					requestPort = receivedDatagram.getPort();
					if(this.receivedLoansList == null)
					{
						this.receivedLoansList = new Loan[msg.getAmountLoans()];
					}
					if(msg.getAmountLoans() > 0)
					{
						if(this.receivedLoansList[msg.getPosLoan()] == null)
						{
							this.receivedLoansList[msg.getPosLoan()] = msg.getLoan();
						}
					}
					nextLoanID = msg.getNextLoanID();
					nextSequenceID = msg.getNextSequenceID();
					
					verifyDataReceived();
				}
			}
		}
		else
		{
			throw new Exception("Invalid Object processRequestLoan");
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
					Env.log("[Sync] Re-Requestion missing Customer -> #" + msg.getPosCustomer() + " of " + msg.getAmountCustomer() + " " + ((SynchronizeCustomer)sentCustomersList[msg.getPosCustomer()].getMessage()).getCustomer().getEmail());
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
					if(msg.getAmountCustomer()>0)
					{
						synchronized (receivedCustomersList)
						{
							if(this.receivedCustomersList[msg.getPosCustomer()] == null)
							{
								Env.log("[Sync] Received Customer #" + msg.getPosCustomer() + " of " + msg.getAmountCustomer() + " " + msg.getCustomer().getEmail());
								this.receivedCustomersList[msg.getPosCustomer()] = msg.getCustomer();
							}
						}
					}
					verifyDataReceived();
					nextCustomerID = msg.getNextCustomerID();
					nextSequenceID = msg.getNextSequenceID();
				}
			}
		}
		else
		{
			throw new Exception("Invalid Object processRequestCustomer");
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
			if(canProcessEndOfSynchronise())
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
