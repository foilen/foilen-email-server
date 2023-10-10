/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2023 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components;

import javax.mail.MessagingException;

public class MessagingExceptionWrapper {

    private MessagingException messagingException;

    public MessagingException getMessagingException() {
        return messagingException;
    }

    public void setMessagingException(MessagingException messagingException) {
        this.messagingException = messagingException;
    }

}
