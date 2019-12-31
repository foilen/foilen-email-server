<?xml version="1.0"?>

<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
 -->

<smtpservers>
    <smtpserver enabled="true">
        <jmxName>smtpserver-global</jmxName>
        <bind>0.0.0.0:10025</bind>
        <connectionBacklog>200</connectionBacklog>
        <tls socketTLS="false" startTLS="true">
            <keystore>file://${configDirectory}/keystore-smtps</keystore>
            <secret>james</secret>
            <provider>org.bouncycastle.jce.provider.BouncyCastleProvider</provider>
            <algorithm>SunX509</algorithm>
        </tls>
        <connectiontimeout>360</connectiontimeout>
        <connectionLimit>0</connectionLimit>
        <connectionLimitPerIP>0</connectionLimitPerIP>
        <authRequired>false</authRequired>
        <authorizedAddresses>127.0.0.0/8</authorizedAddresses>
        <verifyIdentity>true</verifyIdentity>
        <maxmessagesize>${emailConfig.maxMessageSizeInKb?c}</maxmessagesize>
        <addressBracketsEnforcement>true</addressBracketsEnforcement>
        <smtpGreeting>Foilen Email Server (Apache JAMES)</smtpGreeting>
        <handlerchain>
            <handler class="com.foilen.james.components.handler.fastfail.ValidRcptHandler"/>
            <handler class="org.apache.james.smtpserver.CoreCmdHandlerLoader"/>
        </handlerchain>
    </smtpserver>
    <smtpserver enabled="true">
        <jmxName>smtpserver-socketTLS-authenticated</jmxName>
        <bind>0.0.0.0:10465</bind>
        <connectionBacklog>200</connectionBacklog>
        <tls socketTLS="true" startTLS="false">
            <keystore>file://${configDirectory}/keystore-smtps</keystore>
            <secret>james</secret>
            <provider>org.bouncycastle.jce.provider.BouncyCastleProvider</provider>
            <algorithm>SunX509</algorithm>
        </tls>
        <connectiontimeout>360</connectiontimeout>
        <connectionLimit>0</connectionLimit>
        <connectionLimitPerIP>0</connectionLimitPerIP>
        <!--
           Authorize only local users
        -->
        <authRequired>true</authRequired>
        <authorizedAddresses>127.0.0.0/8</authorizedAddresses>
        <!-- Trust authenticated users -->
        <verifyIdentity>true</verifyIdentity>
        <maxmessagesize>${emailConfig.maxMessageSizeInKb?c}</maxmessagesize>
        <addressBracketsEnforcement>true</addressBracketsEnforcement>
        <smtpGreeting>Foilen Email Server (Apache JAMES)</smtpGreeting>
        <handlerchain>
            <handler class="com.foilen.james.components.handler.fastfail.ValidRcptHandler"/>
            <handler class="org.apache.james.smtpserver.CoreCmdHandlerLoader"/>
        </handlerchain>
    </smtpserver>
    <smtpserver enabled="true">
        <jmxName>smtpserver-startTLS-authenticated</jmxName>
        <bind>0.0.0.0:10587</bind>
        <connectionBacklog>200</connectionBacklog>
        <tls socketTLS="false" startTLS="true">
            <keystore>file://${configDirectory}/keystore-smtps</keystore>
            <secret>james</secret>
            <provider>org.bouncycastle.jce.provider.BouncyCastleProvider</provider>
            <algorithm>SunX509</algorithm>
        </tls>
        <connectiontimeout>360</connectiontimeout>
        <connectionLimit>0</connectionLimit>
        <connectionLimitPerIP>0</connectionLimitPerIP>
        <!--
           Authorize only local users
        -->
        <authRequired>true</authRequired>
        <authorizedAddresses>127.0.0.0/8</authorizedAddresses>
        <!-- Trust authenticated users -->
        <verifyIdentity>true</verifyIdentity>
        <maxmessagesize>${emailConfig.maxMessageSizeInKb?c}</maxmessagesize>
        <addressBracketsEnforcement>true</addressBracketsEnforcement>
        <smtpGreeting>Foilen Email Server (Apache JAMES)</smtpGreeting>
        <handlerchain>
            <handler class="com.foilen.james.components.handler.fastfail.ValidRcptHandler"/>
            <handler class="org.apache.james.smtpserver.CoreCmdHandlerLoader"/>
        </handlerchain>
    </smtpserver>
</smtpservers>


