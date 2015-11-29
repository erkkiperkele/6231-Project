package Server;

import Contracts.IBankService;
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
import shared.data.Bank;
import shared.data.Customer;
import shared.data.Loan;
import shared.udp.CreateAccountMessage;
import shared.udp.GetAccountMessage;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Calendar;
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

        String serverArg = args.length > 0
                ? args[0]
                : "1";

        serverArg = serverArg.length() > 0
                ? "" + Bank.fromString(serverArg).toInt()
                : serverArg;

        initialize(serverArg);

        //Starting bank server
        startUDPServer();
//        startCorbaServer();

        //TEST ONLY (to show server is active even when there's no activity)
//        while(true)
//        {
//            try {
//                GetAccountMessage test = new GetAccountMessage("","");
//
//                System.out.println("dummy test");
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private static void initialize(String arg) {
        shared.data.Bank serverName = shared.data.Bank.fromInt(Integer.parseInt(arg));
        SessionService.getInstance().setBank(serverName);
        bankService = new BankService();
        udp = new UDPServer(bankService);
    }

    private static void startUDPServer() {
        Thread startUdpServer = new Thread(() ->
        {
            udp.startServer();
        });
        startUdpServer.start();
        SessionService.getInstance().log().info(String.format("[UDP] SERVER STARTED"));

    }
}
