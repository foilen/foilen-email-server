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

    private long maxMessageSizeInKb = 75000; // 75 MB

    public EmailConfigDatabase getDatabase() {
        return database;
    }

    public List<EmailConfigDomainAndRelay> getDomainAndRelais() {
        return domainAndRelais;
    }

    public String getImapCertPemFile() {
        return imapCertPemFile;
    }

    public long getMaxMessageSizeInKb() {
        return maxMessageSizeInKb;
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

    public EmailConfig setDatabase(EmailConfigDatabase database) {
        this.database = database;
        return this;
    }

    public EmailConfig setDisableBounceNotifyPostmaster(boolean disableBounceNotifyPostmaster) {
        this.disableBounceNotifyPostmaster = disableBounceNotifyPostmaster;
        return this;
    }

    public EmailConfig setDisableBounceNotifySender(boolean disableBounceNotifySender) {
        this.disableBounceNotifySender = disableBounceNotifySender;
        return this;
    }

    public EmailConfig setDisableRelayDeniedNotifyPostmaster(boolean disableRelayDeniedNotifyPostmaster) {
        this.disableRelayDeniedNotifyPostmaster = disableRelayDeniedNotifyPostmaster;
        return this;
    }

    public EmailConfig setDomainAndRelais(List<EmailConfigDomainAndRelay> domainAndRelais) {
        this.domainAndRelais = domainAndRelais;
        return this;
    }

    public EmailConfig setEnableDebugDumpMessagesDetails(boolean enableDebugDumpMessagesDetails) {
        this.enableDebugDumpMessagesDetails = enableDebugDumpMessagesDetails;
        return this;
    }

    public EmailConfig setImapCertPemFile(String imapCertPemFile) {
        this.imapCertPemFile = imapCertPemFile;
        return this;
    }

    public void setMaxMessageSizeInKb(long maxMessageSizeInKb) {
        this.maxMessageSizeInKb = maxMessageSizeInKb;
    }

    public EmailConfig setPop3CertPemFile(String pop3CertPemFile) {
        this.pop3CertPemFile = pop3CertPemFile;
        return this;
    }

    public EmailConfig setPostmasterEmail(String postmasterEmail) {
        this.postmasterEmail = postmasterEmail;
        return this;
    }

    public EmailConfig setSmtpCertPemFile(String smtpCertPemFile) {
        this.smtpCertPemFile = smtpCertPemFile;
        return this;
    }

}
