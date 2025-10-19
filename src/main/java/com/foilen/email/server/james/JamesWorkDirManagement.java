package com.foilen.email.server.james;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.foilen.email.server.config.EmailConfig;
import com.foilen.email.server.config.EmailManagerConfig;
import com.foilen.email.server.exception.EmailServerException;
import com.foilen.smalltools.crypt.bouncycastle.asymmetric.AsymmetricKeys;
import com.foilen.smalltools.crypt.bouncycastle.asymmetric.RSACrypt;
import com.foilen.smalltools.crypt.bouncycastle.cert.CertificateDetails;
import com.foilen.smalltools.crypt.bouncycastle.cert.RSACertificate;
import com.foilen.smalltools.crypt.bouncycastle.cert.RSATools;
import com.foilen.smalltools.tools.AbstractBasics;
import com.foilen.smalltools.tools.AssertTools;
import com.foilen.smalltools.tools.DirectoryTools;
import com.foilen.smalltools.tools.FileTools;
import com.foilen.smalltools.tools.FreemarkerTools;
import com.foilen.smalltools.tools.JsonTools;
import com.foilen.smalltools.tools.ResourceTools;

public class JamesWorkDirManagement extends AbstractBasics {

    private void copyFile(String configDirectory, String fileName) {
        String absoluteFileName = configDirectory + fileName;
        logger.info("Copying to file {}", absoluteFileName);
        InputStream content = ResourceTools.getResourceAsStream("conf/" + fileName, getClass());
        FileTools.writeFile(content, new File(absoluteFileName));
    }

    private void copyFileWithTemplate(String configDirectory, String fileName, Map<String, Object> model) {
        String absoluteFileName = configDirectory + fileName;
        String destinationFileName = absoluteFileName.substring(0, absoluteFileName.length() - 4);
        logger.info("Templating and copying to file {}", destinationFileName);
        String content = FreemarkerTools.processTemplate("/com/foilen/email/server/james/conf/" + fileName, model);
        FileTools.writeFile(content, destinationFileName);
    }

    private void createKeystore(RSACertificate rsaCertificate, String keystoreFile) {

        try {

            char[] password = new char[] { 'j', 'a', 'm', 'e', 's' };
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(null, password);

            String alias = "james";
            Certificate certificate = rsaCertificate.getCertificate();
            keyStore.setCertificateEntry(alias, certificate);
            Key key = RSATools.createPrivateKey(rsaCertificate.getKeysForSigning());
            keyStore.setKeyEntry(alias, key, password, new Certificate[] { certificate });

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            keyStore.store(outStream, password);
            byte[] byteArray = outStream.toByteArray();
            FileTools.writeFile(byteArray, keystoreFile);
        } catch (Exception e) {
            throw new EmailServerException("Could not create the keystore", e);
        }

    }

    /**
     * Generate the James config files.
     *
     * @param emailConfig
     *            configuration
     * @param managerConfigFile
     *            the json file that contains the manager dynamic configuration
     * @param jamesConfigDirectory
     *            the directory where all the config files are generated
     */
    public void generateConfiguration(EmailConfig emailConfig, String managerConfigFile, String jamesConfigDirectory) {

        DirectoryTools.deleteFolder(jamesConfigDirectory);
        AssertTools.assertTrue(DirectoryTools.createPath(jamesConfigDirectory), "Could not create the james config directory: " + jamesConfigDirectory);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("configDirectory", jamesConfigDirectory);
        model.put("emailConfig", emailConfig);

        // Get the list of domains (if file exists)
        List<String> domainNames = Collections.singletonList("localhost");
        try {
            domainNames = JsonTools.readFromFile(managerConfigFile, EmailManagerConfig.class).getDomains();
        } catch (Exception e) {
            logger.warn("Could not load managerConfigFile {} . Error: {}", managerConfigFile, e.getMessage());
        }
        logger.info("Domain names: {}", domainNames);

        copyFile(jamesConfigDirectory, "dnsservice.xml");
        copyFile(jamesConfigDirectory, "domainlist.xml");
        copyFileWithTemplate(jamesConfigDirectory, "imapserver.xml.ftl", model);
        copyFileWithTemplate(jamesConfigDirectory, "james-database.properties.ftl", model);
        genKeystoreFromPemOrSelfSignedDomains(emailConfig.getImapCertPemFile(), jamesConfigDirectory + "keystore-imaps", domainNames);
        genKeystoreFromPemOrSelfSignedDomains(emailConfig.getPop3CertPemFile(), jamesConfigDirectory + "keystore-pop3s", domainNames);
        genKeystoreFromPemOrSelfSignedDomains(emailConfig.getSmtpCertPemFile(), jamesConfigDirectory + "keystore-smtps", domainNames);
        copyFile(jamesConfigDirectory, "listeners.xml");
        copyFileWithTemplate(jamesConfigDirectory, "mailetcontainer.xml.ftl", model);
        copyFile(jamesConfigDirectory, "mailrepositorystore.xml");
        copyFileWithTemplate(jamesConfigDirectory, "pop3server.xml.ftl", model);
        copyFile(jamesConfigDirectory, "recipientrewritetable.xml");
        copyFileWithTemplate(jamesConfigDirectory, "smtpserver.xml.ftl", model);
        copyFile(jamesConfigDirectory, "sqlResources.xml");
        copyFile(jamesConfigDirectory, "usersrepository.xml");
    }

    private void genKeystoreFromPemOrSelfSignedDomains(String certPemFile, String keystoreFile, List<String> domainNames) {

        RSACertificate rsaCertificate;

        if (certPemFile == null) {
            // Self-signed
            logger.info("Generating to file {} with self-signed", keystoreFile);
            AsymmetricKeys keys = RSACrypt.RSA_CRYPT.generateKeyPair(4096);
            rsaCertificate = new RSACertificate(keys);

            String defaultDomainName = "localhost";
            if (!domainNames.isEmpty()) {
                defaultDomainName = domainNames.get(0);
            }

            CertificateDetails certificateDetails = new CertificateDetails().setCommonName(defaultDomainName);
            domainNames.forEach(domainName -> certificateDetails.addSanDns(domainName));

            rsaCertificate.selfSign(certificateDetails);
        } else {
            // Read PEM
            logger.info("Generating to file {} from PEM {}", keystoreFile, certPemFile);
            if (FileTools.exists(certPemFile)) {
                rsaCertificate = RSACertificate.loadPemFromFile(certPemFile);
            } else {
                throw new EmailServerException("PEM file " + certPemFile + " does not exist");
            }
        }

        // Save keystore
        createKeystore(rsaCertificate, keystoreFile);

    }

}
