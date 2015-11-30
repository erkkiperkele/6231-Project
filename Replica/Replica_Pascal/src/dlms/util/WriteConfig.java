package dlms.util;

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

			// Set the properties value
			// Dominion Server Settings for UDP connection
			prop.setProperty("bank-server-name1", Bank.Dominion.toString());
			prop.setProperty("bank-server-ip1", "localhost");
			prop.setProperty("bank-server-port1", String.valueOf(ServerPorts.getUDPPort(Bank.Dominion)));
			// National Server Settings for UDP connection
			prop.setProperty("bank-server-name2", Bank.National.toString());
			prop.setProperty("bank-server-ip2", "localhost");
			prop.setProperty("bank-server-port2", String.valueOf(ServerPorts.getUDPPort(Bank.National)));
			// Royal Server Settings for UDP connection
			prop.setProperty("bank-server-name3", Bank.Royal.toString());
			prop.setProperty("bank-server-ip3", "localhost");
			prop.setProperty("bank-server-port3", String.valueOf(ServerPorts.getUDPPort(Bank.Royal)));

			// Log
			prop.setProperty("log-console-enabled", "FALSE");
			prop.setProperty("log-file-enabled", "TRUE");
			prop.setProperty("log-file-path", "./logclient.txt");
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
