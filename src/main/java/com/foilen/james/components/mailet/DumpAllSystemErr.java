/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.mailet;

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

import com.google.common.collect.Multimap;

public class DumpAllSystemErr extends GenericMailet {

    private static final Logger LOGGER = LoggerFactory.getLogger(DumpAllSystemErr.class);

    @Override
    public String getMailetInfo() {
        return "Dumps config and message to System.err";
    }

    @Override
    public void init() throws MessagingException {
        super.init();

        System.err.println("---[Mailet Config]---");
        MailetConfig mailetConfig = getMailetConfig();
        System.err.println("Mailet Name: " + mailetConfig.getMailetName());

        System.err.println("---[Mailet Config - Init Parameters ]---");
        mailetConfig.getInitParameterNames().forEachRemaining(name -> {
            System.err.println(name + " -> " + mailetConfig.getInitParameter(name));
        });

        System.err.println("------------");
    }

    @Override
    public void service(Mail mail) throws MessagingException {
        try {

            // Attributes
            System.err.println("---[Mail - Attributes]---");
            mail.attributes() //
                    .sorted((a, b) -> a.getName().asString().compareTo(b.getName().asString())) //
                    .forEach(attribute -> {
                        System.err.println(attribute.getName().asString() + " -> " + attribute.getValue().getValue());
                    });

            System.err.println("---[Mail - MaybeSender]---");
            MaybeSender maybeSender = mail.getMaybeSender();
            if (maybeSender == null) {
                System.err.println("Sender is null");
            } else {
                System.err.println("Email: " + maybeSender.asString());
            }

            // Recipients
            System.err.println("---[Mail - Recipients]---");
            mail.getRecipients().forEach(it -> {
                System.err.println(it.asString());
            });

            // Headers
            System.err.println("---[Mail - Headers]---");
            Enumeration<Header> headers = mail.getMessage().getAllHeaders();
            while (headers.hasMoreElements()) {
                Header header = headers.nextElement();
                System.err.println(header.getName() + " -> " + header.getValue());
            }
            System.err.println("---[Mail - Headers Per Recipient]---");
            Multimap<MailAddress, org.apache.mailet.PerRecipientHeaders.Header> headersByRecipient = mail.getPerRecipientSpecificHeaders().getHeadersByRecipient();
            headersByRecipient.entries().forEach(entry -> {
                org.apache.mailet.PerRecipientHeaders.Header header = entry.getValue();
                System.err.println(entry.getKey().asString() + " : " + header.getName() + " -> " + header.getValue());
            });

            // Message
            System.err.println("---[Mail - Message]---");
            MimeMessage message = mail.getMessage();
            message.writeTo(System.err);
            System.err.println("------------");
        } catch (IOException e) {
            LOGGER.error("error printing message", e);
        }
    }

}
