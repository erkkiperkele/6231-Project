import shared.contracts.IReplicaManager;

import java.io.IOException;

public class ReplicaManagerServer {
    private static String[] banks = new String[]{"RBC", "BMO", "Desjardins"};
    private static IReplicaManager replicaManager;

    public static void main(String[] args) {

        //TODO:
        //- Start listening on udp (for error/failure messages)
        //- Replace hardcoded bank names with real getter.
        //- Store processes in a separate class so they can be managed (killed, restarted, etc.)
        //- Logs!

        replicaManager = new ReplicaManagerService();

        String implementationName = "Aymeric";

        try {
            startBankServers(implementationName);
            startUdpServer();
        } catch (IOException e) {
            e.printStackTrace();

        }



        System.out.println("Servers started");
    }

    private static void startBankServers(String implementationName) throws IOException {
        for (String bank : banks) {
            replicaManager.spawnNewProcess(implementationName, bank);
        }
    }

    private static void startUdpServer() {
        //TODO!!!
    }


}
