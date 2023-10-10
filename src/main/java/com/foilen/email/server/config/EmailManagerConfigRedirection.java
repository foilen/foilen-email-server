/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2023 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.email.server.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class EmailManagerConfigRedirection {

    private String email;
    private List<String> redirectTos = new ArrayList<>();

    public String getEmail() {
        return email;
    }

    public List<String> getRedirectTos() {
        return redirectTos;
    }

    public EmailManagerConfigRedirection setEmail(String email) {
        this.email = email;
        return this;
    }

    public EmailManagerConfigRedirection setRedirectTos(List<String> redirectTos) {
        this.redirectTos = redirectTos;
        return this;
    }

}
