/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.email.server.config;

import com.foilen.smalltools.tools.AbstractBasics;

public class EmailConfigDatabase extends AbstractBasics {

    private String hostname;
    private int port = 3306;
    private String database;
    private String username;
    private String password;

    public String getDatabase() {
        return database;
    }

    public String getHostname() {
        return hostname;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public EmailConfigDatabase setDatabase(String database) {
        this.database = database;
        return this;
    }

    public EmailConfigDatabase setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public EmailConfigDatabase setPassword(String password) {
        this.password = password;
        return this;
    }

    public EmailConfigDatabase setPort(int port) {
        this.port = port;
        return this;
    }

    public EmailConfigDatabase setUsername(String username) {
        this.username = username;
        return this;
    }

}
