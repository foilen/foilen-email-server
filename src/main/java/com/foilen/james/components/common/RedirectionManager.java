/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.common;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.sql.DataSource;

import org.apache.james.core.MailAddress;

import com.foilen.james.components.RedirectionCacheLoader;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public class RedirectionManager {

    private static final String CATCH_ALL_FROM_USER = "*";

    private static LoadingCache<MailAddress, List<MailAddress>> redirectionsByRecipient;

    public static List<MailAddress> getCatchAllRedirections(MailAddress recipient) throws MessagingException {
        try {
            return redirectionsByRecipient.get(new MailAddress(RedirectionManager.CATCH_ALL_FROM_USER, recipient.getDomain()));
        } catch (ExecutionException e) {
            throw new MessagingException("Cannot load the list", e);
        }
    }

    public static List<MailAddress> getRedirections(MailAddress recipient) throws MessagingException {
        try {
            return redirectionsByRecipient.get(recipient);
        } catch (ExecutionException e) {
            throw new MessagingException("Cannot load the list", e);
        }
    }

    public static void initCache(long cacheMaxTimeInSeconds, long cacheMaxEntries, DataSource datasource) {
        redirectionsByRecipient = CacheBuilder.newBuilder() //
                .expireAfterWrite(cacheMaxTimeInSeconds, TimeUnit.SECONDS) //
                .maximumSize(cacheMaxEntries) //
                .build(new RedirectionCacheLoader(datasource));
    }

}
