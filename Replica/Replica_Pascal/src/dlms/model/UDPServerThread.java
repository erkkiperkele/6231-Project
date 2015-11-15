package dlms.model;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.logging.Level;

import dlms.util.Constant;
import dlms.util.Env;

/**
 * @author Pascal Tozzi 27664850 UDPServerThread listen asynchronously for
 *         request to do credit check.
 */
public class UDPServerThread implements Runnable
{
	private boolean continueUDP = true;
	private ServerBank bank;
	private DatagramSocket aSocket = null;
	private Thread t = null;

	public UDPServerThread(ServerBank serverBank, int port) throws SocketException
	{
		this.bank = serverBank;
		System.out.println("Binding to port " + port + " for " + serverBank.getName());
		aSocket = new DatagramSocket(port);
	}

	public void stop()
	{
		continueUDP = false;
	}

	public void start()
	{
		t = new Thread(this);
		t.start();
	}

	public void run()
	{
		try
		{
			byte[] buffer = new byte[Constant.SIZE_BUFFER_REQUEST];

			// HashMap isn't thread safe, but HashTable is thread safe... it's
			// fine, we will synchronize it ;)
			HashMap<String, UDPServerHandleRequestThread> dicHandleRequest = new HashMap<String, UDPServerHandleRequestThread>();
			while (continueUDP)
			{
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);

				String key = request.getAddress() + ":" + request.getPort();
				UDPServerHandleRequestThread client;
				synchronized (dicHandleRequest)
				{
					if (!dicHandleRequest.containsKey(key))
					{
						client = new UDPServerHandleRequestThread(key, bank, aSocket, request, dicHandleRequest);
						dicHandleRequest.put(key, client);
					}
					else
					{
						client = dicHandleRequest.get(key);
						client.resumeNextDatagramReceived(request);
					}
				}
			}
		}
		catch (Exception e)
		{
			Env.log(Level.SEVERE, e.getMessage(), false);
			System.out.println("Error UDP service.");
		}
		finally
		{
			if (aSocket != null)
			{
				aSocket.close();
			}
		}
		t = null;
	}
}
