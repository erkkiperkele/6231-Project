import shared.contracts.IReplicaManagerService;
import shared.data.Bank;

import java.io.IOException;

public class ReplicaManagerServer {
    private static String[] banks = new String[]{Bank.Royal.name(), Bank.Dominion.name(), Bank.National.name()};
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

        UdpRMtoFEListener udpServerFrontEnd= new UdpRMtoFEListener(replicaManagerService);
        udpServerFrontEnd.start();
    }

    public void startStateTransferUDPServer() {

        UdpRMtoRMListener udpServerReplicaManager= new UdpRMtoRMListener(replicaStateService);
        udpServerReplicaManager.start();
    }


}
