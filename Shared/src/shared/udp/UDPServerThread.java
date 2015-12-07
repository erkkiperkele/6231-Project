package shared.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;
import shared.data.AbstractServerBank;
import shared.util.Env;

/**
 * @author Pascal Tozzi 27664850 UDPServerThread listen asynchronously for request.
 */
public class UDPServerThread implements Runnable
{
	public static final int SIZE_BUFFER_REQUEST = 4000;
	
	private boolean continueUDP = true;
	private Thread t = null;
	
	protected AbstractServerBank bank;
	protected DatagramSocket aSocket = null;

	/**
	 * Constructor
	 * @param nameOfServer
	 * @param port
	 * @throws SocketException
	 */
	public UDPServerThread(String nameOfServer, int port) throws SocketException
	{
		System.out.println("Binding to port " + port + " for " + nameOfServer);
		aSocket = new DatagramSocket(port);
	}
	
	/**
	 * Constructor
	 * @param nameOfServer
	 * @param port
	 * @param serverBank
	 * @throws SocketException
	 */
	public UDPServerThread(String nameOfServer, int port, AbstractServerBank serverBank) throws SocketException
	{
		this(nameOfServer, port);
		this.bank = serverBank;
	}

	/**
	 * Enable to overload the handleRequestThread object
	 * @return
	 */
	public UDPServerHandleRequestThread getUDPServerHandleRequestThread()
	{
		return new UDPServerHandleRequestThread();
	}

	/**
	 * Stop the server thread
	 */
	public void stop()
	{
		continueUDP = false;
	}

	/**
	 * Start the server thread
	 */
	public void start()
	{
		t = new Thread(this);
		t.start();
	}

	/**
	 * Thread
	 */
	public void run()
	{
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
		        
		        Env.log("Received Packet: " + udpMessage.getOperation().toString() + " From " + request.getAddress().getHostAddress() + ":" + request.getPort() + " Seq:" + udpMessage.getSequenceNumber());
		        // If the replied message need to be overwritten, we execute it now
		        udpMessage.executeAddressOverwrite(request);
		        

		        // Get the Key used to continue the previous communication
		        // depending on if the message is from the FrontEnd or between banks		        
				String key = request.getAddress().getHostAddress() + ":" + request.getPort() + " seq:" + udpMessage.getSequenceNumber();
				UDPServerHandleRequestThread client;
				synchronized (dicHandleRequest)
				{
					if(udpMessage.getSequenceNumber() < 0 && !udpMessage.isClientMessage())
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
	
	/**
	 * Execute a join on the thread
	 * @throws InterruptedException 
	 */
	public void join() throws InterruptedException 
	{
		if(t == null)
			return;
		
		t.join();
	}
}
