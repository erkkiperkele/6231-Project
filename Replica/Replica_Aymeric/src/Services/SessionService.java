package Services;

import Contracts.IFileLogger;
import Contracts.ILoggerService;
import Contracts.ISessionService;

/**
 * A singleton class to handle session information and logging services
 * It insures logs are written for the current server.
 * (See interface documentation for more details on functionality)
 */
public class SessionService implements ISessionService {
    private static SessionService ourInstance = new SessionService();
    private static ILoggerService loggerService;
    private Data.Bank bank;

    private SessionService() {
        loggerService = new LoggerService();
    }

    public static SessionService getInstance() {
        return ourInstance;
    }

    @Override
    public Data.Bank getBank() {
        return this.bank;
    }

    @Override
    public void setBank(Data.Bank bank) {
        this.bank = bank;
    }

    @Override
    public IFileLogger log() {
        return loggerService.getLogger();
    }
}
