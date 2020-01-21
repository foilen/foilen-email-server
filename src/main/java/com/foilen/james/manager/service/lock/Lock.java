/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.manager.service.lock;

import java.util.Date;

import com.foilen.smalltools.tools.AbstractBasics;

public class Lock extends AbstractBasics {

    private String name;
    private String requestorId;
    private Date until;

    public Lock(String name, String requestorId, Date until) {
        this.name = name;
        this.requestorId = requestorId;
        this.until = until;
    }

    public String getName() {
        return name;
    }

    public String getRequestorId() {
        return requestorId;
    }

    public Date getUntil() {
        return until;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequestorId(String requestorId) {
        this.requestorId = requestorId;
    }

    public void setUntil(Date until) {
        this.until = until;
    }

}
