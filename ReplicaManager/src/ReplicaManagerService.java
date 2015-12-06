import shared.contracts.IReplicaManagerService;
import shared.data.Bank;
import shared.udp.UDPClient;
import shared.util.Constant;
import shared.util.Env;

import java.io.BufferedReader;
import java.io.File;
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
    public void onError(Bank bank, String machineName) {


        //TODO: Return the right server address (dummy address at the moment)

        System.err.println(isSelf(machineName));
        System.err.println(machineName);
        System.err.println(Env.getMachineName());

        if (isSelf(machineName)) {

            stopFrontEnd();
            int newCount = errorCount.get(bank) + 1;

            if (newCount >= Constant.MAX_ERROR_COUNT) {
                Bank[] banks = Bank.getBanks();
                String implementationName = ReplicaManagerSession.getInstance().getNextImplementation();

                //Change all banks implementations because of GetLoan() that needs homogeneous implementations
                for (Bank b : banks) {
                    restartServer(b, implementationName);
                    errorCount.replace(bank, 0);
                }
                System.out.println(String.format(
                        "Server had a 3rd error and restarted all banks with implementation: %1$s",
                        implementationName)
                );

            } else {
                String implementationName = ReplicaManagerSession.getInstance().getCurrentImplementation();
                restartServer(bank, implementationName);
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
                restartServer(b, implementationName);

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
        	File f = new File(System.getProperty("user.dir") + "/../Replica_" + bankName + "_" + implementationName);
            System.err.println(
                    "Spawning process with command: " + command
                    + "\n"
                    + "and working directory: " + f.getAbsoluteFile());

            p = Runtime.getRuntime().exec(command, null, f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ReplicaManagerSession.getInstance().registerServer(bankName, p);

        Thread outputServer1 = getProcessOutputThread(p, implementationName, bankName);
        outputServer1.start();
    }

    private void restartServer(Bank bank, String implementationName) {

        stopBankServer(bank);
        spawnNewProcess(implementationName, bank.name());
        resetState(bank);
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

    private boolean isSelf(String machineName) {

        String myself = Env.getMachineName();

        return machineName.toLowerCase().equals(myself.toLowerCase());
    }

    private void resetState(Bank bank) {
        //TODO!!! How to reset the state!

        //getstate reliably
        //write state on disk
    }
}
