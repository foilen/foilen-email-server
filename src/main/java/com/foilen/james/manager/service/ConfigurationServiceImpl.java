/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.james.manager.service;

import java.io.File;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import com.foilen.email.server.config.EmailManagerConfig;
import com.foilen.james.manager.RetryException;
import com.foilen.smalltools.event.EventCallback;
import com.foilen.smalltools.event.EventList;
import com.foilen.smalltools.filesystemupdatewatcher.handler.OneFileUpdateNotifyer;
import com.foilen.smalltools.filesystemupdatewatcher.handler.OneFileUpdateNotifyerHandler;
import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.AssertTools;
import com.foilen.smalltools.tools.JsonTools;
import com.foilen.smalltools.tools.ThreadTools;

public class ConfigurationServiceImpl extends AbstractBasics implements ConfigurationService, OneFileUpdateNotifyerHandler, Runnable {

    private String configFile;

    private EventList<EmailManagerConfig> updateEvent = new EventList<>();
    private EmailManagerConfig emailManagerConfig;

    private OneFileUpdateNotifyer notifyer;

    private AtomicBoolean lasttimeFailed = new AtomicBoolean();

    public ConfigurationServiceImpl() {
        configFile = System.getProperty("emailConfig.options.managerConfigFile");
        AssertTools.assertNotNull(configFile, "emailConfig.options.managerConfigFile cannot be null");
        configFile = new File(configFile).getAbsolutePath();
        logger.info("Initializing the watcher for config file {}", configFile);

        Thread thread = new Thread(this, "Retry update");
        thread.setDaemon(false);
        thread.start();

        notifyer = new OneFileUpdateNotifyer(configFile, this);
        notifyer.initAutoUpdateSystem();
    }

    @Override
    public void addConfigurationUpdateCallback(EventCallback<EmailManagerConfig> callback) {
        updateEvent.addCallback(callback);

        if (emailManagerConfig != null) {
            try {
                callback.handle(emailManagerConfig);
            } catch (RetryException e) {
                lasttimeFailed.set(true);
            } catch (Exception e) {
                logger.error("Problem executing the callback", e);
            }
        }
    }

    @Override
    public void fileUpdated(String fileName) {
        // Load
        EmailManagerConfig newConfig;
        try {
            logger.info("Loading {}", configFile);
            newConfig = JsonTools.readFromFile(configFile, EmailManagerConfig.class);
        } catch (Exception e) {
            logger.error("Could not load the config file", e);
            return;
        }

        // Check needed config
        try {
            if (newConfig.getAccounts() == null) {
                newConfig.setAccounts(Collections.emptyList());
            }
            if (newConfig.getDomains() == null) {
                newConfig.setDomains(Collections.emptyList());
            }
            if (newConfig.getRedirections() == null) {
                newConfig.setRedirections(Collections.emptyList());
            }
        } catch (Exception e) {
            logger.error("Problem with the config's file content", e);
            return;
        }

        // Set it
        emailManagerConfig = newConfig;

        // Callbacks
        try {
            updateEvent.dispatch(newConfig);
            lasttimeFailed.set(false);
        } catch (RetryException e) {
            lasttimeFailed.set(true);
        }
    }

    @Override
    public EmailManagerConfig getConfiguration() {
        return emailManagerConfig;
    }

    /**
     * Check if needs retry.
     */
    @Override
    public void run() {

        for (;;) {
            ThreadTools.sleep(13000);
            if (lasttimeFailed.get()) {
                notifyer.modified(new File(configFile));
            }
        }

    }

}
