/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2023 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.jdbc;

import javax.sql.DataSource;

import com.google.inject.AbstractModule;

public class JDBCDataSourceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DataSource.class).toProvider(MariaDbDataSourceProvider.class);
    }
}
