package dlms.replica;

import java.net.InetSocketAddress;

import javax.xml.ws.Endpoint;

import dlms.replica.server.BankReplicaStubGroup;
import dlms.replica.server.BankReplicaManager;
import dlms.replica.server.BankReplicaStub;

/**
 * Main launcher class for the comp6231 assignment #2
 * 
 * @author mat
 * 
 */
public class ServerApp implements Runnable {

	public static final String ENDPOINT_URL = "http://localhost:8080/ws/";
	
	protected BankReplicaStubGroup replicaGroup;

	/**
	 * The application launcher. Starts either as a standalone process or can be
	 * run as a thread
	 * 
	 * @param args
	 */
	public static void main(String args[]) {

		ServerApp server = new ServerApp();
		server.run();
	}

	/**
	 * Create the banks and publish the web service and wait for requests
	 */
	@Override
	public void run() {

		this.replicaGroup = new BankReplicaStubGroup();
		
		// Establish the bank details
		this.replicaGroup.put("rbc", new BankReplicaStub("rbc", new InetSocketAddress("localhost", 10101)));
		this.replicaGroup.put("cibc", new BankReplicaStub("cibc", new InetSocketAddress("localhost", 10102)));
		this.replicaGroup.put("bmo", new BankReplicaStub("bmo", new InetSocketAddress("localhost", 10103)));

		// Send the bank details to the replica manager so it can create the bank replicas
		BankReplicaManager replicaManager = new BankReplicaManager(this.replicaGroup);
		
		// Publish the endpoint and wait for requests
		Endpoint.publish(ENDPOINT_URL, replicaManager);
	}
	
}