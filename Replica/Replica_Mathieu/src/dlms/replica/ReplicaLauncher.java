package dlms.replica;

import java.net.InetSocketAddress;

import dlms.replica.server.BankReplica;
import dlms.replica.server.BankReplicaStub;
import dlms.replica.server.BankReplicaStubGroup;
import shared.data.Bank;

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

		String bankName = "";
		BankReplicaStubGroup replicaStubs = new BankReplicaStubGroup();
		replicaStubs.put(Bank.Royal.name(), new BankReplicaStub(Bank.Royal.name(), new InetSocketAddress("localhost", 10101)));
		replicaStubs.put(Bank.National.name(), new BankReplicaStub(Bank.National.name(), new InetSocketAddress("localhost", 10102)));
		replicaStubs.put(Bank.Dominion.name(), new BankReplicaStub(Bank.Dominion.name(), new InetSocketAddress("localhost", 10103)));

		if (args.length < 1 || !replicaStubs.containsKey(args[0])) {
			bankName = Bank.Royal.name();
			System.out.println("Missing valid argument 1, using default argument: Royal");
		}
		else{
			bankName = args[0];
		}


		new BankReplica(bankName, replicaStubs);
	}
}
