package Contracts;

/**
 * A simple loggerService whose only goal is to abstract the access to a logger.
 * (in order to easily support multiple loggers, such as separate loggers per sessions)
 */
public interface ILoggerService {

    IFileLogger getLogger();
}
