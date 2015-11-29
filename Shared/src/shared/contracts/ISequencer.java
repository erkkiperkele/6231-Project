package shared.contracts;

import shared.entities.IAnswer;
import shared.entities.IFailure;
import shared.entities.IRequest;

/**
 * Responsible to reliably sequence then send messages using UDP messaging.
 * Serializes any IRequest arguments.
 * Returns any IAnswer data received from server
 */
public interface ISequencer {

    /**
     * packages and multicast a request message to the Replicas
     * @param message
     * @return
     */
    IAnswer[] sendRequestMessage(IRequest message);

    /**
     * packages and multicast an error message to the Replica Managers
     * @param message
     */
    void sendErrorMessage(IFailure message);
}
