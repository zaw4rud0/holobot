package dev.zawarudo.holo.utils.exceptions;

import java.io.IOException;

public class HttpTransportException extends IOException {
    public HttpTransportException(String message, Throwable cause) {
        super(message, cause);
    }
}