/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.email.server.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class EmailConfig {

    private EmailConfigDatabase database;

    private String postmasterEmail;

    private boolean enableDebugDumpMessagesDetails;
    private boolean disableRelayDeniedNotifyPostmaster;
    private boolean disableBounceNotifyPostmaster;
    private boolean disableBounceNotifySender;

    private String imapCertPemFile;
    private String pop3CertPemFile;
    private String smtpCertPemFile;
    private List<EmailConfigDomainAndRelay> domainAndRelais = new ArrayList<>();

    public EmailConfigDatabase getDatabase() {
        return database;
    }

    public List<EmailConfigDomainAndRelay> getDomainAndRelais() {
        return domainAndRelais;
    }

    public String getImapCertPemFile() {
        return imapCertPemFile;
    }

    public String getPop3CertPemFile() {
        return pop3CertPemFile;
    }

    public String getPostmasterEmail() {
        return postmasterEmail;
    }

    public String getSmtpCertPemFile() {
        return smtpCertPemFile;
    }

    public boolean isDisableBounceNotifyPostmaster() {
        return disableBounceNotifyPostmaster;
    }

    public boolean isDisableBounceNotifySender() {
        return disableBounceNotifySender;
    }

    public boolean isDisableRelayDeniedNotifyPostmaster() {
        return disableRelayDeniedNotifyPostmaster;
    }

    public boolean isEnableDebugDumpMessagesDetails() {
        return enableDebugDumpMessagesDetails;
    }

    public void setDatabase(EmailConfigDatabase database) {
        this.database = database;
    }

    public void setDisableBounceNotifyPostmaster(boolean disableBounceNotifyPostmaster) {
        this.disableBounceNotifyPostmaster = disableBounceNotifyPostmaster;
    }

    public void setDisableBounceNotifySender(boolean disableBounceNotifySender) {
        this.disableBounceNotifySender = disableBounceNotifySender;
    }

    public void setDisableRelayDeniedNotifyPostmaster(boolean disableRelayDeniedNotifyPostmaster) {
        this.disableRelayDeniedNotifyPostmaster = disableRelayDeniedNotifyPostmaster;
    }

    public void setDomainAndRelais(List<EmailConfigDomainAndRelay> domainAndRelais) {
        this.domainAndRelais = domainAndRelais;
    }

    public void setEnableDebugDumpMessagesDetails(boolean enableDebugDumpMessagesDetails) {
        this.enableDebugDumpMessagesDetails = enableDebugDumpMessagesDetails;
    }

    public void setImapCertPemFile(String imapCertPemFile) {
        this.imapCertPemFile = imapCertPemFile;
    }

    public void setPop3CertPemFile(String pop3CertPemFile) {
        this.pop3CertPemFile = pop3CertPemFile;
    }

    public void setPostmasterEmail(String postmasterEmail) {
        this.postmasterEmail = postmasterEmail;
    }

    public void setSmtpCertPemFile(String smtpCertPemFile) {
        this.smtpCertPemFile = smtpCertPemFile;
    }

}
