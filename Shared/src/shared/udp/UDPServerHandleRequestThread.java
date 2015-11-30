package shared.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.List;

import shared.data.AbstractServerBank;

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
	private List<ReplicaInfo> replicaList = null;
	protected AbstractServerBank bank;
	
	public void initialize(String key, AbstractServerBank bank, DatagramSocket aSocket,
			DatagramPacket request, UDPMessage udpMessage, HashMap<String, 
			UDPServerHandleRequestThread> dicHandleRequest, List<ReplicaInfo> replicaList) {
		this.dicHandleRequest = dicHandleRequest;
		this.aSocket = aSocket;
		this.bank = bank;
		this.receivedDatagram = request;
		this.receivedUdpMessage = udpMessage;
		this.key = key;
		this.replicaList = replicaList;
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
		for(ReplicaInfo replica : replicaList)
		{
			DatagramPacket reply = new DatagramPacket(response, response.length, replica.getPort(), replica.getAddress());
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
