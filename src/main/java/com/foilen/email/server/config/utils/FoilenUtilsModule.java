/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2023 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.email.server.config.utils;

import com.google.inject.AbstractModule;

public class FoilenUtilsModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AutoKillProcessOutOfMemory.class).asEagerSingleton();
    }
}
