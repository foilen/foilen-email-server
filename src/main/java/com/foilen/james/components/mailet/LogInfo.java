/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.mailet;

import javax.mail.MessagingException;

import org.apache.mailet.Mail;
import org.apache.mailet.base.GenericMailet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogInfo extends GenericMailet {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogInfo.class);

    private String text;

    @Override
    public void init() throws MessagingException {
        text = getInitParameter("text", "");
    }

    @Override
    public void service(Mail mail) throws MessagingException {
        LOGGER.info("{} - {}", mail.getName(), text);
    }

}
