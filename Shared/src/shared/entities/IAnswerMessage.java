package shared.entities;

/**
 * Simple interface for answer messages.
 */
public interface IAnswerMessage extends IMessage {

    IAnswer getAnswerData();
}