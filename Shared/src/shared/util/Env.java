package shared.util;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import shared.data.Bank;
import shared.data.ServerInfo;

public class Env
{
	private static boolean isLogFileEnabled;
	private static boolean isLogConsoleEnabled;
	private static String logPath;

	private static Bank currentBank;
	private static String machineName;
	private static ServerInfo sequencerSvInfo;
	private static ServerInfo frontEndSvInfo;
	private static Map<String, ServerInfo> replicaManagerSvInfoSet;
	// Name of host machine followed by name of bank give the server info object
	private static Map<String, Map<Bank, ServerInfo>> replicaSvInfoSet;
	private static Map<String, Map<Bank, ServerInfo>> replicaIntranetSvInfoSet;
	private static Map<String, Map<Bank, ServerInfo>> replicaToReplicaManagerSvInfoSet;
		
	/**
	 * Static constructor
	 * @return 
	 */
    public static void loadSettings()
    {
		// Load server settings
    	try
    	{
    		ReadConfig.InitServerSettings();
    	}
    	catch (Exception e)
    	{
        	loadDefaultSettings();
			System.out.println("Error loading the settings files, default setting will be created.");
			WriteConfig.InitServerDefaultSettings();
    	}
    }
    
    /**
     * Load settings
     * @param machineName
     * @param sequencerSvInfo
     * @param frontEndSvInfo
     * @param replicaManagerSvInfoSet
     * @param replicaSvInfoSet
     * @param replicaIntranetSvInfoSet
     * @param replicaRMSvInfoSet
     */
    public static void loadSettings(Bank currentBank,
    		String machineName, 
    		ServerInfo sequencerSvInfo, 
    		ServerInfo frontEndSvInfo, 
    		Map<String, ServerInfo> replicaManagerSvInfoSet, 
    		Map<String, Map<Bank, ServerInfo>> replicaSvInfoSet, 
    		Map<String, Map<Bank, ServerInfo>> replicaIntranetSvInfoSet,
    		Map<String, Map<Bank, ServerInfo>> replicaRMSvInfoSet) 
    {
    	Env.setCurrentBank(currentBank);
    	Env.machineName = machineName;
    	Env.sequencerSvInfo = sequencerSvInfo;
    	Env.frontEndSvInfo = frontEndSvInfo;
    	Env.replicaManagerSvInfoSet = replicaManagerSvInfoSet; 
    	Env.replicaSvInfoSet = replicaSvInfoSet;
    	Env.replicaIntranetSvInfoSet = replicaIntranetSvInfoSet;
    	Env.replicaToReplicaManagerSvInfoSet = replicaRMSvInfoSet;
    }
    
    /**
     * Default setting to load in case config.properties doesn't exist
     */
    private static void loadDefaultSettings() {
    	replicaManagerSvInfoSet = new HashMap<String, ServerInfo>();
    	replicaSvInfoSet = new HashMap<String, Map<Bank, ServerInfo>>();
    	replicaIntranetSvInfoSet = new HashMap<String, Map<Bank, ServerInfo>>();
    	replicaToReplicaManagerSvInfoSet = new HashMap<String, Map<Bank, ServerInfo>>();
    	
    	setLogFileEnabled(true);
    	setLogConsoleEnabled(true);
    	logPath = Constant.DefaultLogPath;
    	Env.setCurrentBank(Bank.None);

    	// Default values
    	machineName = Constant.getFrontEndName();
    	setFrontEndServerInfo(Constant.getFrontEndName(), "127.0.0.1", 4400);
    	setSequencerServerInfo(Constant.getSequencerName(), "127.0.0.1", 4401);
    	
    	int port = 4500;
    	String name = Constant.MACHINE_NAME_AYMERIC;
    	replicaManagerSvInfoSet.put(name, new ServerInfo(Constant.getReplicaManagerName(name), "127.0.0.1", port++));
    	replicaSvInfoSet.put(name, new HashMap<Bank, ServerInfo>());
    	replicaSvInfoSet.get(name).put(Bank.Dominion, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Dominion), "127.0.0.1", port++));
    	replicaSvInfoSet.get(name).put(Bank.National, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.National), "127.0.0.1", port++));
    	replicaSvInfoSet.get(name).put(Bank.Royal, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Royal), "127.0.0.1", port++));

    	replicaIntranetSvInfoSet.put(name, new HashMap<Bank, ServerInfo>());
    	replicaIntranetSvInfoSet.get(name).put(Bank.Dominion, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Dominion), "127.0.0.1", port++));
    	replicaIntranetSvInfoSet.get(name).put(Bank.National, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.National), "127.0.0.1", port++));
    	replicaIntranetSvInfoSet.get(name).put(Bank.Royal, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Royal), "127.0.0.1", port++));

    	replicaToReplicaManagerSvInfoSet.put(name, new HashMap<Bank, ServerInfo>());
    	replicaToReplicaManagerSvInfoSet.get(name).put(Bank.Dominion, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Dominion), "127.0.0.1", port++));
    	replicaToReplicaManagerSvInfoSet.get(name).put(Bank.National, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.National), "127.0.0.1", port++));
    	replicaToReplicaManagerSvInfoSet.get(name).put(Bank.Royal, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Royal), "127.0.0.1", port++));

    	name = Constant.MACHINE_NAME_MATHIEU;
    	replicaManagerSvInfoSet.put(name, new ServerInfo(Constant.getReplicaManagerName(name), "127.0.0.1", port++));
    	replicaSvInfoSet.put(name, new HashMap<Bank, ServerInfo>());
    	replicaSvInfoSet.get(name).put(Bank.Dominion, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Dominion), "127.0.0.1", port++));
    	replicaSvInfoSet.get(name).put(Bank.National, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.National), "127.0.0.1", port++));
    	replicaSvInfoSet.get(name).put(Bank.Royal, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Royal), "127.0.0.1", port++));

    	replicaIntranetSvInfoSet.put(name, new HashMap<Bank, ServerInfo>());
    	replicaIntranetSvInfoSet.get(name).put(Bank.Dominion, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Dominion), "127.0.0.1", port++));
    	replicaIntranetSvInfoSet.get(name).put(Bank.National, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.National), "127.0.0.1", port++));
    	replicaIntranetSvInfoSet.get(name).put(Bank.Royal, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Royal), "127.0.0.1", port++));

    	replicaToReplicaManagerSvInfoSet.put(name, new HashMap<Bank, ServerInfo>());
    	replicaToReplicaManagerSvInfoSet.get(name).put(Bank.Dominion, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Dominion), "127.0.0.1", port++));
    	replicaToReplicaManagerSvInfoSet.get(name).put(Bank.National, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.National), "127.0.0.1", port++));
    	replicaToReplicaManagerSvInfoSet.get(name).put(Bank.Royal, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Royal), "127.0.0.1", port++));
    	
    	name = Constant.MACHINE_NAME_PASCAL;
    	replicaManagerSvInfoSet.put(name, new ServerInfo(Constant.getReplicaManagerName(name), "127.0.0.1", port++));
    	replicaSvInfoSet.put(name, new HashMap<Bank, ServerInfo>());
    	replicaSvInfoSet.get(name).put(Bank.Dominion, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Dominion), "127.0.0.1", port++));
    	replicaSvInfoSet.get(name).put(Bank.National, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.National), "127.0.0.1", port++));
    	replicaSvInfoSet.get(name).put(Bank.Royal, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Royal), "127.0.0.1", port++));

    	replicaIntranetSvInfoSet.put(name, new HashMap<Bank, ServerInfo>());
    	replicaIntranetSvInfoSet.get(name).put(Bank.Dominion, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Dominion), "127.0.0.1", port++));
    	replicaIntranetSvInfoSet.get(name).put(Bank.National, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.National), "127.0.0.1", port++));
    	replicaIntranetSvInfoSet.get(name).put(Bank.Royal, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Royal), "127.0.0.1", port++));

    	replicaToReplicaManagerSvInfoSet.put(name, new HashMap<Bank, ServerInfo>());
    	replicaToReplicaManagerSvInfoSet.get(name).put(Bank.Dominion, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Dominion), "127.0.0.1", port++));
    	replicaToReplicaManagerSvInfoSet.get(name).put(Bank.National, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.National), "127.0.0.1", port++));
    	replicaToReplicaManagerSvInfoSet.get(name).put(Bank.Royal, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Royal), "127.0.0.1", port++));
    	
    	name = Constant.MACHINE_NAME_RICHARD;
    	replicaManagerSvInfoSet.put(name, new ServerInfo(Constant.getReplicaManagerName(name), "127.0.0.1", port++));
    	replicaSvInfoSet.put(name, new HashMap<Bank, ServerInfo>());
    	replicaSvInfoSet.get(name).put(Bank.Dominion, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Dominion), "127.0.0.1", port++));
    	replicaSvInfoSet.get(name).put(Bank.National, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.National), "127.0.0.1", port++));
    	replicaSvInfoSet.get(name).put(Bank.Royal, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Royal), "127.0.0.1", port++));

    	replicaIntranetSvInfoSet.put(name, new HashMap<Bank, ServerInfo>());
    	replicaIntranetSvInfoSet.get(name).put(Bank.Dominion, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Dominion), "127.0.0.1", port++));
    	replicaIntranetSvInfoSet.get(name).put(Bank.National, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.National), "127.0.0.1", port++));
    	replicaIntranetSvInfoSet.get(name).put(Bank.Royal, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Royal), "127.0.0.1", port++));

    	replicaToReplicaManagerSvInfoSet.put(name, new HashMap<Bank, ServerInfo>());
    	replicaToReplicaManagerSvInfoSet.get(name).put(Bank.Dominion, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Dominion), "127.0.0.1", port++));
    	replicaToReplicaManagerSvInfoSet.get(name).put(Bank.National, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.National), "127.0.0.1", port++));
    	replicaToReplicaManagerSvInfoSet.get(name).put(Bank.Royal, new ServerInfo(Constant.getReplicaNameFromBank(name, Bank.Royal), "127.0.0.1", port++));
    }

	private static synchronized void writeToLogFile(String message)
	{
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logPath, true)))) 
		{
			String timestamp = (new java.sql.Timestamp((new java.util.Date()).getTime())).toString();
		    out.println(timestamp + ": " + message);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private static synchronized void writeToLogFile(Level level, String message)
	{
		writeToLogFile(level.toString() + " " + message);
	}

	public static void log(String message)
	{
		if(isLogFileEnabled)
		{
			writeToLogFile(message);
		}
		
		if (isLogConsoleEnabled)
		{
			System.out.println(message);
		}
	}

	public static void log(Level level, String message)
	{
		if(isLogFileEnabled)
		{
			writeToLogFile(level, message);
		}
		
		if (isLogConsoleEnabled)
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
			Env.log(Level.SEVERE, "Date Parse Exception: " + e.getMessage());
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
	 * getSequencerServerInfo
	 * @return
	 */
	public static ServerInfo getSequencerServerInfo() {
		return sequencerSvInfo;
	}
	
	/**
	 * setSequencerServerInfo
	 * @param serverName
	 * @param ipAddress
	 * @param port
	 */
	public static void setSequencerServerInfo(String serverName, String ipAddress, int port) {
		sequencerSvInfo = new ServerInfo(serverName, ipAddress, port);
	}

	/**
	 * getFrontEndServerInfo
	 * @return
	 */
	public static ServerInfo getFrontEndServerInfo() {
		return frontEndSvInfo;
	}

	/**
	 * setFrontEndServerInfo
	 * @param serverName
	 * @param ipAddress
	 * @param port
	 */
	public static void setFrontEndServerInfo(String serverName, String ipAddress, int port) {
		frontEndSvInfo = new ServerInfo(serverName, ipAddress, port);
	}

	/**
	 * getListMachineName
	 * @return
	 */
	public static Iterator<String> getListMachineName() {
		return replicaSvInfoSet.keySet().iterator();
	}

	/**
	 * getReplicaServerInfo
	 * @return
	 */
	public static ServerInfo getReplicaServerInfo(String machineName, Bank bank) {
		Map<Bank, ServerInfo> map = replicaSvInfoSet.get(machineName);
		if(map != null)
		{
			return map.get(bank);
		}
		return null;
	}

	/**
	 * getReplicaServerInfo
	 * @return
	 */
	public static ServerInfo getReplicaServerInfo(Bank bank) {
		Map<Bank, ServerInfo> map = replicaSvInfoSet.get(machineName);
		if(map != null)
		{
			return map.get(bank);
		}
		return null;
	}

	/**
	 * getReplicaServerInfo
	 * @return
	 */
	public static ServerInfo getReplicaServerInfo() {
		Map<Bank, ServerInfo> map = replicaSvInfoSet.get(machineName);
		if(map != null)
		{
			return map.get(Env.getCurrentBank());
		}
		return null;
	}

	public static List<ServerInfo> getReplicaServerInfoList() {
		Map<Bank, ServerInfo> map = replicaSvInfoSet.get(machineName);
		if(map != null)
		{
			return new ArrayList<ServerInfo>(map.values());
		}
		return null;
	}

	public static List<ServerInfo> getReplicaServerInfoList(String machineName) {
		Map<Bank, ServerInfo> map = replicaSvInfoSet.get(machineName);
		if(map != null)
		{
			return new ArrayList<ServerInfo>(map.values());
		}
		return null;
	}

	/**
	 * getReplicaManagerServerInfo
	 * @return
	 */
	public static ServerInfo getReplicaManagerServerInfo() {
		return replicaManagerSvInfoSet.get(Env.getMachineName());
	}

	/**
	 * getReplicaManagerServerInfo
	 * @return
	 */
	public static ServerInfo getReplicaManagerServerInfo(String machineName) {
		return replicaManagerSvInfoSet.get(machineName);
	}
	
	/**
	 * getReplicaIntranetServerInfo
	 * @return
	 */
	public static ServerInfo getReplicaIntranetServerInfo(String machineName, Bank bank) {
		Map<Bank, ServerInfo> map = replicaIntranetSvInfoSet.get(machineName);
		if(map != null)
		{
			return map.get(bank);
		}
		return null;
	}

	/**
	 * getReplicaIntranetServerInfo
	 * @param bank
	 * @return
	 */
	public static ServerInfo getReplicaIntranetServerInfo(Bank bank) {
		Map<Bank, ServerInfo> map = replicaIntranetSvInfoSet.get(Env.getMachineName());
		if(map != null)
		{
			return map.get(bank);
		}
		return null;
	}
	
	/**
	 * getReplicaIntranetServerInfo
	 * @return
	 */
	public static ServerInfo getReplicaIntranetServerInfo() {
		Map<Bank, ServerInfo> map = replicaIntranetSvInfoSet.get(Env.getMachineName());
		if(map != null)
		{
			return map.get(Env.getCurrentBank());
		}
		return null;
	}

	/**
	 * getReplicaToReplicaManagerServerInfo
	 * @return
	 */
	public static ServerInfo getReplicaToReplicaManagerServerInfo(String machineName, Bank bank) {
		Map<Bank, ServerInfo> map = replicaToReplicaManagerSvInfoSet.get(machineName);
		if(map != null)
		{
			return map.get(bank);
		}
		return null;
	}

	/**
	 * getReplicaToReplicaManagerServerInfo
	 * @return
	 */
	public static ServerInfo getReplicaToReplicaManagerServerInfo() {
		Map<Bank, ServerInfo> map = replicaToReplicaManagerSvInfoSet.get(Env.getMachineName());
		if(map != null)
		{
			return map.get(Env.getCurrentBank());
		}
		return null;
	}

	/**
	 * getReplicaToReplicaManagerServerInfo
	 * @param bank
	 * @return
	 */
	public static ServerInfo getReplicaToReplicaManagerServerInfo(Bank bank) {
		Map<Bank, ServerInfo> map = replicaToReplicaManagerSvInfoSet.get(Env.getMachineName());
		if(map != null)
		{
			return map.get(bank);
		}
		return null;
	}
	
	public static String getLogPath() {
		return logPath;
	}

	public static boolean isLogConsoleEnabled() {
		return isLogConsoleEnabled;
	}

	public static void setLogConsoleEnabled(boolean isLogConsoleEnabled) {
		Env.isLogConsoleEnabled = isLogConsoleEnabled;
	}

	public static boolean islogFileEnabled() {
		return isLogFileEnabled;
	}

	public static void setLogFileEnabled(boolean islogFileEnabled) {
		Env.isLogFileEnabled = islogFileEnabled;
	}

	public static void setLogPath(String logFilePath) {
		Env.logPath = logFilePath;
	}

	public static String getMachineName() {
		return Env.machineName;
	}

	public static Bank getCurrentBank() {
		return currentBank;
	}

	public static void setCurrentBank(Bank currentBank) {
		Env.currentBank = currentBank;
	}
}
