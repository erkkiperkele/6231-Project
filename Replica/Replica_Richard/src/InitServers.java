import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import DLMS.BankServerInterfacePOA;

/**
 * InitServers: Creates banks and binds BankServers to ports.
 * @author richard
 */

public class InitServers {
	public static void main(String[] args) throws ServantAlreadyActive, WrongPolicy, InvalidName, ObjectNotActive, FileNotFoundException, AdapterInactive {
		// Initialize ORB
		ORB _orb = ORB.init(args, null);
		POA _rootPOA = POAHelper.narrow(_orb.resolve_initial_references("RootPOA"));

		
		// Create 3 Bank objects
		Bank[] banks = new Bank[3];
		banks[0] = new Bank("TD Bank");
		banks[1] = new Bank("Bank of Montreal");
		banks[2] = new Bank("Scotia Bank");
		
		// Create 3 BankServers
		// See BankServer.java for UDP port information
		BankServerInterfacePOA[] bankservers = new BankServer[banks.length];
		
		for(int i = 0; i < banks.length; ++i) {
			bankservers[i] = new BankServer(banks[i]);
			byte[] _id = _rootPOA.activate_object(bankservers[i]);
			org.omg.CORBA.Object _ref = _rootPOA.id_to_reference(_id);
			String _ior = _orb.object_to_string(_ref);
			
			PrintWriter _file = new PrintWriter(banks[i].getName() + "_IOR.txt");
			_file.println(_ior);
			_file.close();
		}
		
		System.out.println("Starting server");
		_rootPOA.the_POAManager().activate();
		_orb.run();
	}
}
