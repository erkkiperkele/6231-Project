import shared.data.ReplicaState;
import shared.contracts.IReplicaManagerService;
import shared.data.Bank;
import shared.udp.UDPClient;
import shared.util.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class ReplicaManagerService implements IReplicaManagerService {

    private UDPClient udpFrontEnd;

    public ReplicaManagerService() {
        this.udpFrontEnd = new UDPClient();
    }

    @Override
    public void onError(Bank bank, String serverAddress) {

        //TODO:
        // if 3rd error, call onFailure
        // get valid State from replica


        //stopFE

        stopFrontEnd();
        stopBankServer(bank);
        spawnNewProcess("Aymeric", bank.name());
        //resetState


        startFrontEnd();

        System.out.println("Server restarted");
    }

    private void stopFrontEnd() {

        String isStopped = "";
        int retryCount = 0;

        while (isStopped != Constant.FE_STOPPED && retryCount < 5)
        {
            try {
                byte[] answer = udpFrontEnd.sendMessage(Constant.STOP_FE.getBytes(), Constant.FE_TO_RM_LISTENER_PORT);
                isStopped = new String(answer, "UTF-8").trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startFrontEnd() {

        String isStarted = "";
        int retryCount = 0;

        while (isStarted != Constant.FE_STARTED && retryCount < 5)
        {
            try {
                byte[] answer = udpFrontEnd.sendMessage(Constant.START_FE.getBytes(), Constant.FE_TO_RM_LISTENER_PORT);
                isStarted = new String(answer, "UTF-8").trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopBankServer(Bank bank) {
        Process p = ReplicaManagerSession.getInstance().getServerProcess(bank.name());

        if (p != null) {
            p.destroy();
        }


    }

    @Override
    public void onFailure(Bank bank, String serverAddress) {

        //TODO:
        // kill instance
        // choose new implementation
        // start new implementation
        // get valid State from replica

        System.out.println("RECEIVED a FAILURE message");
    }

    private ReplicaState getReplicaState() {

        //TODO:
        // request all 3 bank states to 1st replica in the list
        // kill all 3 bank servers
        // restart all 3 bank server with the new state

        return null;

    }

    @Override
    public void agree() {

    }

    @Override
    public void spawnNewProcess(String implementationName, String bankName) {

        String command = getCommand(implementationName, bankName);

        Process p = null;
        try {
            p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ReplicaManagerSession.getInstance().registerServer(bankName, p);

        Thread outputServer1 = getProcessOutputThread(p, implementationName, bankName);
        outputServer1.start();
    }


    public void killServer(String bankName) {
        Process p = ReplicaManagerSession.getInstance().getServerProcess(bankName);
        p.destroy();
        ReplicaManagerSession.getInstance().unregisterServer(bankName);
        System.err.println(bankName + " has been killed");
    }

    private static String getCommand(String implementationName, String bankName) {
        CommandBuilder commandBuilder = new CommandBuilder();
        return commandBuilder
                .setImplementation(implementationName)
                .setBank(bankName)
                .getCommand();
    }

    private static Thread getProcessOutputThread(Process process, String implementationName, String bankName) {
        return new Thread(() ->
        {
            String processOutput;
            try {
                try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    while ((processOutput = input.readLine()) != null) {
                        System.out.println(
                                String.format("[%1$s][%2$s] - %3$s",
                                        implementationName,
                                        bankName,
                                        processOutput
                                )
                        );
                    }
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
