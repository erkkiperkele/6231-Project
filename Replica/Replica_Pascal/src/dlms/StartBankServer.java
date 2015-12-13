package dlms;

import java.util.Scanner;
import java.util.logging.Level;

import dlms.model.*;
import shared.data.ServerInfo;
import shared.udp.UdpDbSynchronizationServiceThread;
import shared.util.Constant;
import shared.util.Env;

/**
 * @author Pascal Tozzi 27664850 Entry application with console interface to
 *         start the Bank server service.
 */
public class StartBankServer
{
	/**
	 * Initialize the bank server and it services
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		Env.loadSettings();
		if(args.length > 0)
		{
			shared.data.Bank bank = shared.data.Bank.fromString(args[0]);
			if(bank != null && bank != shared.data.Bank.None && bank != Env.getCurrentBank())
			{
				// Bank has been overloaded
				Env.setCurrentBank(bank);
			}
		}
		Env.log("Server Started <" + Env.getMachineName() + "> " + Env.getCurrentBank());

		ServerInfo server = Env.getReplicaServerInfo();
		if(serverIsValid(server))
		{
			try
			{
				// Start instance listening on UDP
				ServerBank bankServer = new ServerBank(server, true);
				UdpDbSynchronizationServiceThread replicaManager = new UdpDbSynchronizationServiceThread(bankServer);
				Env.log(String.format("[UDP] SYNC SERVER STARTED (Pascal Implementation)"));
				UDPIntranetServerThread intranetServer = new UDPIntranetServerThread(bankServer);
				intranetServer.start();
				replicaManager.start();
				bankServer.waitUntilUDPServiceEnd();
				replicaManager.join();
				intranetServer.join();
			}
			catch (Exception e)
			{
				// Most likely an InterruptedException has been raised
				Env.log(Level.SEVERE, "(port #" + server.getPort() + ") Exception in StartBankServer: " + e);
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
	
	/**
	 * getServerInformation
	 * 
	 * @param bankName
	 *            from the Main method
	 * @return the selected ServerInfo or null
	 */
	public static ServerInfo getServerInformation(String bankName)
	{
		if (Env.getReplicaServerInfoList() == null || Env.getReplicaServerInfoList().size() == 0)
			return null;

		ServerInfo result = null;

		boolean bFoundBank = false;
		if (bankName != null && bankName.isEmpty() == false)
		{
			shared.data.Bank bank = shared.data.Bank.fromString(bankName);
			ServerInfo sv = Env.getReplicaServerInfo(bank);
			if (sv != null)
			{
				result = sv;
				bFoundBank = true;
			}
			else
			{
				System.out.println("Invalid choice.\n\n");
			}
		}

		if (bFoundBank == false)
		{
			Scanner in = new Scanner(System.in);
			while (bFoundBank == false)
			{
				System.out.println("Please select the bank from the list:");
				for (int i = 0; i < Env.getReplicaServerInfoList().size(); i++)
				{
					System.out.println(" - " + Env.getReplicaServerInfoList().get(i).toString());
				}

				System.out.print("Insert your choice: ");
				bankName = in.nextLine();
				if (bankName.isEmpty())
				{
					break;
				}
				else
				{
					shared.data.Bank bank = shared.data.Bank.fromString(bankName);
					ServerInfo sv = Env.getReplicaServerInfo(bank);
					if (sv != null)
					{
						result = sv;
						bFoundBank = true;
					}
					else
					{
						System.out.println("Invalid choice.\n\n");
					}
				}
			}
			in.close();
		}

		return result;
	}
}