package com.foilen.james.components.jdbc;

import javax.sql.DataSource;

import com.google.inject.AbstractModule;

public class JDBCDataSourceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(DataSource.class).toProvider(MariaDbDataSourceProvider.class);
    }
}
