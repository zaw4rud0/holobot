package dev.zawarudo.holo.exceptions;

public class InvalidRequestException extends Exception {

    public InvalidRequestException() {
    }

    public InvalidRequestException(String message) {
        super(message);
    }
}