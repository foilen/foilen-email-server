package com.foilen.email.server.config.utils;

import com.google.inject.AbstractModule;

public class FoilenUtilsModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AutoKillProcessOutOfMemory.class).asEagerSingleton();
    }
}
