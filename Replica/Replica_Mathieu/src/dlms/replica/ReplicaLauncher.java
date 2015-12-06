package dlms.replica;

import java.net.InetSocketAddress;
import java.util.HashMap;

import dlms.replica.server.BankReplica;
import dlms.replica.server.BankReplicaStub;
import dlms.replica.server.BankReplicaStubGroup;
import shared.data.Bank;
import shared.data.ServerInfo;
import shared.util.Env;

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
		
		String bankName = "";
		
		Env.loadSettings();
		Env.setCurrentBank(Bank.None);
		
		// Establish the bank details and add them all to a group
		// this will be passed to each replica so that they know of each others'
		// existence
		
		ServerInfo bank1Info = Env.getReplicaIntranetServerInfo(Bank.Royal);
		ServerInfo bank2Info = Env.getReplicaIntranetServerInfo(Bank.National);
		ServerInfo bank3Info = Env.getReplicaIntranetServerInfo(Bank.Dominion);
		
		BankReplicaStubGroup replicaStubs = new BankReplicaStubGroup();
		replicaStubs.put(Bank.Royal.name(), new BankReplicaStub(Bank.Royal.name(), new InetSocketAddress(bank1Info.getIpAddress(), bank1Info.getPort())));
		replicaStubs.put(Bank.National.name(), new BankReplicaStub(Bank.National.name(), new InetSocketAddress(bank2Info.getIpAddress(), bank2Info.getPort())));
		replicaStubs.put(Bank.Dominion.name(), new BankReplicaStub(Bank.Dominion.name(), new InetSocketAddress(bank3Info.getIpAddress(), bank3Info.getPort())));

		if (args.length < 1 || !replicaStubs.containsKey(args[0])) {
			bankName = Bank.Royal.name();
			System.out.println("Missing valid argument 1, using default argument: Royal");
		}
		else {
			bankName = args[0];
		}


		new BankReplica(bankName, replicaStubs);
	}
}
