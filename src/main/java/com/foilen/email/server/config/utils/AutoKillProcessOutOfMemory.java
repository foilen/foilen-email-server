package com.foilen.email.server.config.utils;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.james.core.MailAddress;
import org.apache.james.core.Username;
import org.apache.james.core.builder.MimeMessageBuilder;
import org.apache.james.mailbox.MailboxManager;
import org.apache.james.mailbox.MailboxSession;
import org.apache.james.mailbox.MessageManager;
import org.apache.james.mailbox.model.MailboxPath;
import org.apache.james.user.api.UsersRepository;

import jakarta.mail.internet.MimeMessage;

import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.ExecutorsTools;
import com.foilen.smalltools.tools.ThreadTools;

public class AutoKillProcessOutOfMemory extends AbstractBasics implements Runnable {

    private MailboxManager mailboxManager;

    private long delayBetweenOutputInMs = 30000; // Every 30 seconds
    private int killAtPercent = 90; // 90%

    @Inject
    public AutoKillProcessOutOfMemory(UsersRepository usersRepository, @Named("mailboxmanager") MailboxManager mailboxManager) {
        this.mailboxManager = mailboxManager;
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

                    // Try to send email notification to postmaster
                    try {
                        String email = System.getProperty("emailConfig.postmasterEmail");
                        if (email != null && !email.isEmpty()) {
                            Username username = Username.of(email);
                            MailboxSession session = mailboxManager.createSystemSession(username);
                            MailboxPath inboxPath = MailboxPath.inbox(username);
                            
                            // Create the message
                            MimeMessage message = MimeMessageBuilder.mimeMessageBuilder() //
                                    .addFrom(email) //
                                    .addToRecipient(email) //
                                    .setSubject("Autokilled") //
                                    .setText("Autokilled the process because it reached " + killAtPercent + "% . Currently at " + percentUsed + "%") //
                                    .build();
                            
                            // Append to inbox - convert MimeMessage to byte array
                            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                            message.writeTo(baos);
                            
                            MessageManager messageManager = mailboxManager.getMailbox(inboxPath, session);
                            messageManager.appendMessage(MessageManager.AppendCommand.builder()
                                    .recent()
                                    .build(baos.toByteArray()), session);
                            
                            mailboxManager.endProcessingRequest(session);
                        }
                    } catch (Exception e) {
                        logger.error("Failed to send autokill notification email", e);
                    }
                    
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
