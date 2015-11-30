package dlms.model;

import java.net.SocketException;
import shared.data.AbstractServerBank;

/**
 * @author Pascal Tozzi 27664850 UDPServerThread listen asynchronously for
 *         request to do credit check.
 */
public class UDPServerThread extends shared.udp.UDPServerThread
{

	public UDPServerThread(String nameOfServer, int port) throws SocketException 
	{
		super(nameOfServer, port);
	}
	
	public UDPServerThread(String nameOfServer, int port, AbstractServerBank serverBank) throws SocketException 
	{
		super(nameOfServer, port, serverBank);
	}

	@Override
	public UDPServerHandleRequestThread getUDPServerHandleRequestThread()
	{
		return new UDPServerHandleRequestThread();
	}
}
