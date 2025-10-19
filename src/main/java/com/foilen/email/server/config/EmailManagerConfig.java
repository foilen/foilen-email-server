package com.foilen.email.server.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class EmailManagerConfig {

    private List<String> domains = new ArrayList<>();
    private List<EmailManagerConfigAccount> accounts = new ArrayList<>();
    private List<EmailManagerConfigRedirection> redirections = new ArrayList<>();

    public List<EmailManagerConfigAccount> getAccounts() {
        return accounts;
    }

    public List<String> getDomains() {
        return domains;
    }

    public List<EmailManagerConfigRedirection> getRedirections() {
        return redirections;
    }

    public void setAccounts(List<EmailManagerConfigAccount> accounts) {
        this.accounts = accounts;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public void setRedirections(List<EmailManagerConfigRedirection> redirections) {
        this.redirections = redirections;
    }

}
