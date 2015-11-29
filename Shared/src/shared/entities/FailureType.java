package shared.entities;

/**
 * Created by Aymeric on 2015-11-08.
 */
public enum FailureType {

    benignError,        // 1 software error
    criticalError,      // 3 software errors
    failure             // Crash (server timed out)
}
