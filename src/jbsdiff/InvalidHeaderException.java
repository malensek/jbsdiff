package jbsdiff;

/**
 * An exception that indicates a malformed bsdiff header.
 *
 * @author malensek
 */

public class InvalidHeaderException extends Exception {

    public InvalidHeaderException() {
        super();
    }

    public InvalidHeaderException(String detail) {
        super(detail);
    }

    /**
     * Creates an InvalidHeaderException with details about the invalid field
     * that was set, and its value.
     */
    public InvalidHeaderException(String fieldName, int value) {
        super("Invalid header field; " + fieldName + " = " + value);
    }
}

