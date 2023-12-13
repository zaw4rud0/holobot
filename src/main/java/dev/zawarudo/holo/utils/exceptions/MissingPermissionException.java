package dev.zawarudo.holo.utils.exceptions;

/**
 * An exception that is thrown when the bot is missing permissions to perform an action within a guild.
 */
public class MissingPermissionException extends Exception {

    public MissingPermissionException() {
        super();
    }

    public MissingPermissionException(String message) {
        super(message);
    }

    public MissingPermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MissingPermissionException(Throwable cause) {
        super(cause);
    }
}