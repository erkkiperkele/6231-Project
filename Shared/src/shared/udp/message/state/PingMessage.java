package shared.udp.message.state;

/**
 * Created by Aymeric on 2015-12-05.
 */
public class PingMessage implements IStateAnswer {

    private boolean isAlive;

    public PingMessage(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public boolean isAlive() {
        return isAlive;
    }
}
