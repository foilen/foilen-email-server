/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.email.server.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class EmailConfigDomainAndRelay {

    private String domain;
    private String hostname;
    private int port;
    private String username;
    private String password;

    public String getDomain() {
        return domain;
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

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
