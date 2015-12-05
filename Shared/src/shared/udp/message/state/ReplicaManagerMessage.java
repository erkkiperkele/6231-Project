package shared.udp.message.state;

import shared.udp.message.state.IStateAnswer;
import shared.udp.message.state.StateMessageType;

/**
 * Created by Aymeric on 2015-12-05.
 */
public class ReplicaManagerMessage {

    private StateMessageType stateMessageType;
    private IStateAnswer answer;

    public ReplicaManagerMessage(StateMessageType stateMessageType, IStateAnswer answer) {
        this.stateMessageType = stateMessageType;
        this.answer = answer;
    }

    public StateMessageType getStateMessageType() {
        return stateMessageType;
    }

    public IStateAnswer getAnswer() {
        return answer;
    }
}
