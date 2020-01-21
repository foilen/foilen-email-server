/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.email.server.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.foilen.smalltools.tools.AbstractBasics;

@JsonIgnoreProperties(ignoreUnknown = false)
public class EmailManagerConfigAccount extends AbstractBasics implements Comparable<EmailManagerConfigAccount> {

    private String email;
    private String password;
    private String passwordSha512;

    @Override
    public int compareTo(EmailManagerConfigAccount o) {
        return this.email.compareTo(o.email);
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordSha512() {
        return passwordSha512;
    }

    public EmailManagerConfigAccount setEmail(String email) {
        this.email = email;
        return this;
    }

    public EmailManagerConfigAccount setPassword(String password) {
        this.password = password;
        return this;
    }

    public EmailManagerConfigAccount setPasswordSha512(String passwordSha512) {
        this.passwordSha512 = passwordSha512;
        return this;
    }

}
