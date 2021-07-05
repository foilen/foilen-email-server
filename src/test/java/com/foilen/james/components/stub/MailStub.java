/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.components.stub;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.james.core.MailAddress;
import org.apache.mailet.Attribute;
import org.apache.mailet.AttributeName;
import org.apache.mailet.AttributeValue;
import org.apache.mailet.Mail;
import org.apache.mailet.PerRecipientHeaders;
import org.apache.mailet.PerRecipientHeaders.Header;

import com.google.common.base.Preconditions;

public class MailStub implements Mail {

    private static final long serialVersionUID = 1L;

    private String name = "AAA";

    private Map<AttributeName, Attribute> attributes = new HashMap<>();
    private List<MailAddress> recipients = new ArrayList<>();
    private PerRecipientHeaders perRecipientHeaders = new PerRecipientHeaders();
    private MimeMessage message = new MimeMessage((Session) null);

    @Override
    public void addSpecificHeaderForRecipient(Header header, MailAddress recipient) {
        perRecipientHeaders.addHeaderForRecipient(header, recipient);
    }

    @Override
    public Stream<AttributeName> attributeNames() {
        return attributes.keySet().stream();
    }

    @Override
    public Stream<Attribute> attributes() {
        return attributes.values().stream();
    }

    @Override
    public Mail duplicate() throws MessagingException {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public Optional<Attribute> getAttribute(AttributeName name) {
        return Optional.ofNullable(attributes.get(name));
    }

    @Override
    public Serializable getAttribute(String name) {
        return toSerializable(attributes.get(AttributeName.of(name)));
    }

    @Override
    public Iterator<String> getAttributeNames() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public String getErrorMessage() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public Date getLastUpdated() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public MimeMessage getMessage() throws MessagingException {
        return message;
    }

    @Override
    public long getMessageSize() throws MessagingException {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PerRecipientHeaders getPerRecipientSpecificHeaders() {
        return perRecipientHeaders;
    }

    @Override
    public Collection<MailAddress> getRecipients() {
        return recipients;
    }

    @Override
    public String getRemoteAddr() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public String getRemoteHost() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public MailAddress getSender() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public String getState() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public boolean hasAttributes() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public void removeAllAttributes() {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public Optional<Attribute> removeAttribute(AttributeName attributeName) {
        Attribute previous = attributes.remove(attributeName);
        return Optional.ofNullable(previous);
    }

    @Override
    public Serializable removeAttribute(String name) {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public Optional<Attribute> setAttribute(Attribute attribute) {
        Preconditions.checkNotNull(attribute.getName().asString(), "AttributeName should not be null");
        return Optional.ofNullable(this.attributes.put(attribute.getName(), attribute));
    }

    @Override
    public Serializable setAttribute(String name, Serializable object) {
        Preconditions.checkNotNull(name, "Key of an attribute should not be null");
        Attribute attribute = Attribute.convertToAttribute(name, object);
        Attribute previous = attributes.put(attribute.getName(), attribute);

        return toSerializable(previous);
    }

    @Override
    public void setErrorMessage(String msg) {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public void setLastUpdated(Date lastUpdated) {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public void setMessage(MimeMessage message) {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public void setName(String newName) {
        throw new IllegalAccessError("Not Implemented");
    }

    @Override
    public void setRecipients(Collection<MailAddress> recipients) {
        this.recipients = new ArrayList<>(recipients);
    }

    @Override
    public void setState(String state) {
        throw new IllegalAccessError("Not Implemented");
    }

    private Serializable toSerializable(Attribute previous) {
        return (Serializable) Optional.ofNullable(previous).map(Attribute::getValue).map(AttributeValue::getValue).orElse(null);
    }

}
