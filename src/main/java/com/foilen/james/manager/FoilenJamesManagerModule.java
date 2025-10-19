package com.foilen.james.manager;

import com.foilen.james.manager.service.ConfigurationService;
import com.foilen.james.manager.service.ConfigurationServiceImpl;
import com.foilen.james.manager.service.UpdateJamesService;
import com.google.inject.AbstractModule;

public class FoilenJamesManagerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UpdateJamesService.class).asEagerSingleton();
        bind(ConfigurationService.class).to(ConfigurationServiceImpl.class);
    }
}
