/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.jdbc;

import org.mariadb.jdbc.MariaDbPoolDataSource;

import com.foilen.email.server.exception.EmailServerException;
import com.google.inject.Provider;

public class MariaDbPoolDataSourceProvider implements Provider<MariaDbPoolDataSource> {

    @Override
    public MariaDbPoolDataSource get() {
        String hostname = System.getProperty("emailConfig.database.hostname");
        int port = Integer.valueOf(System.getProperty("emailConfig.database.port"));
        String database = System.getProperty("emailConfig.database.database");
        String user = System.getProperty("emailConfig.database.username");
        String password = System.getProperty("emailConfig.database.password");
        MariaDbPoolDataSource dataSource = new MariaDbPoolDataSource(hostname, port, database);
        try {
            dataSource.setUser(user);
            dataSource.setPassword(password);
        } catch (Exception e) {
            throw new EmailServerException("Problem creating the datasource", e);
        }
        return dataSource;
    }

}
