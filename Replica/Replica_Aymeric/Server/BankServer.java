package Server;

import Contracts.IBankService;
import Data.Bank;
import Data.ServerPorts;
import Services.BankService;
import Services.SessionService;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * This class starts both RMI and UDP servers for a given bank.
 * It also contains basic tests for data access (concurrency, UDP protocols...)
 */
public class BankServer {

    private static IBankService bankService;

    private static UDPServer udp;


    /**
     * Instatiates and starts the RMI and UDP servers.
     * ATTENTION: needs a single integer argument which is the bank Id for the server
     * See the Bank enum to know what integer corresponds to what bank.
     *
     * @param args a single integer defining what bank this server belongs to.
     */
    public static void main(String[] args) {

        String serverArg = args[0];
        initialize(serverArg);

        //Starting bank server
//        startRMIServer();
        startUDPServer();
        startCorbaServer();
    }

    private static void initialize(String arg) {
        Bank serverName = Bank.fromInt(Integer.parseInt(arg));
        SessionService.getInstance().setBank(serverName);
        bankService = new BankService();
        udp = new UDPServer(bankService);
    }

    private static void startRMIServer() {

        Bank bank = SessionService.getInstance().getBank();
        int serverPort = ServerPorts.getRMIPort(bank);

        try {
            (new BankServerRMI(bankService, serverPort)).exportServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SessionService.getInstance().log().info(
                String.format("%s Server is up and running on port %d!",
                        bank,
                        serverPort)
        );
    }

    private static void startUDPServer() {
        Thread startUdpServer = new Thread(() ->
        {
            udp.startServer();
        });
        startUdpServer.start();
        SessionService.getInstance().log().info(String.format("[UDP] SERVER STARTED"));

    }

    public static void startCorbaServer() {

        try {
            String[] args = new String[]{};

            Bank bank = SessionService.getInstance().getBank();
            int serverPort = ServerPorts.getRMIPort(bank);
            Properties props = new Properties();
            props.put("ORBPort", serverPort);
            ORB orb = ORB.init(args, props);

            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

            //Obtain a reference
            BankServerCorba bankServerCorba = new BankServerCorba(bankService, serverPort);
            byte[] id = rootPOA.activate_object(bankServerCorba);
            org.omg.CORBA.Object ref = rootPOA.id_to_reference(id);

            //Translate to ior and write it to a file
            String ior = orb.object_to_string(ref);
            System.out.println(ior);

            PrintWriter file = new PrintWriter(String.format("ior_%s.txt", bank.name()));
            file.println(ior);
            file.close();

            SessionService.getInstance().log().info(String.format("[CORBA] SERVER STARTED"));

            //initialize the ORB
            rootPOA.the_POAManager().activate();
            orb.run();

        } catch (WrongPolicy wrongPolicy) {
            wrongPolicy.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidName invalidName) {
            invalidName.printStackTrace();
        } catch (ServantAlreadyActive servantAlreadyActive) {
            servantAlreadyActive.printStackTrace();
        } catch (ObjectNotActive objectNotActive) {
            objectNotActive.printStackTrace();
        } catch (AdapterInactive adapterInactive) {
            adapterInactive.printStackTrace();
        }
    }


}
