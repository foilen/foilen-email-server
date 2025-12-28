package com.foilen.email.server.config.utils;

import org.apache.james.mailbox.extractor.TextExtractor;
import org.apache.james.mailbox.store.extractor.DefaultTextExtractor;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * Module to provide TextExtractor binding for Lucene search.
 */
public class TextExtractorModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TextExtractor.class).to(DefaultTextExtractor.class).in(Scopes.SINGLETON);
    }
}
