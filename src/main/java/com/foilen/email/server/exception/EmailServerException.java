package com.foilen.email.server.exception;

public class EmailServerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmailServerException(String message) {
        super(message);
    }

    public EmailServerException(String message, Throwable cause) {
        super(message, cause);
    }

}
