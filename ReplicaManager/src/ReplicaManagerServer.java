import shared.contracts.IReplicaManagerService;

import java.io.IOException;

public class ReplicaManagerServer {
    private static String[] banks = new String[]{"RBC", "BMO", "Desjardins"};
    private static IReplicaManagerService replicaManagerService;
    private IReplicaStateService replicaStateService;

    public ReplicaManagerServer() {

        this.replicaManagerService = new ReplicaManagerService();
        this.replicaStateService = new ReplicaStateService();
    }

    public void startBankServers(String implementationName) throws IOException {
        for (String bank : banks) {
            replicaManagerService.spawnNewProcess(implementationName, bank);
        }
    }

    public void startFrontEndMessageUdpServer() {
        //TODO!!! use RUDP.

        UdpRMtoFE udpServerFrontEnd= new UdpRMtoFE(replicaManagerService);
        udpServerFrontEnd.start();
    }

    public void startStateTransferUDPServer() {

        UdpRMtoRM udpServerReplicaManager= new UdpRMtoRM(replicaStateService);
        udpServerReplicaManager.startServer();
    }


}
