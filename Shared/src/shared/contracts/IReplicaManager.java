package shared.contracts;

import java.io.IOException;

/**
 * Created by Aymeric on 2015-11-08.
 */
public interface IReplicaManager {

    //TODO: Write the real ReplicaManager Interface

    void onError(String serverName);
    void onFailure(String serverName);
    void agree();

    void spawnNewProcess(String implementationName, String bankName) throws IOException;
}
