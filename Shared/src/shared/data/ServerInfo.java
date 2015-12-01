package shared.data;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

import shared.util.Constant;

/**
 * @author Pascal Tozzi 27664850 Information related to the Bank server Name,
 *         udp port, rmi port, hostname...
 */
public class ServerInfo
{
	private String serverName;
	private String ipAddress;
	private int port;

	/**
	 * @return the serverName
	 */
	public String getServerName()
	{
		return serverName;
	}

	/**
	 * @param serverName
	 *            the serverName to set
	 */
	public void setServerName(String serverName)
	{
		this.serverName = serverName;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress()
	{
		return ipAddress;
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the port
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * @return if the service is already running
	 */
	public boolean isServiceOpened()
	{
		return !isPortAvailable(getPort());
	}

	/**
	 * Source: http://stackoverflow.com/questions/434718/sockets-discover-port-
	 * availability-using-java Checks to see if a specific port is available.
	 * 
	 * @param port
	 *            the port to check for availability
	 */
	public static boolean isPortAvailable(int port)
	{
		if (port < Constant.MIN_PORT_NUMBER || port > Constant.MAX_PORT_NUMBER)
		{
			throw new IllegalArgumentException("Invalid start port: " + port);
		}

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try
		{
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		}
		catch (IOException e)
		{
		}
		finally
		{
			if (ds != null)
			{
				ds.close();
			}

			if (ss != null)
			{
				try
				{
					ss.close();
				}
				catch (IOException e)
				{
					/* should not be thrown */
				}
			}
		}

		return false;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

	@Override
	public String toString()
	{
		return getServerName() + " [Service Status: " + (isServiceOpened() ? "Currently Running" : "Not Available") + "]";
	}
}
