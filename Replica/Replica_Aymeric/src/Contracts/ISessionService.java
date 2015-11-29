package Contracts;

/**
 * A simple interface for a Session Service
 * holding a current session info and giving access to a logger.
 * Designed to be used as a Singleton.
 */
public interface ISessionService {

    /**
     * Indicates what's the current server's bank.
     * @return
     */
    shared.data.Bank getBank();

    /**
     * Sets the current server's bank at startup.
     * Should not be changed once server is online.
     * @param bank
     */
    void setBank(shared.data.Bank bank);

    /**
     * Returns a file logger where to write the logs for the server.
     * @return
     */
    IFileLogger log();
}
