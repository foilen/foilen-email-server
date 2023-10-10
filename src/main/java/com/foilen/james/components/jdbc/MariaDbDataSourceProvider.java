/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2023 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.jdbc;

import org.apache.commons.dbcp2.BasicDataSource;

import com.foilen.email.server.exception.EmailServerException;
import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.ExecutorsTools;
import com.foilen.smalltools.tools.ThreadTools;
import com.google.inject.Provider;

public class MariaDbDataSourceProvider extends AbstractBasics implements Provider<BasicDataSource> {

    @Override
    public BasicDataSource get() {
        String hostname = System.getProperty("emailConfig.database.hostname");
        int port = Integer.valueOf(System.getProperty("emailConfig.database.port"));
        String database = System.getProperty("emailConfig.database.database");
        String user = System.getProperty("emailConfig.database.username");
        String password = System.getProperty("emailConfig.database.password");

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mariadb://" + hostname + ":" + port + "/" + database);
        try {
            dataSource.setUsername(user);
            dataSource.setPassword(password);
        } catch (Exception e) {
            throw new EmailServerException("Problem creating the datasource", e);
        }

        // Show pool usage
        ExecutorsTools.getCachedDaemonThreadPool().execute(() -> {

            for (;;) {
                try {
                    logger.info("DataSource: active: {} ; idle: {}", dataSource.getNumActive(), dataSource.getNumIdle());
                } catch (Exception e) {
                    logger.error("Problem outputting the database pool usage", e);
                } finally {
                    ThreadTools.sleep(60000);
                }
            }

        });

        return dataSource;
    }

}
