package shared.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import shared.data.Bank;
import shared.data.ServerInfo;

public class ReadConfig
{
	private static long cache_LastModifiedDate = Long.MIN_VALUE;

	public static void InitServerSettings() throws Exception
	{
		Properties prop = new Properties();
		InputStream input = null;

		try
		{
			File file = new File("config.properties");
			if (file.lastModified() <= cache_LastModifiedDate)
			{
				// File already loaded
				return;
			}
			cache_LastModifiedDate = file.lastModified();
			input = new FileInputStream(file);

			// load a properties file
			prop.load(input);
			
    		ServerInfo sequencerSvInfo = null;
    		ServerInfo frontEndSvInfo = null;
    		Map<String, ServerInfo> replicaManagerSvInfoSet = new HashMap<String, ServerInfo>();
    		Map<String, Map<Bank, ServerInfo>> replicaSvInfoSet = new HashMap<String, Map<Bank, ServerInfo>>();
    		Map<String, Map<Bank, ServerInfo>> replicaIntranetSvInfoSet = new HashMap<String, Map<Bank, ServerInfo>>();
    		Map<String, Map<Bank, ServerInfo>> replicaRMSvInfoSet = new HashMap<String, Map<Bank, ServerInfo>>();

			String machineName = prop.getProperty("current-machine-name");
			
			String logConsoleEnabled = prop.getProperty("log-console-enabled");
			String logFileEnabled = prop.getProperty("log-file-enabled");
			String logFilePath = prop.getProperty("log-file-path");

			String frontEndIp = prop.getProperty("bank-front-end-ip");
			String frontEndPort = prop.getProperty("bank-front-end-port");

			String sequencerIp = prop.getProperty("bank-sequencer-ip");
			String sequencerPort = prop.getProperty("bank-sequencer-port");
			
			if(frontEndIp == null || frontEndPort == null || sequencerIp == null || sequencerPort == null)
			{
				throw new Exception("Missing settings");
			}

			frontEndSvInfo = new ServerInfo(Constant.getFrontEndName(), frontEndIp, Integer.parseInt(frontEndPort));
			sequencerSvInfo = new ServerInfo(Constant.getSequencerName(), sequencerIp, Integer.parseInt(sequencerPort));
			
			int serverCounter = 1;
			String bankmachineName;
			String replicaManagerIp;
			String replicaManagerPort;
			String replicaDominionIp;
			String replicaDominionPort;
			String replicaNationalIp;
			String replicaNationalPort;
			String replicaRoyalIp;
			String replicaRoyalPort;
			String replicaIntranetDominionIp;
			String replicaIntranetDominionPort;
			String replicaIntranetNationalIp;
			String replicaIntranetNationalPort;
			String replicaIntranetRoyalIp;
			String replicaIntranetRoyalPort;
			String replicaRMDominionIp;
			String replicaRMDominionPort;
			String replicaRMNationalIp;
			String replicaRMNationalPort;
			String replicaRMRoyalIp;
			String replicaRMRoyalPort;
			while ((bankmachineName = prop.getProperty("bank-server-machine-name" + serverCounter)) != null
					&& (replicaManagerIp = prop.getProperty("bank-server-replicatemanager" + serverCounter + "-ip")) != null
					&& (replicaManagerPort = prop.getProperty("bank-server-replicatemanager" + serverCounter + "-port")) != null 
					&& (replicaDominionIp = prop.getProperty("bank-server-intranet" + serverCounter + "-" + Bank.Dominion.toString() + "-ip")) != null
					&& (replicaDominionPort = prop.getProperty("bank-server-intranet" + serverCounter + "-" + Bank.Dominion.toString() + "-port")) != null 
					&& (replicaNationalIp = prop.getProperty("bank-server-intranet" + serverCounter + "-" + Bank.National.toString() + "-ip")) != null
					&& (replicaNationalPort = prop.getProperty("bank-server-intranet" + serverCounter + "-" + Bank.National.toString() + "-port")) != null 
					&& (replicaRoyalIp = prop.getProperty("bank-server-intranet" + serverCounter + "-" + Bank.Royal.toString() + "-ip")) != null
					&& (replicaRoyalPort = prop.getProperty("bank-server-intranet" + serverCounter + "-" + Bank.Royal.toString() + "-port")) != null 
					&& (replicaIntranetDominionIp = prop.getProperty("bank-server-replica" + serverCounter + "-" + Bank.Dominion.toString() + "-ip")) != null
					&& (replicaIntranetDominionPort = prop.getProperty("bank-server-replica" + serverCounter + "-" + Bank.Dominion.toString() + "-port")) != null 
					&& (replicaIntranetNationalIp = prop.getProperty("bank-server-replica" + serverCounter + "-" + Bank.National.toString() + "-ip")) != null
					&& (replicaIntranetNationalPort = prop.getProperty("bank-server-replica" + serverCounter + "-" + Bank.National.toString() + "-port")) != null 
					&& (replicaIntranetRoyalIp = prop.getProperty("bank-server-replica" + serverCounter + "-" + Bank.Royal.toString() + "-ip")) != null
					&& (replicaIntranetRoyalPort = prop.getProperty("bank-server-replica" + serverCounter + "-" + Bank.Royal.toString() + "-port")) != null 
					&& (replicaRMDominionIp = prop.getProperty("bank-server-replica-rm" + serverCounter + "-" + Bank.Dominion.toString() + "-ip")) != null
					&& (replicaRMDominionPort = prop.getProperty("bank-server-replica-rm" + serverCounter + "-" + Bank.Dominion.toString() + "-port")) != null 
					&& (replicaRMNationalIp = prop.getProperty("bank-server-replica-rm" + serverCounter + "-" + Bank.National.toString() + "-ip")) != null
					&& (replicaRMNationalPort = prop.getProperty("bank-server-replica-rm" + serverCounter + "-" + Bank.National.toString() + "-port")) != null 
					&& (replicaRMRoyalIp = prop.getProperty("bank-server-replica-rm" + serverCounter + "-" + Bank.Royal.toString() + "-ip")) != null
					&& (replicaRMRoyalPort = prop.getProperty("bank-server-replica-rm" + serverCounter + "-" + Bank.Royal.toString() + "-port")) != null 
					&& isValidPort(replicaManagerPort) 
					&& isValidPort(replicaDominionPort)
					&& isValidPort(replicaNationalPort)
					&& isValidPort(replicaRoyalPort) 
					&& isValidPort(replicaIntranetDominionPort) 
					&& isValidPort(replicaIntranetNationalPort)
					&& isValidPort(replicaIntranetRoyalPort)
					&& isValidPort(replicaRMDominionPort) 
					&& isValidPort(replicaRMNationalPort)
					&& isValidPort(replicaRMRoyalPort))
			{
				replicaManagerSvInfoSet.put(bankmachineName, new ServerInfo(Constant.getReplicaManagerName(bankmachineName), replicaManagerIp, Integer.parseInt(replicaManagerPort)));
				
				replicaSvInfoSet.put(bankmachineName, new HashMap<Bank, ServerInfo>());
				replicaSvInfoSet.get(bankmachineName).put(Bank.Dominion, new ServerInfo(bankmachineName, replicaDominionIp, Integer.parseInt(replicaDominionPort)));
				replicaSvInfoSet.get(bankmachineName).put(Bank.National, new ServerInfo(bankmachineName, replicaNationalIp, Integer.parseInt(replicaNationalPort)));
				replicaSvInfoSet.get(bankmachineName).put(Bank.Royal, new ServerInfo(bankmachineName, replicaRoyalIp, Integer.parseInt(replicaRoyalPort)));
				
				replicaIntranetSvInfoSet.put(bankmachineName, new HashMap<Bank, ServerInfo>());
				replicaSvInfoSet.get(bankmachineName).put(Bank.Dominion, new ServerInfo(bankmachineName, replicaIntranetDominionIp, Integer.parseInt(replicaIntranetDominionPort)));
				replicaSvInfoSet.get(bankmachineName).put(Bank.National, new ServerInfo(bankmachineName, replicaIntranetNationalIp, Integer.parseInt(replicaIntranetNationalPort)));
				replicaSvInfoSet.get(bankmachineName).put(Bank.Royal, new ServerInfo(bankmachineName, replicaIntranetRoyalIp, Integer.parseInt(replicaIntranetRoyalPort)));
				
				
				replicaRMSvInfoSet.put(bankmachineName, new HashMap<Bank, ServerInfo>());
				replicaSvInfoSet.get(bankmachineName).put(Bank.Dominion, new ServerInfo(bankmachineName, replicaRMDominionIp, Integer.parseInt(replicaRMDominionPort)));
				replicaSvInfoSet.get(bankmachineName).put(Bank.National, new ServerInfo(bankmachineName, replicaRMNationalIp, Integer.parseInt(replicaRMNationalPort)));
				replicaSvInfoSet.get(bankmachineName).put(Bank.Royal, new ServerInfo(bankmachineName, replicaRMRoyalIp, Integer.parseInt(replicaRMRoyalPort)));

				serverCounter++;
			}

			boolean isLogConsoleEnabled = (logConsoleEnabled == null || logConsoleEnabled.equalsIgnoreCase(Constant.TRUE));
			boolean islogFileEnabled = (logFileEnabled == null || logFileEnabled.equalsIgnoreCase(Constant.TRUE));
			if (logFilePath == null || logFilePath.isEmpty())
			{
				logFilePath = Constant.DefaultLogPath;
			}

	    	Env.setLogFileEnabled(islogFileEnabled);
	    	Env.setLogConsoleEnabled(isLogConsoleEnabled);
	    	Env.setLogPath(logFilePath);
	    	
	    	Env.setMachineName(machineName);
			
	    	Env.loadSettings(bankmachineName, sequencerSvInfo, frontEndSvInfo, 
	    			replicaManagerSvInfoSet, replicaSvInfoSet, replicaIntranetSvInfoSet, replicaRMSvInfoSet);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			throw ex;
		}
		finally
		{
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public static boolean isValidPort(String str)
	{
		boolean result = false;
		try
		{
			int d = Integer.parseInt(str);
			if (d >= Constant.MIN_PORT_NUMBER && d <= Constant.MAX_PORT_NUMBER)
			{
				result = true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
}
