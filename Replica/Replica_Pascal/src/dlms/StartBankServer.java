package dlms;

import java.util.Scanner;
import java.util.logging.Level;

import dlms.model.*;
import dlms.util.Env;

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
		// Load server settings
		Env.loadServerSettings("BankServer");

		String bankName;
		if (args == null || args.length == 0)
		{
			Env.setDebug(true);
			bankName = shared.data.Bank.Royal.toString();
		}
		else
		{
			bankName = args[0];
		}
		
		ServerInfo server = getServerInformation(bankName);
		if(serverIsValid(server))
		{
			try
			{
				// Start instance listening on UDP
				ServerBank bankServer = new ServerBank(server, true);
				bankServer.waitUntilUDPServiceEnd();
			}
			catch (Exception e)
			{
				// Most likely an InterruptedException has been raised
				Env.log(Level.SEVERE, "Exception in StartBankServer: " + e, true);
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
			Env.log(Level.SEVERE, "Invalid server name!", true);
			isServerValid = false;
		}
		else if (server.isServiceOpened())
		{
			Env.log(Level.SEVERE, "Service is currently running on the UDP port " + server.getPort(), true);
			Env.log(Level.SEVERE, "Change the port and try again or close the service instance.", true);
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
		if (Env.getLstServers() == null || Env.getLstServers().size() == 0)
			return null;

		ServerInfo result = null;

		boolean bFoundBank = false;
		if (bankName != null && bankName.isEmpty() == false)
		{
			ServerInfo sv = Env.GetServerInfo(bankName);
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
				for (int i = 0; i < Env.getLstServers().size(); i++)
				{
					System.out.println(" - " + Env.getLstServers().get(i).toString());
				}

				System.out.print("Insert your choice: ");
				bankName = in.nextLine();
				if (bankName.isEmpty())
				{
					break;
				}
				else
				{
					ServerInfo sv = Env.GetServerInfo(bankName);
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