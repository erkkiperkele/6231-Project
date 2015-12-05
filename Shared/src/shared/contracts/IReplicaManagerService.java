package shared.contracts;

import shared.data.Bank;

import java.io.IOException;

/**
 * Created by Aymeric on 2015-11-08.
 */
public interface IReplicaManagerService {

    //TODO: Write the real ReplicaManager Interface

    void onError(Bank bank, String serverAddress);
    void onFailure(Bank bank, String serverAddress);
    void agree();

    void spawnNewProcess(String implementationName, String bankName) throws IOException;
}