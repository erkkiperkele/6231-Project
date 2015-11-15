package dlms.util;

import java.io.File;

public class Constant
{
	public static final String TRUE = "TRUE";
	public static final String DefaultLogPath = "." + File.separator + "log.txt";
	public static final int MIN_PORT_NUMBER = 1;
	public static final int MAX_PORT_NUMBER = 65535;

	public static final String ServerCustomersFile = "_Customers.xml";
	public static final String ServerLoansFile = "_Loans.xml";

	public static final int ACCOUNTID_NOT_FOUND = 0;
	public static final int VALID_CUSTOMER = 1;

	public static final byte RESOURCE_LOCK_SUCCESSFUL = 0;
	public static final byte RESOURCE_LOCK_FAILED = 1;

	public static final int SIZE_BUFFER_REQUEST = 1000;
	public static final int SIZE_BUFFER_RESPONSE = 9;
	public static final int AMOUNT_OF_RETRY_UDP = 3;

	public static final byte TRANSFER_FAILED = 1;
	public static final byte TRANSFER_SUCCESSFUL = 0;
}
