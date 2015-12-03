package dlms.client;

import java.util.Properties;

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

	private ORB orb;
	protected FrontEnd server = null;
	
	/**
	 * 
	 * Constructor
	 */
	public Client() {
		
		super();
		
		Properties orbProps = new Properties();
		orbProps.put("org.omg.CORBA.ORBInitialHost", "localhost");
		orbProps.put("org.omg.CORBA.ORBInitialPort", "1050");
		
		this.orb = ORB.init(new String[]{}, orbProps);

		try {
			
			// Get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			
			// Resolve the Object Reference in Naming
			server = FrontEndHelper.narrow(ncRef.resolve_str("FrontEnd"));

		} catch (Exception e) {
			System.out.println("Error : " + e);
			e.printStackTrace(System.out);
			System.exit(1);
		}
		
	}
}
