package dev.zawarudo.holo.utils.exceptions;

/**
 * Exception that is thrown when an API returned an error, i.e. 5XX HTTP codes.
 */
public class APIException extends Exception {

    public APIException() {
        super();
    }

    public APIException(String message) {
        super(message);
    }

    public APIException(String message, Throwable cause) {
        super(message, cause);
    }

    public APIException(Throwable cause) {
        super(cause);
    }
}