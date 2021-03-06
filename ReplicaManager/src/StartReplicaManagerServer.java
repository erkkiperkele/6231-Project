import shared.util.Env;

import java.io.IOException;

/**
 * Created by Aymeric on 2015-12-05.
 */
public class StartReplicaManagerServer {

    public static void main(String[] args) {

        ReplicaManagerService replicaManager = new ReplicaManagerService();
		Env.loadSettings();
        String implementationName = Env.getMachineName();

        ReplicaManagerServer server = new ReplicaManagerServer();

        try {
            server.startBankServers(implementationName);
            server.startFrontEndMessageUdpServer();
            server.initializeBankServers();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Servers started");
    }
}
