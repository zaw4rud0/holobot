package dev.zawarudo.holo.exceptions;

/**
 * Exception that is thrown when a request to an API is invalid, i.e. 4XX HTTP codes.
 */
public class InvalidRequestException extends Exception {

    public InvalidRequestException() {
        super();
    }

    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRequestException(Throwable cause) {
        super(cause);
    }
}