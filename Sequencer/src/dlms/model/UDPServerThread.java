package dlms.model;

import java.net.SocketException;

import shared.data.AbstractServerBank;
import shared.data.Bank;
import shared.udp.UDPMessage;
import shared.udp.message.client.OpenAccountMessage;

/**
 * @author Pascal Tozzi 27664850
 * UDPServerThread
 * Process all message for the sequencer
 */
public class UDPServerThread extends shared.udp.UdpReplicaServiceThread
{
	/**
	 * Constructor
	 * @param nameOfServer
	 * @param port
	 * @throws SocketException
	 */
	public UDPServerThread(String nameOfServer, int port) throws SocketException 
	{
		super(nameOfServer, port);
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
		super(nameOfServer, port, serverBank);
	}
	
	/**
	 * Override the object created to handle the request
	 */
	@Override
	public UDPServerHandleRequestThread getUDPServerHandleRequestThread()
	{
		return new UDPServerHandleRequestThread();
	}

	public void executeTestMessage() {
		UDPServerHandleRequestThread thread = getUDPServerHandleRequestThread();
		thread.initialize("", bank, aSocket, null, new UDPMessage(new OpenAccountMessage(Bank.Dominion.toString(), "Pascal", "Tozzi", "ptozzi@example.com", "555-555-5555", "123456")), null);
		thread.join();
		
		for(int i = 0; i < 1000; i++)
		{
			thread = getUDPServerHandleRequestThread();
			thread.initialize("", bank, aSocket, null, new UDPMessage(new OpenAccountMessage(Bank.Dominion.toString(), "Pascal" + i, "Tozzi" + i, "ptozzi@example.com" + i, "555-555-5555", "123456" + i)), null);
			thread.join();
		}

		for(int i = 0; i < 1000; i++)
		{
			thread = getUDPServerHandleRequestThread();
			thread.initialize("", bank, aSocket, null, new UDPMessage(new OpenAccountMessage(Bank.National.toString(), "Pascal" + i, "Tozzi" + i, "ptozzi@example.com" + i, "555-555-5555", "123456" + i)), null);
			thread.join();
		}

		for(int i = 0; i < 1000; i++)
		{
			thread = getUDPServerHandleRequestThread();
			thread.initialize("", bank, aSocket, null, new UDPMessage(new OpenAccountMessage(Bank.Royal.toString(), "Pascal" + i, "Tozzi" + i, "ptozzi@example.com" + i, "555-555-5555", "123456" + i)), null);
			thread.join();
		}
	}
}
