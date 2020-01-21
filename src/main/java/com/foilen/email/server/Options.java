/*
    Email Server
    https://github.com/foilen/foilen-email-server
    Copyright (c) 2019-2020 Foilen (http://foilen.com)

    The MIT License
    http://opensource.org/licenses/MIT

 */
package com.foilen.email.server;

import javax.annotation.Nullable;

import org.kohsuke.args4j.Option;

import com.foilen.smalltools.JavaEnvironmentValues;

/**
 * The arguments to pass to the application.
 */
public class Options {

    @Option(name = "--debug", usage = "To log everything (default: false)")
    private boolean debug;

    @Option(name = "--workDir", usage = "The directory where temporary files are placed (default: subfolder './_workdir'")
    private String workDir = JavaEnvironmentValues.getWorkingDirectory() + "/_workdir";

    @Nullable
    @Option(name = "--jamesConfigFile", usage = "The config file path for the email server")
    private String jamesConfigFile;
    @Option(name = "--managerConfigFile", usage = "The config file path for the manager service")
    private String managerConfigFile;

    public String getJamesConfigFile() {
        return jamesConfigFile;
    }

    public String getManagerConfigFile() {
        return managerConfigFile;
    }

    public String getWorkDir() {
        return workDir;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setJamesConfigFile(String jamesConfigFile) {
        this.jamesConfigFile = jamesConfigFile;
    }

    public void setManagerConfigFile(String managerConfigFile) {
        this.managerConfigFile = managerConfigFile;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

}
