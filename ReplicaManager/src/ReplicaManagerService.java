import shared.contracts.IReplicaManagerService;
import shared.data.Bank;
import shared.udp.UDPClient;
import shared.util.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;


public class ReplicaManagerService implements IReplicaManagerService {

    private UDPClient udpFrontEnd;
    private HashMap<Bank, Integer> errorCount;

    public ReplicaManagerService() {
        this.udpFrontEnd = new UDPClient();
        this.errorCount = new HashMap<>();
        errorCount.put(Bank.Royal, 0);
        errorCount.put(Bank.Dominion, 0);
        errorCount.put(Bank.National, 0);

    }

    @Override
    public void onError(Bank bank, String serverAddress) {


        //TODO: Return the right server address (dummy address at the moment)

        if (isSelf(serverAddress)) {

            stopFrontEnd();
            int newCount = errorCount.get(bank) + 1;
            Bank[] banks = Bank.getBanks();

            if (newCount >= Constant.MAX_ERROR_COUNT) {
                String implementationName = ReplicaManagerSession.getInstance().getNextImplementation();

                //Change all banks implementations because of GetLoan() that needs homogeneous implementations
                for (Bank b : banks) {
                    restartServers(b, implementationName);
                    errorCount.replace(bank, 0);
                }
                System.out.println(String.format(
                        "Server had a 3rd error and restarted all banks with implementation: %1$s",
                        implementationName)
                );

            } else {
                String implementationName = ReplicaManagerSession.getInstance().getCurrentImplementation();
                restartServers(bank, implementationName);
                errorCount.replace(bank, newCount);

                System.out.println(String.format("Server had an error and restarted"));
            }
            startFrontEnd();
        }
    }

    @Override
    public void onFailure(Bank bank, String serverAddress) {

        if (isSelf(serverAddress)) {
            stopFrontEnd();
            String implementationName = ReplicaManagerSession.getInstance().getNextImplementation();
            Bank[] banks = Bank.getBanks();

            //Change all banks implementations because of GetLoan() that needs homogeneous implementations
            for (Bank b : banks) {
                restartServers(b, implementationName);

                errorCount.replace(bank, 0);
            }

            errorCount.replace(bank, 0);

            System.out.println(String.format(
                    "Server had a failure and restarted all banks with implementation: %1$s",
                    implementationName));

            startFrontEnd();
        }
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

    private void restartServers(Bank bank, String implementationName) {

        stopBankServer(bank);
        resetState(bank);
        spawnNewProcess(implementationName, bank.name());
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

    private void stopFrontEnd() {

        String isStopped = "";
        int retryCount = 0;

        while (isStopped != Constant.FE_STOPPED && retryCount < 5) {
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

        while (isStarted != Constant.FE_STARTED && retryCount < 5) {
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
        ReplicaManagerSession.getInstance().unregisterServer(bank.name());
    }

    private boolean isSelf(String serverAddress) {
        String thisReplicaServerAddress = ReplicaManagerSession.getInstance().getServerAddress();
        return serverAddress.toLowerCase().equals(thisReplicaServerAddress.toLowerCase());
    }

    private void resetState(Bank bank) {
        //TODO!!! How to reset the state!

        //getstate reliably
        //write state on disk
    }
}
