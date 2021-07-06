/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.handler.fastfail;

import javax.inject.Inject;

import org.apache.james.core.MailAddress;
import org.apache.james.domainlist.api.DomainList;
import org.apache.james.protocols.smtp.SMTPSession;
import org.apache.james.rrt.api.RecipientRewriteTable;
import org.apache.james.rrt.api.RecipientRewriteTableException;
import org.apache.james.user.api.UsersRepository;
import org.apache.james.user.api.UsersRepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foilen.james.components.common.RedirectionManager;

public class ValidRcptHandler extends org.apache.james.smtpserver.fastfail.ValidRcptHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidRcptHandler.class);

    @Inject
    public ValidRcptHandler(UsersRepository users, RecipientRewriteTable recipientRewriteTable, DomainList domains) {
        super(users, recipientRewriteTable, domains);
    }

    @Override
    protected boolean isValidRecipient(SMTPSession session, MailAddress recipient) throws UsersRepositoryException, RecipientRewriteTableException {

        boolean result = false;

        // Normal
        result |= super.isValidRecipient(session, recipient);

        try {
            // Redirection
            result |= !RedirectionManager.getRedirections(recipient).isEmpty();

            // Catch-all
            result |= !RedirectionManager.getCatchAllRedirections(recipient).isEmpty();
        } catch (Exception e) {
            LOGGER.info("Unable to access Redirection", e);
        }

        return result;
    }
}