/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.mailet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.james.core.MailAddress;
import org.apache.james.core.MaybeSender;
import org.apache.mailet.Mail;
import org.apache.mailet.MailetConfig;
import org.apache.mailet.base.GenericMailet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foilen.smalltools.tools.CharsetTools;
import com.google.common.collect.Multimap;

public class DumpAllLoggerInfo extends GenericMailet {

    private static final Logger LOGGER = LoggerFactory.getLogger(DumpAllLoggerInfo.class);

    @Override
    public String getMailetInfo() {
        return "Dumps config and message to Logger.info";
    }

    @Override
    public void init() throws MessagingException {
        super.init();

        LOGGER.info("---[Mailet Config]---");
        MailetConfig mailetConfig = getMailetConfig();
        LOGGER.info("Mailet Name: " + mailetConfig.getMailetName());

        LOGGER.info("---[Mailet Config - Init Parameters ]---");
        mailetConfig.getInitParameterNames().forEachRemaining(name -> {
            LOGGER.info(name + " -> " + mailetConfig.getInitParameter(name));
        });

        LOGGER.info("------------");
    }

    @Override
    public void service(Mail mail) throws MessagingException {
        try {

            // Attributes
            LOGGER.info("---[Mail - Attributes]---");
            mail.attributes() //
                    .sorted((a, b) -> a.getName().asString().compareTo(b.getName().asString())) //
                    .forEach(attribute -> {
                        LOGGER.info(attribute.getName().asString() + " -> " + attribute.getValue().getValue());
                    });

            LOGGER.info("---[Mail - MaybeSender]---");
            MaybeSender maybeSender = mail.getMaybeSender();
            if (maybeSender == null) {
                LOGGER.info("Sender is null");
            } else {
                LOGGER.info("Email: " + maybeSender.asString());
            }

            // Recipients
            LOGGER.info("---[Mail - Recipients]---");
            mail.getRecipients().forEach(it -> {
                LOGGER.info(it.asString());
            });

            // Headers
            LOGGER.info("---[Mail - Headers]---");
            Enumeration<Header> headers = mail.getMessage().getAllHeaders();
            while (headers.hasMoreElements()) {
                Header header = headers.nextElement();
                LOGGER.info(header.getName() + " -> " + header.getValue());
            }
            LOGGER.info("---[Mail - Headers Per Recipient]---");
            Multimap<MailAddress, org.apache.mailet.PerRecipientHeaders.Header> headersByRecipient = mail.getPerRecipientSpecificHeaders().getHeadersByRecipient();
            headersByRecipient.entries().forEach(entry -> {
                org.apache.mailet.PerRecipientHeaders.Header header = entry.getValue();
                LOGGER.info(entry.getKey().asString() + " : " + header.getName() + " -> " + header.getValue());
            });

            // Message
            LOGGER.info("---[Mail - Message]---");
            MimeMessage message = mail.getMessage();
            ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
            message.writeTo(messageStream);
            LOGGER.info(messageStream.toString(CharsetTools.UTF_8.name()));
            LOGGER.info("------------");
        } catch (IOException e) {
            LOGGER.error("error printing message", e);
        }
    }

}
