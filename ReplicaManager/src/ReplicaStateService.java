import shared.data.ReplicaState;

/**
 * Created by Aymeric on 2015-12-05.
 */
public class ReplicaStateService implements IReplicaStateService {

    @Override
    public boolean isAlive() {

        //TODO: how to determine is process is still alive?
        return true;
    }

    @Override
    public ReplicaState getReplicaState() {

        //TODO: read state from disk and return it.
        return null;
    }

    @Override
    public void setReplicaState(ReplicaState state) {
        //TODO: write the state on disk and ask bank to reload it.
    }
}