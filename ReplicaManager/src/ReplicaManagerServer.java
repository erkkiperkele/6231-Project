import shared.contracts.IReplicaManagerService;
import shared.data.Bank;
import shared.util.Constant;
import shared.util.Env;

import java.io.IOException;

public class ReplicaManagerServer {
    private static Bank[] banks = new Bank[]{Bank.Royal, Bank.Dominion, Bank.National};
    private static IReplicaManagerService replicaManagerService;

    public ReplicaManagerServer() {

        this.replicaManagerService = new ReplicaManagerService();
    }

    public void startBankServers(String implementationName) throws IOException {
        for (Bank bank : banks) {
            replicaManagerService.spawnNewProcess(implementationName, bank.name());
        }
    }

    public void startFrontEndMessageUdpServer() {
        UdpRMtoFEListener udpServerFrontEnd = new UdpRMtoFEListener(replicaManagerService);
        udpServerFrontEnd.start();
    }

    public void initializeBankServers() {
        String masterMachine = Constant.MASTER_MACHINE_NAME;
        if (!Env.getMachineName().equals(masterMachine)) {
            for (Bank bank : banks) {
                replicaManagerService.resetState(masterMachine, Env.getMachineName(), bank);
            }
        }
    }

}
