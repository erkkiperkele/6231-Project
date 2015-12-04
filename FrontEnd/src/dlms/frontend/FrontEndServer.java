package dlms.frontend;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import dlms.corba.FrontEndHelper;

/**
 * Entry point for the front end
 * 
 * @author mat
 *
 */
public class FrontEndServer {

	public static final boolean ENABLE_FILE_LOGGING = false;
	
	private ORB orb = null;
	private Logger logger = null;
	private volatile QueuePool opQueuePool = null;
	
	/**
	 * Constructor
	 */
	public FrontEndServer() {
		
		super();

		// Create a pool of blocking queues to hold UDP messages for specific
		// operations which arrive from the replicas
		this.opQueuePool = new QueuePool();

		// Set up the logger
		logger = Logger.getLogger("FrontEnd");
		if (ENABLE_FILE_LOGGING) {
		    FileHandler fh;  
		    try {
		        fh = new FileHandler("FrontEnd-log.txt");  
		        logger.addHandler(fh);
		        SimpleFormatter formatter = new SimpleFormatter();  
		        fh.setFormatter(formatter);
		    } catch (SecurityException e) {  
		        e.printStackTrace();
		        System.exit(1);
		    } catch (IOException e) {  
		        e.printStackTrace(); 
		        System.exit(1);
		    }
		}
		logger.info("FrontEnd: Logger started");
		
		this.initiOrb();
		this.startServers();
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String args[]) {

		new FrontEndServer();
	}
	
	/**
	 * Initialize the ORB
	 */
	private void initiOrb() {

		try {
			
			Properties orbProps = new Properties();
			orbProps.put("org.omg.CORBA.ORBInitialHost", "localhost");
			orbProps.put("org.omg.CORBA.ORBInitialPort", "1050");
			
			// Create and initialize the ORB
			orb = ORB.init(new String[]{}, orbProps);

			// Get reference to RootPOA and activate the POAManager
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// Create a servant and register it with the ORB
			FrontEnd frontEnd = new FrontEnd(logger, this.opQueuePool);

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

		}
		catch (Exception e) {
			System.err.println("Error: " + e);
			e.printStackTrace(System.out);
		}
	}

	/**
	 * Starts the ORB server and UDP listener for incoming messages from the replicas
	 */
	private void startServers() {
		
		//System.out.println("FrontEnd: ready and waiting ...");

		// Wait for replica operation responses (in its own thread)
		ReplicaListener replicaListener = new ReplicaListener(logger, this.opQueuePool);
		replicaListener.start();

		// Wait for invocations from clients
		orb.run();
	}
}
