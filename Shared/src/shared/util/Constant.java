package shared.util;

import java.io.File;

import shared.data.Bank;

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

	private static final String nameSequencer = "Sequencer";
	private static final String nameFrontEnd = "FrontEnd";
	private static final String nameReplicaManager = "ReplicaManager %s";
	private static final String nameReplica = "Replica %s";
	private static final String nameReplicaBank = "Replica %s, Bank %s";

	public static final String MACHINE_NAME_PASCAL = "pascal";
	public static final String MACHINE_NAME_AYMERIC = "aymeric";
	public static final String MACHINE_NAME_MATHIEU = "mathieu";
	public static final String MACHINE_NAME_RICHARD = "richard";
	public static final String MACHINE_NAME_FRONT_END = nameFrontEnd;
	public static final String MACHINE_NAME_SEQUENCER = nameSequencer;

	public static String getSequencerName() {
		return nameSequencer;
	}
	public static String getFrontEndName() {
		return nameFrontEnd;
	}
	public static String getReplicaName(String name) {
		return String.format(nameReplica, name);
	}
	public static String getReplicaNameFromBank(String name, Bank bankName) {
		return String.format(nameReplicaBank, name, bankName.toString());
	}
	public static String getReplicaManagerName(String name) {
		return String.format(nameReplicaManager, name);
	}
}
