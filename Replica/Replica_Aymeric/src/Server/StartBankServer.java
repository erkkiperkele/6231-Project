package Server;

import Contracts.IBankService;
import Services.BankService;
import Services.SessionService;
import shared.data.Bank;
import shared.udp.UDPServerThread;

/**
 * This class starts both RMI and UDP servers for a given bank.
 * It also contains basic tests for data access (concurrency, UDP protocols...)
 */
public class StartBankServer {

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
        startBankServer();
        startUDPServerForInternalOperations();
    }

    private static void initialize(String arg) {
        shared.data.Bank serverName = shared.data.Bank.fromInt(Integer.parseInt(arg));
        SessionService.getInstance().setBank(serverName);
        bankService = new BankService();
        udp = new UDPServer(bankService);
    }

    private static void startUDPServerForInternalOperations() {
        Thread startUdpServer = new Thread(() ->
        {
            udp.startServer();
        });
        startUdpServer.start();
        SessionService.getInstance().log().info(String.format("[UDP] SERVER STARTED"));

    }

    private static void startBankServer() {

        IBankService bankService = new BankService();

        BankServerUdp bankServer = new BankServerUdp(bankService);
    }
}
