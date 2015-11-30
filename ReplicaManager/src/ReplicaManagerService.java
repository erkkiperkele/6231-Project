import shared.contracts.IReplicaManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class ReplicaManagerService implements IReplicaManager {



    @Override
    public void onError(String serverName) {

    }

    @Override
    public void onFailure(String serverName) {

    }

    @Override
    public void agree() {

    }

    @Override
    public void spawnNewProcess(String implementationName, String bankName) throws IOException {

        String command = getCommand(implementationName, bankName);

        Process p = Runtime.getRuntime().exec(command);
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
                try(BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))){
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
