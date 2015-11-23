package dlms.frontend;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import dlms.corba.AppException;
import dlms.corba.FrontEndHelper;
import dlms.corba.FrontEndPOA;

/**
 * The DLMS front end. this is the object through which the client performs
 * operations.
 * 
 * @author mat
 *
 */
public class FrontEnd extends FrontEndPOA {

	public static final String orbArgs[] = {"-ORBInitialPort 1050", "-ORBInitialHost localhost"};
	private ORB orb;
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String args[]) {

		try {
			
			// Create and initialize the ORB
			ORB orb = ORB.init(orbArgs, null);

			// Get reference to RootPOA and activate the POAManager
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// Create servant and register it with the ORB
			FrontEnd frontEnd = new FrontEnd();
			frontEnd.setORB(orb);

			// Get object reference from the servant
			org.omg.CORBA.Object ref1 = rootpoa.servant_to_reference(frontEnd);
			dlms.corba.FrontEnd feRef = FrontEndHelper.narrow(ref1);

			// Get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			// Use NamingContextExt which is part of the Interoperable
			// Naming Service (INS) specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// Bind the object reference in naming
			NameComponent path1[] = ncRef.to_name("frontend");
			ncRef.rebind(path1, feRef);

			System.out.println("AppController: Front end ready and waiting ...");
	
			// Wait for invocations from clients
			orb.run();
			
		}
		catch (Exception e) {
			System.err.println("Error: " + e);
			e.printStackTrace(System.out);
		}
	}

	/**
	 * Constructor
	 */
	public FrontEnd() {
		super();

	}

	@Override
	public boolean delayPayment(String bankId, int loanId, String currentDueDate, String newDueDate)
			throws AppException {
		
		return false;
	}

	@Override
	public String printCustomerInfo(String bankId) {
		
		return null;
	}

	@Override
	public int transferLoan(String bankId, int loanId, String currentBankId, String otherBankId) throws AppException {
		
		return 0;
	}

	@Override
	public int openAccount(String bankId, String firstName, String lastName, String emailAddress, String phoneNumber,
			String password) throws AppException {
		
		return 0;
	}

	@Override
	public int getLoan(String bankId, int accountNbr, String password, int loanAmount) throws AppException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 
	 * @param orb_val
	 */
	public void setORB(ORB orb_val) {
		this.orb = orb_val;
	}

}
