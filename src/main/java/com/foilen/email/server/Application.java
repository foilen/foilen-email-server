/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2019 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.email.server;

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.annotation.Nullable;

import org.apache.james.GuiceJamesServer;
import org.apache.james.modules.MailboxModule;
import org.apache.james.modules.activemq.ActiveMQQueueModule;
import org.apache.james.modules.data.JPADataModule;
import org.apache.james.modules.data.SieveJPARepositoryModules;
import org.apache.james.modules.mailbox.DefaultEventModule;
import org.apache.james.modules.mailbox.JPAMailboxModule;
import org.apache.james.modules.mailbox.LuceneSearchMailboxModule;
import org.apache.james.modules.protocols.IMAPServerModule;
import org.apache.james.modules.protocols.LMTPServerModule;
import org.apache.james.modules.protocols.ManageSieveServerModule;
import org.apache.james.modules.protocols.POP3ServerModule;
import org.apache.james.modules.protocols.ProtocolHandlerModule;
import org.apache.james.modules.protocols.SMTPServerModule;
import org.apache.james.modules.server.DataRoutesModules;
import org.apache.james.modules.server.DefaultProcessorsConfigurationProviderModule;
import org.apache.james.modules.server.ElasticSearchMetricReporterModule;
import org.apache.james.modules.server.JMXServerModule;
import org.apache.james.modules.server.MailQueueRoutesModule;
import org.apache.james.modules.server.MailRepositoriesRoutesModule;
import org.apache.james.modules.server.MailboxRoutesModule;
import org.apache.james.modules.server.NoJwtModule;
import org.apache.james.modules.server.RawPostDequeueDecoratorModule;
import org.apache.james.modules.server.ReIndexingModule;
import org.apache.james.modules.server.SieveRoutesModule;
import org.apache.james.modules.server.SwaggerRoutesModule;
import org.apache.james.modules.server.WebAdminServerModule;
import org.apache.james.modules.spamassassin.SpamAssassinListenerModule;
import org.apache.james.server.core.configuration.Configuration;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.foilen.email.server.config.EmailConfig;
import com.foilen.email.server.config.EmailConfigDatabase;
import com.foilen.email.server.config.EmailManagerConfig;
import com.foilen.email.server.config.EmailManagerConfigAccount;
import com.foilen.email.server.config.EmailManagerConfigRedirection;
import com.foilen.email.server.exception.EmailServerException;
import com.foilen.email.server.james.JamesWorkDirManagement;
import com.foilen.james.components.jdbc.JDBCDataSourceModule;
import com.foilen.james.manager.FoilenJamesManagerModule;
import com.foilen.smalltools.JavaEnvironmentValues;
import com.foilen.smalltools.reflection.ReflectionTools;
import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.AssertTools;
import com.foilen.smalltools.tools.DirectoryTools;
import com.foilen.smalltools.tools.FileTools;
import com.foilen.smalltools.tools.JsonTools;
import com.foilen.smalltools.tools.LogbackTools;
import com.google.inject.Module;
import com.google.inject.util.Modules;

// TODO Cleanup - Remove any extra modules
public class Application extends AbstractBasics {

    public static final Module WEBADMIN = Modules.combine( //
            new WebAdminServerModule(), //
            new DataRoutesModules(), //
            new MailboxRoutesModule(), //
            new MailQueueRoutesModule(), //
            new MailRepositoriesRoutesModule(), //
            new SwaggerRoutesModule(), //
            new SieveRoutesModule(), //
            new ReIndexingModule());

    public static final Module PROTOCOLS = Modules.combine( //
            new IMAPServerModule(), //
            new LMTPServerModule(), //
            new ManageSieveServerModule(), //
            new POP3ServerModule(), //
            new ProtocolHandlerModule(), //
            new SMTPServerModule(), //
            WEBADMIN);

    public static final Module JPA_SERVER_MODULE = Modules.combine( //
            new ActiveMQQueueModule(), //
            new DefaultProcessorsConfigurationProviderModule(), //
            new ElasticSearchMetricReporterModule(), //
            new JPADataModule(), //
            new JPAMailboxModule(), //
            new JDBCDataSourceModule(), //
            new MailboxModule(), //
            new LuceneSearchMailboxModule(), //
            new NoJwtModule(), //
            new RawPostDequeueDecoratorModule(), //
            new SieveJPARepositoryModules(), //
            new DefaultEventModule(), //
            new SpamAssassinListenerModule());

    public static final Module JPA_MODULE_AGGREGATE = Modules.combine(JPA_SERVER_MODULE, PROTOCOLS);

    public static void main(String[] args) {
        try {
            new Application().execute(args);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void showUsage() {
        System.out.println("Usage:");
        CmdLineParser cmdLineParser = new CmdLineParser(new Options());
        cmdLineParser.printUsage(System.out);
    }

    private EmailConfig emailConfig;
    private Options options;

    private void configToSystemProperties() {
        configToSystemProperties(options, "options");
        configToSystemProperties(emailConfig.getDatabase(), "database");
    }

    private void configToSystemProperties(Object configObject, String prefix) {
        for (Method method : ReflectionTools.allMethods(configObject.getClass())) {
            String methodName = method.getName();
            if (!methodName.startsWith("get")) {
                continue;
            }
            methodName = methodName.substring(3);
            methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
            Object propertyValue;
            try {
                propertyValue = method.invoke(configObject);
            } catch (Exception e) {
                throw new EmailServerException("Problem getting value", e);
            }
            if (propertyValue == null || propertyValue.toString().isEmpty()) {
                if (ReflectionTools.findAnnotationByFieldNameAndAnnotation(configObject.getClass(), methodName, Nullable.class) == null) {
                    throw new EmailServerException(methodName + " in the config cannot be null or empty");
                }
            } else {
                String systemPropertyName = "emailConfig." + prefix + "." + methodName;
                System.setProperty(systemPropertyName, propertyValue.toString());
                logger.info("Adding to system properties: {}", systemPropertyName);
            }
        }
    }

    private void execute(String[] args) throws Exception {

        // Get the parameters
        options = new Options();
        CmdLineParser cmdLineParser = new CmdLineParser(options);
        try {
            cmdLineParser.parseArgument(args);
        } catch (CmdLineException e) {
            e.printStackTrace();
            showUsage();
            return;
        }

        if (options.isDebug()) {
            System.out.println("Enabling LOGBACK debug");
            LogbackTools.changeConfig("/com/foilen/email/server/logback-debug.xml");
        } else {
            System.out.println("Enabling LOGBACK normal");
            LogbackTools.changeConfig("/com/foilen/email/server/logback.xml");
        }

        logger.info("Current user: {}", JavaEnvironmentValues.getUserName());

        // Load the config file
        if (options.getJamesConfigFile() != null && FileTools.exists(options.getJamesConfigFile())) {
            logger.info("Loading config file {}", options.getJamesConfigFile());
            emailConfig = JsonTools.readFromFile(options.getJamesConfigFile(), EmailConfig.class);
        } else {
            logger.info("The config file {} does not exist. Will create a local in-memory config for testing", options.getJamesConfigFile());
            emailConfig = new EmailConfig();
            emailConfig.setDatabase(new EmailConfigDatabase() //
                    .setHostname("127.0.0.1") //
                    .setDatabase("james") //
                    .setUsername("root")//
                    .setPassword("ABC")//
            );
            emailConfig.setPostmasterEmail("account@localhost.foilen-lab.com");
        }

        EmailConfigDatabase database = emailConfig.getDatabase();
        AssertTools.assertNotNull(database, "Missing 'database' field");
        AssertTools.assertNotNull(database.getHostname(), "Missing 'database.hostname' field");
        AssertTools.assertNotNull(database.getDatabase(), "Missing 'database.database' field");
        AssertTools.assertNotNull(database.getUsername(), "Missing 'database.username' field");
        AssertTools.assertNotNull(database.getPassword(), "Missing 'database.password' field");

        // Create the working directory if missing
        String workingDirectory = options.getWorkDir();
        logger.info("Current working directory: {}", workingDirectory);
        DirectoryTools.createPath(workingDirectory);

        // Create a default manager config file if none specified
        if (options.getManagerConfigFile() == null) {
            logger.info("No --managerConfigFile specified. Will create a test one");
            options.setManagerConfigFile(workingDirectory + "/email-manager-config.json");
            EmailManagerConfig emailManagerConfig = new EmailManagerConfig();
            emailManagerConfig.getDomains().add("localhost.foilen-lab.com");
            emailManagerConfig.getAccounts().add(new EmailManagerConfigAccount() //
                    .setEmail("account@localhost.foilen-lab.com") //
                    .setPassword("qwerty"));
            logger.info("Account account@localhost.foilen-lab.com with password 'qwerty' will receive all the emails sent to any account");
            emailManagerConfig.getRedirections().add(new EmailManagerConfigRedirection() //
                    .setEmail("*@localhost.foilen-lab.com") //
                    .setRedirectTos(Arrays.asList("account@localhost.foilen-lab.com")));

            JsonTools.writeToFile(options.getManagerConfigFile(), emailManagerConfig);
        }

        // Export all config as properties
        configToSystemProperties();

        // Process all the James configuration
        String jamesConfigDirectory = workingDirectory + "/conf/";
        JamesWorkDirManagement jamesWorkDirManagement = new JamesWorkDirManagement();
        jamesWorkDirManagement.generateConfiguration(emailConfig, options.getManagerConfigFile(), jamesConfigDirectory);

        Configuration configuration = Configuration.builder() //
                .configurationPath("file://" + jamesConfigDirectory) //
                .workingDirectory(workingDirectory) //
                .build();
        GuiceJamesServer server = GuiceJamesServer //
                .forConfiguration(configuration) //
                .combineWith(JPA_MODULE_AGGREGATE, //
                        new JMXServerModule(), //
                        new FoilenJamesManagerModule() //
                );

        server.start();

    }

    public EmailConfig getEmailConfig() {
        return emailConfig;
    }

    public Options getOptions() {
        return options;
    }

}
