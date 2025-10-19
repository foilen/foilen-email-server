package com.foilen.james.components;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.sql.DataSource;

import org.apache.james.core.MailAddress;

import com.google.common.cache.CacheLoader;

public final class RedirectionCacheLoader extends CacheLoader<MailAddress, List<MailAddress>> {

    private DataSource datasource;

    public RedirectionCacheLoader(DataSource datasource) {
        this.datasource = datasource;
    }

    @Override
    public List<MailAddress> load(MailAddress recipient) throws Exception {
        String fromUser = recipient.getLocalPart();
        String fromDomain = recipient.getDomain().asString();

        try (Connection connection = datasource.getConnection()) {
            PreparedStatement s = connection.prepareStatement("SELECT TO_EMAIL FROM FOILEN_REDIRECTIONS WHERE FROM_USER = ? AND FROM_DOMAIN = ?");
            s.setString(1, fromUser);
            s.setString(2, fromDomain);
            ResultSet rs = s.executeQuery();
            List<MailAddress> toEmails = new ArrayList<>();
            while (rs.next()) {
                toEmails.add(new MailAddress(rs.getString(1)));
            }
            return toEmails;
        } catch (SQLException e) {
            throw new MessagingException("Problem getting the list of redirections", e);
        }
    }
}