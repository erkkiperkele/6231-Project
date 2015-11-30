package dlms.replica;

import java.net.InetSocketAddress;

import dlms.replica.server.BankReplica;
import dlms.replica.server.BankReplicaStub;
import dlms.replica.server.BankReplicaStubGroup;

/**
 * Launches a bank replica server
 * 
 * @author mat
 *
 */
public class ReplicaLauncher {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Establish the bank details and add them all to a group
		// this will be passed to each replica so that they know of each others'
		// existence
		BankReplicaStubGroup replicaStubs = new BankReplicaStubGroup();
		replicaStubs.put("rbc", new BankReplicaStub("rbc", new InetSocketAddress("localhost", 10101)));
		replicaStubs.put("cibc", new BankReplicaStub("cibc", new InetSocketAddress("localhost", 10102)));
		replicaStubs.put("bmo", new BankReplicaStub("bmo", new InetSocketAddress("localhost", 10103)));

		if (args.length < 1 || !replicaStubs.containsKey(args[0])) {
			System.out.println("Missing valid argument 1: must be one of rbc, cibc or bmo");
			System.exit(1);
		}

		new BankReplica(args[0], replicaStubs);
	}
}
