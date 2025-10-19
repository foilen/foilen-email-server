package com.foilen.james.components.stub;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.james.core.Domain;
import org.apache.james.core.MailAddress;
import org.apache.mailet.HostAddress;
import org.apache.mailet.LookupException;
import org.apache.mailet.Mail;
import org.apache.mailet.MailetContext;
import org.apache.mailet.TemporaryLookupException;
import org.slf4j.Logger;

@SuppressWarnings("deprecation")
public class MailetContextStub implements MailetContext {

    private Collection<MailAddress> localAccounts;

    public MailetContextStub(Collection<MailAddress> localAccounts) {
        this.localAccounts = localAccounts;
    }

    @Override
    public void bounce(Mail mail, String message) throws MessagingException {
    }

    @Override
    public void bounce(Mail mail, String message, MailAddress bouncer) throws MessagingException {
    }

    @Override
    public List<String> dnsLookup(String name, RecordType type) throws TemporaryLookupException, LookupException {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public Iterator<String> getAttributeNames() {
        return null;
    }

    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public Collection<String> getMailServers(Domain domain) {
        return null;
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public MailAddress getPostmaster() {
        return null;
    }

    @Override
    public String getServerInfo() {
        return null;
    }

    @Override
    public Iterator<HostAddress> getSMTPHostAddresses(Domain domain) {
        return null;
    }

    @Override
    public boolean isLocalEmail(MailAddress mailAddress) {
        return localAccounts.contains(mailAddress);
    }

    @Override
    public boolean isLocalServer(Domain domain) {
        return false;
    }

    @Override
    public boolean isLocalUser(String userAccount) {
        return false;
    }

    @Override
    public void log(LogLevel level, String message) {
    }

    @Override
    public void log(LogLevel level, String message, Throwable t) {
    }

    @Override
    public void log(String message) {
    }

    @Override
    public void log(String message, Throwable t) {
    }

    @Override
    public void removeAttribute(String name) {
    }

    @Override
    public void sendMail(Mail mail) throws MessagingException {
    }

    @Override
    public void sendMail(Mail mail, long delay, TimeUnit unit) throws MessagingException {
    }

    @Override
    public void sendMail(Mail mail, String state) throws MessagingException {
    }

    @Override
    public void sendMail(Mail mail, String state, long delay, TimeUnit unit) throws MessagingException {
    }

    @Override
    public void sendMail(MailAddress sender, Collection<MailAddress> recipients, MimeMessage message) throws MessagingException {
    }

    @Override
    public void sendMail(MailAddress sender, Collection<MailAddress> recipients, MimeMessage message, String state) throws MessagingException {
    }

    @Override
    public void sendMail(MimeMessage message) throws MessagingException {
    }

    @Override
    public void setAttribute(String name, Object value) {
    }

}
