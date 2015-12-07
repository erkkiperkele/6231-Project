package Server;

import Contracts.IBankService;
import Services.BankService;
import Services.SessionService;
import shared.data.Bank;
import shared.udp.UDPReplicaToReplicaManagerHandleRequestThread;
import shared.udp.UDPReplicaToReplicaManagerThread;
import shared.util.Env;

/**
 * This class starts both RMI and UDP servers for a given bank.
 * It also contains basic tests for data access (concurrency, UDP protocols...)
 */
public class StartBankServer {

    private static IBankService bankService;

    private static UDPServer udpInternal;
    private static BankServerUdp bankServerUdp;
    private static UDPReplicaToReplicaManagerThread udpRtoRM;
    private static UDPReplicaToReplicaManagerHandleRequestThread udpRtoRMRequest;


    /**
     * Instatiates and starts the RMI and UDP servers.
     * ATTENTION: needs a single integer argument which is the bank Id for the server
     * See the Bank enum to know what integer corresponds to what bank.
     *
     * @param args a single integer defining what bank this server belongs to.
     */
    public static void main(String[] args) {

        Env.loadSettings();

        String serverArg = args.length > 0
                ? args[0]
                : "RBC";

        serverArg = serverArg.length() > 0
                ? "" + Bank.fromString(serverArg).toInt()
                : serverArg;

        initialize(serverArg);

        //Starting bank server
        startBankServer();
        startUDPServerForInternalOperations();
        startUdpThreads();
    }

    private static void startUdpThreads() {

        try {
            Env.setCurrentBank(Bank.Royal);     //HACK To get the Thread initialization working.
            udpRtoRM = new UDPReplicaToReplicaManagerThread(bankServerUdp);
            udpRtoRMRequest = new UDPReplicaToReplicaManagerHandleRequestThread();
            udpRtoRM.start();
            udpRtoRMRequest.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initialize(String arg) {
        shared.data.Bank serverName = shared.data.Bank.fromInt(Integer.parseInt(arg));
        SessionService.getInstance().setBank(serverName);
        bankService = new BankService();
        udpInternal = new UDPServer(bankService);

    }

    private static void startUDPServerForInternalOperations() {
        Thread startUdpServer = new Thread(() ->
        {
            udpInternal.startServer();
        });
        startUdpServer.start();
        SessionService.getInstance().log().info(String.format("[UDP] INTERNAL SERVER STARTED"));

    }

    private static void startBankServer() {

        IBankService bankService = new BankService();

        bankServerUdp = new BankServerUdp(bankService);
        bankServerUdp.start();
        SessionService.getInstance().log().info(String.format("[UDP] BANK SERVER STARTED"));
    }
}
