/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.stub;

import java.util.Collection;
import java.util.Iterator;

import org.apache.james.core.MailAddress;
import org.apache.mailet.MailetConfig;
import org.apache.mailet.MailetContext;

public class MailetConfigStub implements MailetConfig {

    private MailetContext mailetContext;

    public MailetConfigStub(Collection<MailAddress> localAccounts) {
        mailetContext = new MailetContextStub(localAccounts);
    }

    @Override
    public String getInitParameter(String name) {
        return null;
    }

    @Override
    public Iterator<String> getInitParameterNames() {
        return null;
    }

    @Override
    public MailetContext getMailetContext() {
        return mailetContext;
    }

    @Override
    public String getMailetName() {
        return null;
    }

}
