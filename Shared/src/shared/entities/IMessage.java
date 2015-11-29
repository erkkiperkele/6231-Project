package shared.entities;

/**
 * Created by Aymeric on 2015-11-08.
 */
public interface IMessage {

    int getSequenceNumber();
    RequestType getType();
}
