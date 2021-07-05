/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.manager.service;

import com.foilen.smalltools.tools.AbstractBasics;
import com.google.common.collect.ComparisonChain;

public class EmailRedirectionParts extends AbstractBasics implements Comparable<EmailRedirectionParts> {

    private String fromUser;
    private String fromDomain;
    private String toEmail;

    @Override
    public int compareTo(EmailRedirectionParts o) {
        return ComparisonChain.start() //
                .compare(this.fromUser, o.fromUser) //
                .compare(this.fromDomain, o.fromDomain) //
                .compare(this.toEmail, o.toEmail) //
                .result();
    }

    public String getFromDomain() {
        return fromDomain;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getToEmail() {
        return toEmail;
    }

    public EmailRedirectionParts setFromDomain(String fromDomain) {
        this.fromDomain = fromDomain;
        return this;
    }

    public EmailRedirectionParts setFromUser(String fromUser) {
        this.fromUser = fromUser;
        return this;
    }

    public EmailRedirectionParts setToEmail(String toEmail) {
        this.toEmail = toEmail;
        return this;
    }

}
