package impl;

import java.util.logging.Level;

import shared.data.Bank;
import shared.data.ServerInfo;
import shared.udp.UdpDbSynchronizationServiceThread;
import shared.util.Env;

/**
 * InitServers: Creates banks and binds BankServers to ports.
 * @author richard
 */

public class InitServers {
	public static void main(String[] args) {
		Env.loadSettings();
		if (args.length > 0) {
			Bank bank = Bank.fromString(args[0]);
			if(bank != null && bank != shared.data.Bank.None && bank != Env.getCurrentBank())
			{
				Env.setCurrentBank(bank);
			}
		}
		ServerInfo server = Env.getReplicaServerInfo();
		if (serverIsValid(server)) {
			try {
				BankServer bankServer = new BankServer(new BankStore(Env.getCurrentBank()), server);
				UdpDbSynchronizationServiceThread replicaManager = new UdpDbSynchronizationServiceThread(bankServer);
				replicaManager.start();
				bankServer.waitUntilFinished();
				replicaManager.join();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * Validate if the server is valid and show the corresponding error message
	 * @param server
	 * @return true or false depending on if the server can be executed or not
	 */
	private static boolean serverIsValid(ServerInfo server) 
	{
		boolean isServerValid = true;
		if (server == null)
		{
			Env.log(Level.SEVERE, "Invalid server name!");
			isServerValid = false;
		}
		else if (server.isServiceOpened())
		{
			Env.log(Level.SEVERE, "Service is currently running on the UDP port " + server.getPort());
			Env.log(Level.SEVERE, "Change the port and try again or close the service instance.");
			isServerValid = false;
		}
		return isServerValid;
	}
}
