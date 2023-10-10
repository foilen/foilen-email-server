/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2023 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.manager.service;

import com.foilen.email.server.config.EmailManagerConfig;
import com.foilen.smalltools.event.EventCallback;

public interface ConfigurationService {

    void addConfigurationUpdateCallback(EventCallback<EmailManagerConfig> callback);

    EmailManagerConfig getConfiguration();

}
