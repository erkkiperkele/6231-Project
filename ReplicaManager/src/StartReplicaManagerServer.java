import java.io.IOException;

/**
 * Created by Aymeric on 2015-12-05.
 */
public class StartReplicaManagerServer {

    public static void main(String[] args) {

        //TODO:
        //- Start listening on udp (for error/failure messages)
        //- Replace hardcoded bank names with real getter.
        //- Store processes in a separate class so they can be managed (killed, restarted, etc.)
        //- Logs!

        ReplicaManagerService replicaManager = new ReplicaManagerService();

        String implementationName = "Aymeric";
        ReplicaManagerServer server = new ReplicaManagerServer();

        try {
            server.startBankServers(implementationName);
            server.startFrontEndMessageUdpServer();
            server.startStateTransferUDPServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Servers started");
    }
}
