package Exceptions;

/**
 * A simple exception class to indicate to the client that the
 * record to manipulate was not found
 * Note, this exception should only be used for update or delete methods
 * but not for find or get methods (return empty object instead).
 */
public class RecordNotFoundException extends Exception {

    public RecordNotFoundException(String message) {
        super(message);
    }
}
