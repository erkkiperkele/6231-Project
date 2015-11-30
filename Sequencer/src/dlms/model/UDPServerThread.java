package dlms.model;

import java.net.SocketException;
import java.util.ArrayList;

import shared.data.AbstractServerBank;
import shared.udp.ReplicaInfo;

/**
 * @author Pascal Tozzi 27664850
 * UDPServerThread
 * Process all message for the sequencer
 */
public class UDPServerThread extends shared.udp.UDPServerThread
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
		initialize();
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
		initialize();
	}
	
	/**
	 * Initialize the replica information for the multicast
	 */
	public void initialize()
	{
		replicaList = new ArrayList<ReplicaInfo>();
		
		/// TODO: Need to add all the replica IP and PORT for the multicast here. 
		System.out.println("TODO: Need to add all the replica IP and PORT for the multicast here.");
		
		//this.replicaList.add(new ReplicaInfo(null, 5000));
		//this.replicaList.add(new ReplicaInfo(null, 5001));
		//this.replicaList.add(new ReplicaInfo(null, 5002));
	}

	/**
	 * Override the object created to handle the request
	 */
	@Override
	public UDPServerHandleRequestThread getUDPServerHandleRequestThread()
	{
		return new UDPServerHandleRequestThread();
	}
}
