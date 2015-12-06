package shared.util;

import java.io.File;

public class Constant
{
	public static final String TRUE = "TRUE";
	public static final String DefaultLogPath = "." + File.separator + "log.txt";
	public static final int MIN_PORT_NUMBER = 1;
	public static final int MAX_PORT_NUMBER = 65535;

	public static final int RM_TO_FE_LISTENER_PORT = 6667;
	public static final int RM_TO_RM_LISTENER_PORT = 6666;

	public static final String GET_STATE = "getstate";
	public static final String GET_PING = "ping";

	public static final String STOP_FE = "stop";
	public static final String FE_STOPPED = "stopped";
	public static final String START_FE = "start";
	public static final String FE_STARTED = "started";

	public static final int FE_TO_RM_LISTENER_PORT = 7777;


	public static final int MAX_ERROR_COUNT = 3;
}
