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

    public EmailConfigDomainAndRelay setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public EmailConfigDomainAndRelay setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public EmailConfigDomainAndRelay setPassword(String password) {
        this.password = password;
        return this;
    }

    public EmailConfigDomainAndRelay setPort(int port) {
        this.port = port;
        return this;
    }

    public EmailConfigDomainAndRelay setUsername(String username) {
        this.username = username;
        return this;
    }
}
