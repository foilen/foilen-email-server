/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2021 Foilen (https://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.email.server.james;

import java.io.File;

import org.junit.Test;

import com.foilen.email.server.config.EmailConfig;
import com.foilen.email.server.config.EmailConfigDatabase;
import com.foilen.email.server.config.EmailConfigDomainAndRelay;
import com.foilen.email.server.config.EmailManagerConfig;
import com.foilen.smalltools.tools.JsonTools;
import com.google.common.io.Files;

public class JamesWorkDirManagementTest {

    @Test
    public void testGenerateConfiguration() throws Exception {

        // Prepare
        JamesWorkDirManagement jamesWorkDirManagement = new JamesWorkDirManagement();

        EmailConfig emailConfig = new EmailConfig();
        emailConfig.setDatabase(new EmailConfigDatabase() //
                .setHostname("127.0.0.1") //
                .setDatabase("james") //
                .setUsername("james") //
                .setPassword("ABC") //
        );
        emailConfig.setPostmasterEmail("admin@example.com");
        emailConfig.getDomainAndRelais().add(new EmailConfigDomainAndRelay() //
                .setDomain("example.com") //
                .setHostname("relay.example.com") //
                .setPort(25) //
                .setUsername("relayUser") //
                .setPassword("relayPass") //
        );

        String managerConfigFile = File.createTempFile("manager", "json").getAbsolutePath();
        EmailManagerConfig emailManagerConfig = new EmailManagerConfig();
        emailManagerConfig.getDomains().add("example.com");
        JsonTools.writeToFile(managerConfigFile, emailManagerConfig);

        String jamesConfigDirectory = Files.createTempDir().getAbsolutePath();

        // Execute
        jamesWorkDirManagement.generateConfiguration(emailConfig, managerConfigFile, jamesConfigDirectory);
    }

}
