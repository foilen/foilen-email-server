/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2023 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.email.server.config.utils;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.james.core.MailAddress;
import org.apache.james.core.builder.MimeMessageBuilder;
import org.apache.james.mailbox.MailboxManager;
import org.apache.james.mailbox.model.MailboxConstants;
import org.apache.james.metrics.api.MetricFactory;
import org.apache.james.server.core.MailImpl;
import org.apache.james.transport.mailets.delivery.MailDispatcher;
import org.apache.james.transport.mailets.delivery.MailboxAppender;
import org.apache.james.transport.mailets.delivery.SimpleMailStore;
import org.apache.james.user.api.UsersRepository;
import org.apache.mailet.Mail;
import org.apache.mailet.MailetContext;

import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.ExecutorsTools;
import com.foilen.smalltools.tools.ThreadTools;

public class AutoKillProcessOutOfMemory extends AbstractBasics implements Runnable {

    private MailDispatcher mailDispatcher;

    private long delayBetweenOutputInMs = 30000; // Every 30 seconds
    private int killAtPercent = 90; // 90%

    @Inject
    public AutoKillProcessOutOfMemory(UsersRepository usersRepository, @Named("mailboxmanager") MailboxManager mailboxManager, MetricFactory metricFactory, MailetContext mailetContext) {
        mailDispatcher = MailDispatcher.builder() //
                .mailStore(SimpleMailStore.builder() //
                        .mailboxAppender(new MailboxAppender(mailboxManager)) //
                        .usersRepository(usersRepository) //
                        .folder(MailboxConstants.INBOX) //
                        .metric(metricFactory.generate("autoKillMails")) //
                        .build())
                .consume(true) //
                .mailetContext(mailetContext) //
                .build();
        start();
    }

    public long getDelayBetweenOutputInMs() {
        return delayBetweenOutputInMs;
    }

    public int getKillAtPercent() {
        return killAtPercent;
    }

    @Override
    public void run() {

        long lastCheckedTime = 0;

        logger.info("Starting to monitor usage");

        for (;;) {
            try {

                // Wait for the next time to execute
                long nextExecutionTime = lastCheckedTime + delayBetweenOutputInMs;
                long waitTimeInMs = nextExecutionTime - System.currentTimeMillis();
                if (waitTimeInMs > 0) {
                    ThreadTools.sleep(waitTimeInMs);
                }

                // Get the details
                lastCheckedTime = System.currentTimeMillis();

                // JVM Memory
                long free = Runtime.getRuntime().freeMemory();
                long total = Runtime.getRuntime().totalMemory();
                long max = Runtime.getRuntime().maxMemory();
                long used = total - free;
                long percentUsed = 100 * used / max;

                if (percentUsed >= killAtPercent) {
                    logger.error("Used memory has reached {}% . Currently at {}% . Killing the process", killAtPercent, percentUsed);

                    // Send email
                    String email = System.getProperty("emailConfig.postmasterEmail");
                    Mail mail = MailImpl.builder() //
                            .name(getClass().getSimpleName()) //
                            .sender(new MailAddress(email)) //
                            .addRecipients(new MailAddress(email)) //
                            .mimeMessage(MimeMessageBuilder.mimeMessageBuilder() //
                                    .addFrom(email) //
                                    .addToRecipient(email) //
                                    .setSubject("Autokilled") //
                                    .setText("Autokilled the process because it reached " + killAtPercent + "% . Currently at " + percentUsed + "%") //
                                    .build() //
                            ) //
                            .build();
                    mailDispatcher.dispatch(mail);
                    System.exit(1);
                }

            } catch (Exception e) {
                logger.error("Problem checking the resource usage", e);
            }
        }
    }

    public AutoKillProcessOutOfMemory setDelayBetweenChecksInMs(long delayBetweenOutputInMs) {
        this.delayBetweenOutputInMs = delayBetweenOutputInMs;
        return this;
    }

    public AutoKillProcessOutOfMemory setKillAtPercent(int killAtPercent) {
        this.killAtPercent = killAtPercent;
        return this;
    }

    /**
     * Start checks at the fixed rate.
     */
    public AutoKillProcessOutOfMemory start() {
        ExecutorsTools.getCachedDaemonThreadPool().submit(this);
        return this;
    }
}
