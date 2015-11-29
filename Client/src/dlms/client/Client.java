package dlms.client;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import dlms.corba.FrontEndHelper;
import dlms.corba.FrontEnd;

/**
 * Base class for customer and manager clients
 *
 * @author mat
 *
 */
public class Client {

	public final static String orbArgs[] = { "-ORBInitialPort 1050", "-ORBInitialHost localhost" };
	private ORB orb;
	private FrontEnd serverCache = null;

	/**
	 *
	 * Constructor
	 */
	public Client() {

		super();
		this.orb = ORB.init(orbArgs, null);
	}

	protected FrontEnd getServer() {

		if (serverCache != null) {
			return serverCache;
		}

		try {

			// Get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// Resolve the Object Reference in Naming
			serverCache = FrontEndHelper.narrow(ncRef.resolve_str("frontEnd"));

		} catch (Exception e) {
			System.out.println("Error : " + e);
			e.printStackTrace(System.out);
		}

		return serverCache;
	}
}
