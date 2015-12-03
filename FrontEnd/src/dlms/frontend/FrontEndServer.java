package dlms.frontend;

import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import dlms.corba.FrontEndHelper;

public class FrontEndServer {
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String args[]) {

		try {
			
			Properties orbProps = new Properties();
			orbProps.put("org.omg.CORBA.ORBInitialHost", "localhost");
			orbProps.put("org.omg.CORBA.ORBInitialPort", "1050");
			
			// Create and initialize the ORB
			//ORB orb = ORB.init(orbArgs, null);
			ORB orb = ORB.init(args, orbProps);

			// Get reference to RootPOA and activate the POAManager
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// Create a servant and register it with the ORB
			FrontEnd frontEnd = new FrontEnd();
			//frontEnd.setORB(orb);

			// Get object reference from the servant
			org.omg.CORBA.Object ref1 = rootpoa.servant_to_reference(frontEnd);
			dlms.corba.FrontEnd feRef = FrontEndHelper.narrow(ref1);

			// Get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			// Use NamingContextExt which is part of the Interoperable
			// Naming Service (INS) specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// Bind the object reference in naming
			NameComponent path1[] = ncRef.to_name("FrontEnd");
			ncRef.rebind(path1, feRef);

			System.out.println("FrontEnd: ready and waiting ...");

			// Wait for invocations from clients
			orb.run();
			
		}
		catch (Exception e) {
			System.err.println("Error: " + e);
			e.printStackTrace(System.out);
		}
	}
}
