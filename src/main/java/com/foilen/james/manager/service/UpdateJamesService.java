/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.manager.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.foilen.email.server.config.EmailManagerConfig;
import com.foilen.email.server.config.EmailManagerConfigAccount;
import com.foilen.email.server.config.EmailManagerConfigRedirection;
import com.foilen.james.manager.RetryException;
import com.foilen.james.manager.service.lock.LockService;
import com.foilen.james.manager.service.lock.LockServiceImpl;
import com.foilen.smalltools.event.EventCallback;
import com.foilen.smalltools.hash.HashSha512;
import com.foilen.smalltools.listscomparator.ListComparatorHandler;
import com.foilen.smalltools.listscomparator.ListsComparator;
import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.StringTools;

public class UpdateJamesService extends AbstractBasics implements EventCallback<EmailManagerConfig> {

    private DataSource dataSource;

    @Inject
    public UpdateJamesService(ConfigurationService configurationService, DataSource dataSource) {
        this.dataSource = dataSource;

        logger.info("Adding callback");
        configurationService.addConfigurationUpdateCallback(this);
    }

    private void cleanup(JdbcTemplate jdbcTemplate, String table, String column, String pivotTable, String pivotColumn) {
        List<String> existing = jdbcTemplate.queryForList("SELECT DISTINCT " + column + " FROM " + table + " ORDER BY " + column, String.class);
        List<String> desired = jdbcTemplate.queryForList("SELECT DISTINCT " + pivotColumn + " FROM " + pivotTable + " ORDER BY " + pivotColumn, String.class);

        logger.info("[{}] Got {} existing and {} desired", table, existing.size(), desired.size());

        ListsComparator.compareLists(existing, desired, new ListComparatorHandler<String, String>() {

            @Override
            public void both(String existing, String desired) {
                logger.info("[{}] Keep {}", table, existing);
            }

            @Override
            public void leftOnly(String existing) {
                logger.info("[{}] Delete {}", table, existing);
                jdbcTemplate.update("DELETE FROM " + table + " WHERE " + column + " = ?", existing);
            }

            @Override
            public void rightOnly(String desired) {
            }
        });
    }

    @Override
    public synchronized void handle(EmailManagerConfig config) {

        if (config == null) {
            logger.error("No configuration to update");
            return;
        }

        try {

            logger.info("[BEGIN] Updating the James configuration");
            // Get the DB
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

            LockService lockService = new LockServiceImpl(jdbcTemplate);
            lockService.init();

            // Execute
            lockService.executeIfGotLock("manage", () -> {

                // Update domains
                {
                    List<String> existing = jdbcTemplate.queryForList("SELECT DOMAIN_NAME FROM JAMES_DOMAIN ORDER BY DOMAIN_NAME", String.class);
                    config.getDomains().add("localhost");
                    List<String> desired = config.getDomains().stream().sorted().distinct().collect(Collectors.toList());
                    logger.info("[DOMAIN] Got {} existing and {} desired", existing.size(), desired.size());

                    ListsComparator.compareLists(existing, desired, new ListComparatorHandler<String, String>() {

                        @Override
                        public void both(String left, String right) {
                            logger.info("[DOMAIN] Keep {}", left);
                        }

                        @Override
                        public void leftOnly(String existing) {
                            logger.info("[DOMAIN] Delete {}", existing);
                            jdbcTemplate.update("DELETE FROM JAMES_DOMAIN WHERE DOMAIN_NAME = ?", existing);
                        }

                        @Override
                        public void rightOnly(String desired) {
                            logger.info("[DOMAIN] Add {}", desired);
                            jdbcTemplate.update("INSERT INTO JAMES_DOMAIN (DOMAIN_NAME) VALUES (?)", desired);
                        }
                    });
                }

                // Update accounts
                config.getAccounts().forEach(account -> {
                    if (account.getPasswordSha512() == null && account.getPassword() != null) {
                        account.setPasswordSha512(HashSha512.hashString(account.getPassword()));
                    }
                });
                {
                    List<EmailManagerConfigAccount> existing = jdbcTemplate.query("SELECT USER_NAME, PASSWORD FROM JAMES_USER ORDER BY USER_NAME", new RowMapper<EmailManagerConfigAccount>() {

                        @Override
                        public EmailManagerConfigAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
                            EmailManagerConfigAccount item = new EmailManagerConfigAccount();
                            item.setEmail(rs.getString(1));
                            item.setPasswordSha512(rs.getString(2));
                            return item;
                        }
                    });
                    List<EmailManagerConfigAccount> desired = config.getAccounts().stream().sorted((a, b) -> a.getEmail().compareTo(b.getEmail())).distinct().collect(Collectors.toList());
                    logger.info("[ACCOUNT] Got {} existing and {} desired", existing.size(), desired.size());

                    ListsComparator.compareLists(existing, desired, new ListComparatorHandler<EmailManagerConfigAccount, EmailManagerConfigAccount>() {

                        @Override
                        public void both(EmailManagerConfigAccount existing, EmailManagerConfigAccount desired) {
                            logger.info("[ACCOUNT] Keep {}", existing.getEmail());

                            if (!StringTools.safeEquals(existing.getPasswordSha512(), desired.getPasswordSha512()) && desired.getPasswordSha512() != null) {
                                logger.info("[ACCOUNT] Update password {}", existing.getEmail());
                                jdbcTemplate.update("UPDATE JAMES_USER SET PASSWORD = ?, VERSION = VERSION + 1 WHERE USER_NAME = ?", desired.getPasswordSha512(), desired.getEmail());
                            }
                        }

                        @Override
                        public void leftOnly(EmailManagerConfigAccount existing) {
                            logger.info("[ACCOUNT] Delete {}", existing.getEmail());
                            jdbcTemplate.update("DELETE FROM JAMES_USER WHERE USER_NAME = ?", existing.getEmail());
                        }

                        @Override
                        public void rightOnly(EmailManagerConfigAccount desired) {
                            logger.info("[ACCOUNT] Add {}", desired.getEmail());
                            jdbcTemplate.update("INSERT INTO JAMES_USER (USER_NAME, PASSWORD, PASSWORD_HASH_ALGORITHM, version) VALUES (?, ?, 'SHA-512', 1)", desired.getEmail(),
                                    desired.getPasswordSha512());
                        }
                    });
                }

                // Update redirections
                {
                    List<EmailRedirectionParts> existing = jdbcTemplate.query("SELECT FROM_USER, FROM_DOMAIN, TO_EMAIL FROM FOILEN_REDIRECTIONS ORDER BY FROM_USER, FROM_DOMAIN, TO_EMAIL",
                            new RowMapper<EmailRedirectionParts>() {
                                @Override
                                public EmailRedirectionParts mapRow(ResultSet rs, int rowNum) throws SQLException {
                                    EmailRedirectionParts item = new EmailRedirectionParts();
                                    item.setFromUser(rs.getString(1));
                                    item.setFromDomain(rs.getString(2));
                                    item.setToEmail(rs.getString(3));
                                    return item;
                                }
                            });
                    List<EmailRedirectionParts> desired = new ArrayList<>();
                    for (EmailManagerConfigRedirection redirection : config.getRedirections()) {
                        String[] fromParts = redirection.getEmail().split("@");
                        for (String to : redirection.getRedirectTos()) {
                            desired.add(new EmailRedirectionParts() //
                                    .setFromUser(fromParts[0]) //
                                    .setFromDomain(fromParts[1]) //
                                    .setToEmail(to));
                        }
                    }
                    desired = desired.stream().sorted().distinct().collect(Collectors.toList());
                    logger.info("[REDIRECTION] Got {} existing and {} desired", existing.size(), desired.size());

                    ListsComparator.compareLists(existing, desired, new ListComparatorHandler<EmailRedirectionParts, EmailRedirectionParts>() {

                        @Override
                        public void both(EmailRedirectionParts existing, EmailRedirectionParts desired) {
                            logger.info("[REDIRECTION] Keep {}", existing);
                        }

                        @Override
                        public void leftOnly(EmailRedirectionParts existing) {
                            logger.info("[REDIRECTION] Delete {}", existing);
                            jdbcTemplate.update("DELETE FROM FOILEN_REDIRECTIONS WHERE FROM_USER = ? AND FROM_DOMAIN = ? AND TO_EMAIL = ?", //
                                    existing.getFromUser(), existing.getFromDomain(), existing.getToEmail());
                        }

                        @Override
                        public void rightOnly(EmailRedirectionParts desired) {
                            logger.info("[REDIRECTION] Add {}", desired);
                            jdbcTemplate.update("INSERT INTO FOILEN_REDIRECTIONS (FROM_USER, FROM_DOMAIN, TO_EMAIL) VALUES (?, ?, ?)", //
                                    desired.getFromUser(), desired.getFromDomain(), desired.getToEmail());
                        }
                    });
                }

                // Cleanup
                cleanup(jdbcTemplate, "FOILEN_REDIRECTIONS", "FROM_DOMAIN", "JAMES_DOMAIN", "DOMAIN_NAME");
                cleanup(jdbcTemplate, "JAMES_MAX_DOMAIN_MESSAGE_COUNT", "DOMAIN", "JAMES_DOMAIN", "DOMAIN_NAME");
                cleanup(jdbcTemplate, "JAMES_MAX_DOMAIN_STORAGE", "DOMAIN", "JAMES_DOMAIN", "DOMAIN_NAME");
                cleanup(jdbcTemplate, "JAMES_RECIPIENT_REWRITE", "DOMAIN_NAME", "JAMES_DOMAIN", "DOMAIN_NAME");

                cleanup(jdbcTemplate, "JAMES_SUBSCRIPTION", "USER_NAME", "JAMES_USER", "USER_NAME");
                cleanup(jdbcTemplate, "JAMES_MAILBOX", "USER_NAME", "JAMES_USER", "USER_NAME");

            });

        } catch (Exception e) {
            logger.error("Problem updating the James configuration in the Database. Will retry later", e);
            throw new RetryException();
        } finally {
            logger.info("[END] Updating the James configuration");
        }

    }

}
