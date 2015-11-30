package dlms.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.logging.*;

import dlms.model.*;

public class Env
{
	private static ArrayList<ServerInfo> lstServers;
	private static String rmiHostName;
	private static Logger logger;
	private static final Random rand = new Random();
	private static boolean isDebug = false;

	/**
	 * @return the logger
	 */
	public static Logger getLogger()
	{
		return logger;
	}

	public static void log(Level level, String message, boolean showConsole)
	{
		Env.getLogger().log(level, message);
		if (showConsole)
		{
			System.out.println(message);
		}
	}
	
	public static Date getNewLoanDueDate()
	{
		Date loanDueDate;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 6);
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		try
		{
			loanDueDate = formatter.parse(formatter.format(cal.getTime()));
		}
		catch (Exception e)
		{
			Env.log(Level.SEVERE, "Date Parse Exception: " + e.getMessage(), true);
			loanDueDate = cal.getTime();
		}
		return loanDueDate;
	}

	/**
	 * If the string is numeric
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str)
	{
		try
		{
			Double.parseDouble(str);
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param logLevelValue
	 * @param isLogConsoleEnabled
	 * @param islogFileEnabled
	 * @param logFilePath
	 */
	public static void initLogger(Level logLevelValue, boolean isLogConsoleEnabled, boolean islogFileEnabled, String logFilePath, String info)
	{
		String loggerName = Env.class.getName() + "_" + info;
		logger = Logger.getLogger(loggerName);
		logger.setLevel(logLevelValue);

		if (isLogConsoleEnabled)
		{
			// Set the console log
			ConsoleHandler consoleHandler = new ConsoleHandler();
			consoleHandler.setLevel(logLevelValue);
			logger.addHandler(consoleHandler);
		}

		if (islogFileEnabled)
		{
			// Set the file log
			Handler fileHandler = null;
			try
			{
				fileHandler = new FileHandler(logFilePath);
			}
			catch (Exception e1)
			{
				logger.log(Level.SEVERE, "an exception was thrown", e1);
				logFilePath = Constant.DefaultLogPath;
				try
				{
					fileHandler = new FileHandler(logFilePath);
				}
				catch (Exception e2)
				{
					fileHandler = null;
					logger.log(Level.SEVERE, "an exception was thrown", e2);
				}
			}

			if (fileHandler == null)
			{
				// In case of error
				if (isLogConsoleEnabled == false)
				{
					logger.log(Level.SEVERE, "Couldn't create a log file! Activating console logging instead.");
					ConsoleHandler consoleHandler = new ConsoleHandler();
					consoleHandler.setLevel(logLevelValue);
					logger.addHandler(consoleHandler);
				}
				else
				{
					logger.log(Level.SEVERE, "Couldn't create a log file!");
				}
			}
			else
			{
				fileHandler.setLevel(logLevelValue);
				logger.addHandler(fileHandler);
			}
		}
	}

	/**
	 * @return the lstServers
	 */
	public static ArrayList<ServerInfo> getLstServers()
	{
		return lstServers;
	}

	/**
	 * @param lstServers
	 *            the lstServers to set
	 */
	public static void setLstServers(ArrayList<ServerInfo> lstServers)
	{
		Env.lstServers = lstServers;
	}

	/**
	 * @return the rmiPortNumber
	 */
	public static String getRMIHostName()
	{
		return rmiHostName;
	}

	public static void setRMIHostName(String hostName)
	{
		Env.rmiHostName = hostName;
	}

	public static String getCustomerClientRegistryURL(int port)
	{
		return "rmi://" + getRMIHostName() + ":" + port + "/CustomerClient";
	}

	public static String getManagerClientRegistryURL(int port)
	{
		return "rmi://" + getRMIHostName() + ":" + port + "/ManagerClient";
	}

	public static String getServerCustomersFile(String name, String username)
	{
		Character lowerKey = Character.toLowerCase(username.charAt(0));
		return "./" + name + "_" + lowerKey.toString() + Constant.ServerCustomersFile;
	}

	public static String getServerLoansFile(String name, String username)
	{
		Character lowerKey = Character.toLowerCase(username.charAt(0));
		return "./" + name + "_" + lowerKey.toString() + Constant.ServerLoansFile;
	}

	public static String getRandomAccountNumber()
	{
		return String.format("%04d", (rand.nextInt(9999) + 1)) + "-" + String.format("%04d", (rand.nextInt(9999) + 1));
	}

	public static void loadServerSettings(String info)
	{
		// Load server settings
		ReadConfig.InitServerSettings(info);

		if (getLstServers() == null || getLstServers().size() == 0)
		{
			System.out.println("No bank information found in config.properties.");
			System.out.println("Default setting will be created.");
			if (WriteConfig.InitServerDefaultSettings())
			{
				// Reload server settings
				ReadConfig.InitServerSettings(info);
			}
		}
	}

	public static String getIORBankFile(String bankName)
	{
		String path;
		if (Env.isDebug())
		{
			path = ".." + File.separator + bankName + "_IOR.txt";
		}
		else
		{
			path = "." + File.separator + bankName + "_IOR.txt";
		}
		return path;
	}

	public static boolean isDebug()
	{
		return isDebug;
	}

	public static void setDebug(boolean isDebug)
	{
		Env.isDebug = isDebug;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getObjectFromDatagram(DatagramPacket request)
	{
		T messageRequested;
		try
		{
			byte[] message = Arrays.copyOf(request.getData(), request.getLength());

			// De-serialization of object
			ByteArrayInputStream bis = new ByteArrayInputStream(message);
			ObjectInputStream in = new ObjectInputStream(bis);
			messageRequested = (T) in.readObject();
		}
		catch (Exception e)
		{
			messageRequested = null;
		}
		return messageRequested;
	}

	public static <T> byte[] getByteFromObject(T object) throws Exception
	{
		byte[] message;

		// Serialization of object
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos);
		out.writeObject(object);

		message = bos.toByteArray();
		return message;
	}

	/**
	 * Get the bank server information
	 * 
	 * @param bankName
	 *            name of the bank
	 * @return the server information or null if not found
	 */
	public static ServerInfo GetServerInfo(String bankName)
	{
		Optional<ServerInfo> sv = getLstServers().stream().filter(x -> x.getServerName().equalsIgnoreCase(bankName)).findFirst();
		return sv.isPresent() ? sv.get() : null;
	}
}
