package shared.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import shared.data.ServerInfo;

public class ReadConfig
{
	private static long cache_LastModifiedDate = Long.MIN_VALUE;

	public static void InitServerSettings(String info)
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
			input = new FileInputStream(file);

			// load a properties file
			prop.load(input);

			if (Env.getLstServers() == null)
			{
				// get the property value and print it out
				ArrayList<ServerInfo> lstServers = new ArrayList<ServerInfo>();

				int serverCounter = 1;
				String serverName;
				String serverIp;
				String serverPort;
				while ((serverName = prop.getProperty("bank-server-name" + serverCounter)) != null
						&& (serverIp = prop.getProperty("bank-server-ip" + serverCounter)) != null
						&& (serverPort = prop.getProperty("bank-server-port" + serverCounter)) != null && isValidPort(serverPort))
				{
					ServerInfo server = new ServerInfo();
					server.setServerName(serverName);
					server.setIpAddress(serverIp);
					server.setPort(Integer.parseInt(serverPort));
					lstServers.add(server);
					serverCounter++;
				}
				Env.setLstServers(lstServers);
			}

			String logConsoleEnabled = prop.getProperty("log-console-enabled");
			String logFileEnabled = prop.getProperty("log-file-enabled");
			String logFilePath = prop.getProperty("log-file-path");
			String logLevel = prop.getProperty("log-level");

			Level logLevelValue;
			try
			{
				logLevelValue = Level.parse(logLevel);
			}
			catch (Exception e)
			{
				logLevelValue = Level.ALL;
			}
			boolean isLogConsoleEnabled = (logConsoleEnabled == null || logConsoleEnabled.equalsIgnoreCase(Constant.TRUE));
			boolean islogFileEnabled = (logFileEnabled == null || logFileEnabled.equalsIgnoreCase(Constant.TRUE));

			if (logFilePath == null || logFilePath.isEmpty())
			{
				logFilePath = Constant.DefaultLogPath;
			}

			// Logger.getAnonymousLogger()
			Env.initLogger(logLevelValue, isLogConsoleEnabled, islogFileEnabled, logFilePath, info);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
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
