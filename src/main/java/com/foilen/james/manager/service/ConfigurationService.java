package com.foilen.james.manager.service;

import com.foilen.email.server.config.EmailManagerConfig;
import com.foilen.smalltools.event.EventCallback;

public interface ConfigurationService {

    void addConfigurationUpdateCallback(EventCallback<EmailManagerConfig> callback);

    EmailManagerConfig getConfiguration();

}
