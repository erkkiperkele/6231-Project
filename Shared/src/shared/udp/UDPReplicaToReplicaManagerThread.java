package shared.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.HashMap;

import shared.data.AbstractServerBank;
import shared.data.ServerInfo;
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
		t = new Thread();
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
			HashMap<String, UDPServerHandleRequestThread> dicHandleRequest = new HashMap<String, UDPServerHandleRequestThread>();
			while (continueUDP)
			{
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				byte[] message = Arrays.copyOf(request.getData(), request.getLength());
		        UDPMessage udpMessage = Serializer.deserialize(message);

		        // Get the Key used to continue the previous communication
		        // depending on if the message is from the FrontEnd or between banks		        
				String key = request.getAddress().getHostAddress() + ":" + request.getPort();
				UDPServerHandleRequestThread client;
				synchronized (dicHandleRequest)
				{
					/*if(udpMessage.getSequenceNumber() < 0 && !udpMessage.isClientMessage())
					{
						getUDPServerHandleRequestThread()
							.initialize(key, bank, aSocket, request, udpMessage, dicHandleRequest);
					}
					else if (!dicHandleRequest.containsKey(key))
					{
						client = getUDPServerHandleRequestThread();
						client.initialize(key, bank, aSocket, request, udpMessage, dicHandleRequest);
						dicHandleRequest.put(key, client);
					}
					else
					{
						client = dicHandleRequest.get(key);
						client.resumeNextUdpMessageReceived(request, udpMessage);
					}*/
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
