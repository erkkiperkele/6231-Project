import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class ReplicaManagerServer {
    private static String[] banks = new String[] { "RBC", "BMO", "Desjardins" };
    private static Map<String, Process> processes;

    public static void main(String[] args) {

        //TODO:
        //- Start listening on udp (for error/failure messages)
        //- Replace hardcoded bank names with real getter.
        //- Store processes in a separate class so they can be managed (killed, restarted, etc.)
        //- Logs!


        String implementationName = "Aymeric";

        try {

            processes = new HashMap<>();

            for (String bank : banks)
            {

                String command = getCommand(implementationName, bank);
                Process p = spawnNewProcess(implementationName, bank, command);
                processes.put(bank, p);
            }

            System.out.println("Servers started");

//            Thread.sleep(2000);
//            killServer(banks[0]);       //Test Only

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void killServer(String bankName) {
        Process p = processes.get(bankName);
        p.destroy();
        System.err.println(bankName + " has been killed");
    }

    private static Process spawnNewProcess(String implementationName, String bankName, String command) throws IOException {
        Process p1 = Runtime.getRuntime().exec(command);
        Thread outputServer1 = getProcessOutputThread(p1, implementationName, bankName);
        outputServer1.start();
        return p1;
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
