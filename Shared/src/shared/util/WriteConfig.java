package shared.util;

import java.io.*;
import java.util.Properties;

import shared.data.Bank;
import shared.data.ServerPorts;

public class WriteConfig
{
	public static boolean InitServerDefaultSettings()
	{
		boolean areSettingsCreated = false;
		Properties prop = new Properties();
		OutputStream output = null;

		try
		{
			output = new FileOutputStream("config.properties");

			// Used to translate all the port of X
			// There 4 replicas implementations
			prop.setProperty("port-overload-replica-name", "aymeric");
			prop.setProperty("port-overload-increase-aymeric", "0");
			prop.setProperty("port-overload-increase-mathieu", "10");
			prop.setProperty("port-overload-increase-pascal", "20");
			prop.setProperty("port-overload-increase-richard", "30");

			// ip address
			prop.setProperty("bank-server-ip-aymeric", "localhost");
			prop.setProperty("bank-server-ip-mathieu", "localhost");
			prop.setProperty("bank-server-ip-pascal", "localhost");
			prop.setProperty("bank-server-ip-richard", "localhost");

			// Front-End
			prop.setProperty("bank-front-end-ip", "localhost");
			prop.setProperty("bank-front-end-port", "4100");
			
			// Sequencer
			prop.setProperty("bank-sequencer-ip", "localhost");
			prop.setProperty("bank-sequencer-port", "4101");
			
			// Set the properties value
			// Dominion Server Settings for UDP connection
			prop.setProperty("bank-server-name1", Bank.Dominion.toString());
			prop.setProperty("bank-server-replica-port1", String.valueOf(ServerPorts.getUDPPort(Bank.Dominion)));
			prop.setProperty("bank-server-intranet-port1", String.valueOf(ServerPorts.getUDPPortIntranet(Bank.Dominion)));
			prop.setProperty("bank-server-replicatemanager-port1", String.valueOf(ServerPorts.getUDPPortReplicaManager(Bank.Dominion)));
			// National Server Settings for UDP connection
			prop.setProperty("bank-server-name2", Bank.National.toString());
			prop.setProperty("bank-server-replica-port2", String.valueOf(ServerPorts.getUDPPort(Bank.National)));
			prop.setProperty("bank-server-intranet-port2", String.valueOf(ServerPorts.getUDPPortIntranet(Bank.National)));
			prop.setProperty("bank-server-replicatemanager-port2", String.valueOf(ServerPorts.getUDPPortReplicaManager(Bank.National)));
			// Royal Server Settings for UDP connection
			prop.setProperty("bank-server-name3", Bank.Royal.toString());
			prop.setProperty("bank-server-replica-port3", String.valueOf(ServerPorts.getUDPPort(Bank.Royal)));
			prop.setProperty("bank-server-intranet-port3", String.valueOf(ServerPorts.getUDPPortIntranet(Bank.Royal)));
			prop.setProperty("bank-server-replicatemanager-port3", String.valueOf(ServerPorts.getUDPPortReplicaManager(Bank.Royal)));

			// Log
			prop.setProperty("log-console-enabled", "FALSE");
			prop.setProperty("log-file-enabled", "TRUE");
			prop.setProperty("log-file-path", "./log/logclient.txt");
			prop.setProperty("log-level", "ALL");

			// save properties to project root folder
			prop.store(output, null);
			areSettingsCreated = true;
		}
		catch (IOException io)
		{
			io.printStackTrace();
		}
		finally
		{
			if (output != null)
			{
				try
				{
					output.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		if (areSettingsCreated == false)
		{
			System.out.println("Failed to create config.properties.");
			System.out.println("Make sure you have admin privilege and try again.");
		}

		return areSettingsCreated;
	}
}
