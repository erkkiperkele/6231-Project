package shared.udp;

import java.net.SocketAddress;

/**
 * @author Pascal Tozzi 27664850
 * ReplicaInfo contain the ip and port of a replica
 */
public class ReplicaInfo 
{
	private SocketAddress ip;
	private int port;
	
	public ReplicaInfo(SocketAddress ip, int port)
	{
		this.ip = ip;
		this.port = port;
	}
	
	public int getPort() 
	{
		return this.port;
	}

	public SocketAddress getAddress() 
	{
		return this.ip;
	}
}
