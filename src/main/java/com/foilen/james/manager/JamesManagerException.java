package com.foilen.james.manager;

public class JamesManagerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JamesManagerException(String message) {
        super(message);
    }

    public JamesManagerException(String message, Throwable cause) {
        super(message, cause);
    }

}
