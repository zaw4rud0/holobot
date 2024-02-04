package dev.zawarudo.holo.utils.exceptions;

public class NotFoundException extends Exception {

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable e) {
        super(message, e);
    }

    public NotFoundException(Throwable e) {
        super(e);
    }
}