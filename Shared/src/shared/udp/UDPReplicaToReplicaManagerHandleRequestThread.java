package shared.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
	private boolean waitForNextDatagram = true;
	private DatagramPacket receivedDatagram;
	private UDPMessage receivedUdpMessage;
	private DatagramSocket aSocket;
	private HashMap<String, UDPReplicaToReplicaManagerHandleRequestThread> dicHandleRequest;
	private Exception lastError = null;
	protected AbstractServerBank bank;
	private Loan[] receivedLoansList = null;
	private Customer[] receivedCustomersList = null;
	private Timer timer;
	private int delaySeconds = 0;
	
	public void initialize(String key, AbstractServerBank bank, DatagramSocket aSocket,
			DatagramPacket request, UDPMessage udpMessage, HashMap<String, 
			UDPReplicaToReplicaManagerHandleRequestThread> dicHandleRequest) {
		this.dicHandleRequest = dicHandleRequest;
		this.aSocket = aSocket;
		this.bank = bank;
		this.receivedDatagram = request;
		this.receivedUdpMessage = udpMessage;
		this.key = key;
		this.timer = new Timer();		
		
		UDPReplicaToReplicaManagerHandleRequestThread current = this;
		timer.schedule(new TimerTask() {

            @Override
            public void run() {
            	delaySeconds++;
            	if(delaySeconds > 3)
            	{
                	current.timerExec();
                	delaySeconds = 0;
            	}
            }
        }, 0, 1000);
		
		t = new Thread(this);
		t.start();
	}
	
	public void timerExec() {
		timer.cancel();
		timer = null;
		
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
		OperationType operationType = null;
		try
		{
			UDPMessage udpMessage = this.receivedUdpMessage;
			operationType = udpMessage.getOperation();
			processRequest(udpMessage);
		}
		catch (Exception e)
		{
			lastError = e;
		}
		finally
		{
			if(dicHandleRequest != null 
				&& operationType == OperationType.RequestSynchronize)
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
				InetAddress addr = InetAddress.getByName(msg.getIpAddress());
				int port = msg.getPort();
				BankState state = bank.getCurrentState();
				List<Loan> loanList = state.getLoanList();
				List<Customer> customerList = state.getCustomerList();

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
					
					send(new UDPMessage(udpMsgLoan), addr, port);
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
					
					send(new UDPMessage(udpMsgCustomer), addr, port);
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
				if(this.receivedLoansList == null)
				{
					this.receivedLoansList = new Loan[msg.getAmountLoans()];
				}
				this.receivedLoansList[msg.getPosLoan()] = msg.getLoan();
			}
			this.send(udpMessage);
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
				if(this.receivedCustomersList == null)
				{
					this.receivedCustomersList = new Customer[msg.getAmountCustomer()];
				}
				this.receivedCustomersList[msg.getPosCustomer()] = msg.getCustomer();
			}
			this.send(udpMessage);
		}
		else
		{
			throw new Exception("Invalid Object processOpenAccount");
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
