package shared.util;

import java.io.*;
import java.util.Iterator;
import java.util.Properties;

import shared.data.Bank;

public class WriteConfig
{
	/**
	 * Create server settings file
	 * @return true if created successfully
	 */
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
			prop.setProperty("current-machine-name", Constant.MACHINE_NAME_FRONT_END);
			prop.setProperty("current-bank-name", Bank.None.toString());
			
			// Log
			prop.setProperty("log-console-enabled", String.valueOf(Env.isLogConsoleEnabled()).toUpperCase());
			prop.setProperty("log-file-enabled", String.valueOf(Env.islogFileEnabled()).toUpperCase());
			prop.setProperty("log-file-path", Env.getLogPath());

			// Front-End
			prop.setProperty("bank-front-end-ip", Env.getFrontEndServerInfo().getIpAddress());
			prop.setProperty("bank-front-end-port", String.valueOf(Env.getFrontEndServerInfo().getPort()));
			
			// Sequencer
			prop.setProperty("bank-sequencer-ip", Env.getSequencerServerInfo().getIpAddress());
			prop.setProperty("bank-sequencer-port", String.valueOf(Env.getSequencerServerInfo().getPort()));

			int machineCount = 0;
			for (Iterator<String> iterator = Env.getListMachineName(); iterator.hasNext();) {
				String machineName = iterator.next();

				machineCount++;
				prop.setProperty("bank-server-machine-name" + machineCount, machineName);
				prop.setProperty("bank-server-replicatemanager"+machineCount+"-ip", Env.getReplicaManagerServerInfo(machineName).getIpAddress());
				prop.setProperty("bank-server-replicatemanager"+machineCount+"-port", String.valueOf(Env.getReplicaManagerServerInfo(machineName).getPort()));

				String bankName = Bank.Dominion.toString();
				prop.setProperty("bank-server-replica"+machineCount+"-"+bankName+"-ip", Env.getReplicaServerInfo(machineName, Bank.Dominion).getIpAddress());
				prop.setProperty("bank-server-replica"+machineCount+"-"+bankName+"-port", String.valueOf(Env.getReplicaServerInfo(machineName, Bank.Dominion).getPort()));
				prop.setProperty("bank-server-intranet"+machineCount+"-"+bankName+"-ip", Env.getReplicaIntranetServerInfo(machineName, Bank.Dominion).getIpAddress());
				prop.setProperty("bank-server-intranet"+machineCount+"-"+bankName+"-port", String.valueOf(Env.getReplicaIntranetServerInfo(machineName, Bank.Dominion).getPort()));
				prop.setProperty("bank-server-replica-rm"+machineCount+"-"+bankName+"-ip", Env.getReplicaToReplicaManagerServerInfo(machineName, Bank.Dominion).getIpAddress());
				prop.setProperty("bank-server-replica-rm"+machineCount+"-"+bankName+"-port", String.valueOf(Env.getReplicaToReplicaManagerServerInfo(machineName, Bank.Dominion).getPort()));
				
				bankName = Bank.National.toString();
				prop.setProperty("bank-server-replica"+machineCount+"-"+bankName+"-ip", Env.getReplicaServerInfo(machineName, Bank.National).getIpAddress());
				prop.setProperty("bank-server-replica"+machineCount+"-"+bankName+"-port", String.valueOf(Env.getReplicaServerInfo(machineName, Bank.National).getPort()));
				prop.setProperty("bank-server-intranet"+machineCount+"-"+bankName+"-ip", Env.getReplicaIntranetServerInfo(machineName, Bank.National).getIpAddress());
				prop.setProperty("bank-server-intranet"+machineCount+"-"+bankName+"-port", String.valueOf(Env.getReplicaIntranetServerInfo(machineName, Bank.National).getPort()));
				prop.setProperty("bank-server-replica-rm"+machineCount+"-"+bankName+"-ip", Env.getReplicaToReplicaManagerServerInfo(machineName, Bank.National).getIpAddress());
				prop.setProperty("bank-server-replica-rm"+machineCount+"-"+bankName+"-port", String.valueOf(Env.getReplicaToReplicaManagerServerInfo(machineName, Bank.National).getPort()));
				
				bankName = Bank.Royal.toString();
				prop.setProperty("bank-server-replica"+machineCount+"-"+bankName+"-ip", Env.getReplicaServerInfo(machineName, Bank.Royal).getIpAddress());
				prop.setProperty("bank-server-replica"+machineCount+"-"+bankName+"-port", String.valueOf(Env.getReplicaServerInfo(machineName, Bank.Royal).getPort()));
				prop.setProperty("bank-server-intranet"+machineCount+"-"+bankName+"-ip", Env.getReplicaIntranetServerInfo(machineName, Bank.Royal).getIpAddress());
				prop.setProperty("bank-server-intranet"+machineCount+"-"+bankName+"-port", String.valueOf(Env.getReplicaIntranetServerInfo(machineName, Bank.Royal).getPort()));
				prop.setProperty("bank-server-replica-rm"+machineCount+"-"+bankName+"-ip", Env.getReplicaToReplicaManagerServerInfo(machineName, Bank.Royal).getIpAddress());
				prop.setProperty("bank-server-replica-rm"+machineCount+"-"+bankName+"-port", String.valueOf(Env.getReplicaToReplicaManagerServerInfo(machineName, Bank.Royal).getPort()));
			}
			
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
