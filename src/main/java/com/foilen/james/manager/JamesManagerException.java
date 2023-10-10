/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2023 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
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
