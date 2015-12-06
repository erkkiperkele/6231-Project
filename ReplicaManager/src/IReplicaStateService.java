import shared.data.ReplicaState;

/**
 * Created by Aymeric on 2015-12-05.
 */
public interface IReplicaStateService {

    boolean isAlive();
    ReplicaState getReplicaState();
    void setReplicaState(String machineToGetStateFrom, ReplicaState state);

}
