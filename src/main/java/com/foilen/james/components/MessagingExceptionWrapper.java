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
