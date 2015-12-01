package shared.util;

import java.io.*;
import java.net.DatagramPacket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;
import shared.data.ServerInfo;

public class Env
{
	private static ArrayList<ServerInfo> lstServers;
	private static boolean isDebug = false;

	private static boolean isLogFileEnabled = false;
	private static String logPath = "./log/log.txt";

	public static synchronized void writeToLogFile(Level level, String message)
	{
		if(!isLogFileEnabled)
			return;
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logPath, true)))) 
		{
			String timestamp = (new java.sql.Timestamp((new java.util.Date()).getTime())).toString();
		    out.println(timestamp + ": " + level.toString() + " " + message);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public static void log(Level level, String message, boolean showConsole)
	{
		writeToLogFile(level, message);
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
		isLogFileEnabled = islogFileEnabled;
		logPath = logFilePath;
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
