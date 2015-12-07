package shared.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.HashMap;

import shared.data.AbstractServerBank;
import shared.data.ServerInfo;
import shared.udp.message.client.RequestSynchronize;
import shared.util.Env;

public class UDPReplicaToReplicaManagerThread implements Runnable
{
	public static final int SIZE_BUFFER_REQUEST = 4000;
	
	private Thread t;
	private boolean continueUDP = true;
	private AbstractServerBank bank;
	private DatagramSocket aSocket;
	
	public UDPReplicaToReplicaManagerThread(AbstractServerBank bank) throws Exception
	{
		t = new Thread(this);
		this.bank = bank;
		ServerInfo sv = Env.getReplicaToReplicaManagerServerInfo();
		System.out.println("Binding to port " + sv.getPort() + " for " + sv.getIpAddress());
		aSocket = new DatagramSocket(sv.getPort());
	}

	@Override
	public void run() {
		try
		{
			byte[] buffer = new byte[SIZE_BUFFER_REQUEST];

			// HashMap isn't thread safe, but HashTable is thread safe... it's
			// fine, we will synchronize it ;)
			HashMap<String, UDPReplicaToReplicaManagerHandleRequestThread> dicHandleRequest = new HashMap<String, UDPReplicaToReplicaManagerHandleRequestThread>();
			while (continueUDP)
			{
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				byte[] message = Arrays.copyOf(request.getData(), request.getLength());
		        UDPMessage udpMessage = Serializer.deserialize(message);

		        // Get the Key used to continue the previous communication
		        // depending on if the message is from the FrontEnd or between banks		        
				String key;
				key = request.getAddress().getHostAddress() + ":" + request.getPort();
				if(udpMessage.getOperation() == OperationType.RequestSynchronize)
				{
					// we take the destination IP in case it a request instead to the one who talked to us
					RequestSynchronize r = (RequestSynchronize)udpMessage.getMessage();
					key = r.getIpAddress() + ":" + r.getPort();
				}
				UDPReplicaToReplicaManagerHandleRequestThread client;
				synchronized (dicHandleRequest)
				{
					if (!dicHandleRequest.containsKey(key))
					{
						client = new UDPReplicaToReplicaManagerHandleRequestThread();
						client.initialize(key, bank, aSocket, request, udpMessage, dicHandleRequest);
						dicHandleRequest.put(key, client);
					}
					else
					{
						client = dicHandleRequest.get(key);
						client.appendNextUdpMessageReceived(request, udpMessage);
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Error UDP service: " + e.getMessage());
		}
		finally
		{
			if (aSocket != null)
			{
				aSocket.close();
			}
			t = null;
		}
	}

	public void start() {
		t.start();
	}

	public void join() throws InterruptedException {
		t.join();
	}
}
