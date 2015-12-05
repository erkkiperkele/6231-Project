import shared.data.ReplicaState;
import shared.contracts.IReplicaManagerService;
import shared.data.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class ReplicaManagerService implements IReplicaManagerService {



    @Override
    public void onError(Bank bank, String serverAddress) {

        //TODO:
        // if 3rd error, call onFailure
        // get valid State from replica
    }

    @Override
    public void onFailure(Bank bank, String serverAddress) {

        //TODO:
        // kill instance
        // choose new implementation
        // start new implementation
        // get valid State from replica
    }

    private ReplicaState getReplicaState(){

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
