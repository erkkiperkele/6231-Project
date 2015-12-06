import shared.data.Bank;

import java.util.HashMap;
import java.util.Map;


public class ReplicaManagerSession {

    private static ReplicaManagerSession ourInstance = new ReplicaManagerSession();
    private static Map<String, Process> processes = new HashMap<>();

    public static ReplicaManagerSession getInstance() {
        return ourInstance;
    }

    private ReplicaManagerSession() {
    }

    public void registerServer(String serverName, Process serverProcess){

        processes.put(serverName, serverProcess);
    }

    public void unregisterServer(String serverName){

        processes.remove(serverName);
    }

    public Process getServerProcess(String serverName){
        return processes.get(serverName);
    }

    //TODO!!!!! FOR REAL
    public String getServerAddress(){
        return "TODO";
    }

    //TODO!!!!! FOR REAL
    public String getCurrentImplementation() {
        return "Aymeric";
    }

    //TODO!!!!! FOR REAL
    public String getNextImplementation() {
        return "Pascal";
    }
}
