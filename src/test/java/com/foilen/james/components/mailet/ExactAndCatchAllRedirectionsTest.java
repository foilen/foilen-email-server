/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2023 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.mailet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.internet.AddressException;
import javax.sql.DataSource;

import org.apache.james.core.MailAddress;
import org.apache.mailet.Mail;
import org.apache.mailet.Mailet;
import org.apache.mailet.MailetConfig;
import org.apache.mailet.PerRecipientHeaders.Header;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactory;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.foilen.james.components.stub.MailStub;
import com.foilen.james.components.stub.MailetConfigStub;
import com.google.common.base.Joiner;

public class ExactAndCatchAllRedirectionsTest {

    private void execute(List<String> initialRecipients, List<String> expectedFinalRecipients, Mailet mailet, List<String> expectedRedirectedEmails) throws Exception {

        System.out.println("---");

        // Prepare
        Mail mail = new MailStub();
        mail.setRecipients(initialRecipients.stream().map(it -> {
            try {
                return new MailAddress(it);
            } catch (AddressException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));

        // Add headers everywhere
        mail.getMessage().addHeader(ExactAndCatchAllRedirections.HEADER_IS_REDIRECTION, "true");
        initialRecipients.forEach(it -> {
            try {
                mail.addSpecificHeaderForRecipient(Header.builder().name(ExactAndCatchAllRedirections.HEADER_IS_REDIRECTION).value("true").build(), new MailAddress(it));
            } catch (AddressException e) {
                throw new RuntimeException(e);
            }
        });

        // Call
        mailet.service(mail);

        // Assert
        List<String> actualRecipients = mail.getRecipients().stream().map(it -> it.asString()).sorted().collect(Collectors.toList());
        List<String> expectedRecipients = expectedFinalRecipients.stream().sorted().collect(Collectors.toList());
        Assert.assertEquals(Joiner.on('\n').join(expectedRecipients), Joiner.on('\n').join(actualRecipients));

        // Get those marked different
        boolean allDifferent = mail.getMessage().getHeader(ExactAndCatchAllRedirections.HEADER_IS_REDIRECTION) != null;
        Set<String> actualRedirectedEmails = new HashSet<>();
        if (allDifferent) {
            System.out.println("Marked all");
            mail.getRecipients().forEach(it -> actualRedirectedEmails.add(it.asString()));
        } else {
            mail.getPerRecipientSpecificHeaders().getHeadersByRecipient().asMap().forEach((mailAddress, headers) -> {
                headers.forEach(header -> {
                    if (ExactAndCatchAllRedirections.HEADER_IS_REDIRECTION.equals(header.getName())) {
                        System.out.println("Marked " + mailAddress.asString());
                        actualRedirectedEmails.add(mailAddress.asString());
                    }
                });
            });
        }

        // Assert those marked different
        actualRecipients = actualRedirectedEmails.stream().sorted().collect(Collectors.toList());
        expectedRecipients = expectedRedirectedEmails.stream().sorted().collect(Collectors.toList());
        Assert.assertEquals(Joiner.on('\n').join(expectedRecipients), Joiner.on('\n').join(actualRecipients));

    }

    @Test
    public void testServiceMail() throws Exception {

        ExactAndCatchAllRedirections mailet = new ExactAndCatchAllRedirections();

        // Prepare DB
        EmbeddedDatabaseFactory databaseFactory = new EmbeddedDatabaseFactory();
        databaseFactory.setDatabaseType(EmbeddedDatabaseType.H2);
        DataSource dataSource = databaseFactory.getDatabase();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        mailet.setDataSource(dataSource);

        // Populate test data: Local account
        MailetConfig newConfig = new MailetConfigStub(Arrays.asList( //
                "a1@s1.example.com", //
                "a2@s1.example.com", //
                "a3@s1.example.com", //
                "a4@s1.example.com", //
                "a5@s1.example.com", //
                "catchall1@example.com", //
                "catchall2@example.com" //
        ).stream().map(it -> {
            try {
                return new MailAddress(it);
            } catch (AddressException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        mailet.init(newConfig);

        // Populate test data: Redirection
        jdbcTemplate.update("INSERT INTO FOILEN_REDIRECTIONS(FROM_USER, FROM_DOMAIN, TO_EMAIL) VALUES ('r1', 's1.example.com', 'a1@s1.example.com')");
        jdbcTemplate.update("INSERT INTO FOILEN_REDIRECTIONS(FROM_USER, FROM_DOMAIN, TO_EMAIL) VALUES ('r1', 's1.example.com', 'a2@s1.example.com')");
        jdbcTemplate.update("INSERT INTO FOILEN_REDIRECTIONS(FROM_USER, FROM_DOMAIN, TO_EMAIL) VALUES ('r1', 's1.example.com', 'outside@out.example.com')");
        jdbcTemplate.update("INSERT INTO FOILEN_REDIRECTIONS(FROM_USER, FROM_DOMAIN, TO_EMAIL) VALUES ('r2', 's1.example.com', 'a3@s1.example.com')");
        jdbcTemplate.update("INSERT INTO FOILEN_REDIRECTIONS(FROM_USER, FROM_DOMAIN, TO_EMAIL) VALUES ('r3', 's2.example.com', 'a4@s1.example.com')");

        // Populate test data: Catch-all
        jdbcTemplate.update("INSERT INTO FOILEN_REDIRECTIONS(FROM_USER, FROM_DOMAIN, TO_EMAIL) VALUES ('*', 's1.example.com', 'catchall1@example.com')");
        jdbcTemplate.update("INSERT INTO FOILEN_REDIRECTIONS(FROM_USER, FROM_DOMAIN, TO_EMAIL) VALUES ('*', 's1.example.com', 'catchall2@example.com')");

        // Populate test data: Recursive redirect
        jdbcTemplate.update("INSERT INTO FOILEN_REDIRECTIONS(FROM_USER, FROM_DOMAIN, TO_EMAIL) VALUES ('rm1', 's3.example.com', 'r1@s1.example.com')");
        jdbcTemplate.update("INSERT INTO FOILEN_REDIRECTIONS(FROM_USER, FROM_DOMAIN, TO_EMAIL) VALUES ('rm1', 's3.example.com', 'a5@s1.example.com')");
        jdbcTemplate.update("INSERT INTO FOILEN_REDIRECTIONS(FROM_USER, FROM_DOMAIN, TO_EMAIL) VALUES ('l1', 's1.example.com', 'a1@s1.example.com')");
        jdbcTemplate.update("INSERT INTO FOILEN_REDIRECTIONS(FROM_USER, FROM_DOMAIN, TO_EMAIL) VALUES ('l1', 's1.example.com', 'l2@s1.example.com')");
        jdbcTemplate.update("INSERT INTO FOILEN_REDIRECTIONS(FROM_USER, FROM_DOMAIN, TO_EMAIL) VALUES ('l2', 's1.example.com', 'l1@s1.example.com')");
        jdbcTemplate.update("INSERT INTO FOILEN_REDIRECTIONS(FROM_USER, FROM_DOMAIN, TO_EMAIL) VALUES ('l2', 's1.example.com', 'a2@s1.example.com')");
        jdbcTemplate.update("INSERT INTO FOILEN_REDIRECTIONS(FROM_USER, FROM_DOMAIN, TO_EMAIL) VALUES ('l2', 's1.example.com', 'r1@s1.example.com')");
        jdbcTemplate.update("INSERT INTO FOILEN_REDIRECTIONS(FROM_USER, FROM_DOMAIN, TO_EMAIL) VALUES ('l2', 's1.example.com', 'any@s1.example.com')");

        // Test nothing
        execute(Arrays.asList(), Arrays.asList(), mailet, Arrays.asList());
        // Test not handled
        execute(Arrays.asList("dontcare@out.example.com"), Arrays.asList("dontcare@out.example.com"), mailet, //
                Arrays.asList());
        // Test simple redirection
        execute(Arrays.asList("r1@s1.example.com"), Arrays.asList("a1@s1.example.com", "a2@s1.example.com", "outside@out.example.com"), mailet, //
                Arrays.asList("a1@s1.example.com", "a2@s1.example.com", "outside@out.example.com"));
        // Test outside and r2
        execute(Arrays.asList("r2@s1.example.com", "outside@out.example.com"), Arrays.asList("a3@s1.example.com", "outside@out.example.com"), mailet, Arrays.asList("a3@s1.example.com"));
        // Test local account
        execute(Arrays.asList("a1@s1.example.com"), Arrays.asList("a1@s1.example.com"), mailet, Arrays.asList());
        // Test catch-all
        execute(Arrays.asList("blah@s1.example.com"), Arrays.asList("catchall1@example.com", "catchall2@example.com"), mailet, //
                Arrays.asList("catchall1@example.com", "catchall2@example.com"));
        // Test multi-redirection
        execute(Arrays.asList("rm1@s3.example.com"), Arrays.asList("a1@s1.example.com", "a2@s1.example.com", "outside@out.example.com", "a5@s1.example.com"), mailet, //
                Arrays.asList("a1@s1.example.com", "a2@s1.example.com", "outside@out.example.com", "a5@s1.example.com"));
        // Test loop
        execute(Arrays.asList("l1@s1.example.com"), Arrays.asList("a1@s1.example.com", "a2@s1.example.com", "outside@out.example.com", "catchall1@example.com", "catchall2@example.com"), mailet, //
                Arrays.asList("a1@s1.example.com", "a2@s1.example.com", "outside@out.example.com", "catchall1@example.com", "catchall2@example.com"));
        // Test many kinds
        execute(Arrays.asList("dontcare@out.example.com", "a5@s1.example.com", "r2@s1.example.com"), Arrays.asList("dontcare@out.example.com", "a5@s1.example.com", "a3@s1.example.com"), mailet, //
                Arrays.asList("a3@s1.example.com"));
    }

}
