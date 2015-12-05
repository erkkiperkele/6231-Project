package shared.udp.message.state;

import shared.data.ReplicaState;

/**
 * Created by Aymeric on 2015-12-05.
 */
public class StateMessage implements IStateAnswer{

    private ReplicaState replicaState;

    public StateMessage(ReplicaState replicaState) {
        this.replicaState = replicaState;
    }

    public ReplicaState getReplicaState() {
        return replicaState;
    }
}
