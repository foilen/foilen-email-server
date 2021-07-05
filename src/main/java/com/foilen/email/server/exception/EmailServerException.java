/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
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
