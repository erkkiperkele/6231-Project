import shared.data.Bank;
import shared.util.Env;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ReplicaManagerSession {

    private static ReplicaManagerSession ourInstance = new ReplicaManagerSession();
    private static Map<String, Process> processes = new HashMap<>();
    private static String currentImplementation;

    public static ReplicaManagerSession getInstance() {
        return ourInstance;
    }

    private ReplicaManagerSession() {
    }

    public static void setCurrentImplementation(String currentImplementation) {
        ReplicaManagerSession.currentImplementation = currentImplementation;
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

    public String getCurrentImplementation() {

        return currentImplementation;
    }

    public String getNextImplementation() {

        String current = this.getCurrentImplementation();

        String next = "";
        String first = "";
        boolean keepNext = false;
        boolean isFirst = true;

        Iterator iterator = Env.getListMachineName();
        while(iterator.hasNext()){

            next = (String)iterator.next();

            if(isFirst)
            {
                first = next;
                isFirst = false;
            }
            if (keepNext)
            {
                this.setCurrentImplementation(next);
                return next;
            }

            if (next.equals(current))
            {
                keepNext = true;
            }


        }
        this.setCurrentImplementation(first);
        return first;   //I was the last one therefore I need first implementation
    }
}
