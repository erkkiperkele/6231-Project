package dlms;

import java.util.Calendar;
import java.util.Scanner;

import com.sun.javafx.scene.traversal.SubSceneTraversalEngine;
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

		if (args == null || args.length == 0)
		{
			Env.setDebug(true);
		}

		String bankName = "RBC";
		if (args.length > 0)
		{
			bankName = args[0];
		}
		ServerInfo server = getServerInformation(bankName);

		if (server == null)
		{
			return;
		}
		else if (server.isServiceOpened())
		{
			System.out.println("Service is currently running on the UDP port " + server.getPort());
			System.out.println("Change the port and try again or close the service instance.");
			return;
		}

		try
		{
			// Start instance listening on UDP
			System.out.println("TODO: start listening on UDP!!");
			while (true){
				System.out.println(Calendar.getInstance().getTime() + " - DUMMY Server: just a while true");
				Thread.sleep(5000);
			}
		}
		catch (Exception e)
		{
			System.out.println("Exception in StartBankServer: " + e);
		}
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
