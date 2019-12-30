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


<imapservers>
    <imapserver enabled="true">
        <jmxName>imapserver</jmxName>
        <bind>0.0.0.0:10143</bind>
        <connectionBacklog>2000</connectionBacklog>
        <tls socketTLS="false" startTLS="true">
            <keystore>file://${configDirectory}/keystore-imaps</keystore>
            <secret>james</secret>
            <provider>org.bouncycastle.jce.provider.BouncyCastleProvider</provider>
        </tls>
        <connectionLimit>0</connectionLimit>
        <connectionLimitPerIP>0</connectionLimitPerIP>
        <idleTimeInterval>120</idleTimeInterval>
        <idleTimeIntervalUnit>SECONDS</idleTimeIntervalUnit>
        <enableIdle>true</enableIdle>
    </imapserver>
    <imapserver enabled="true">
        <jmxName>imapsserver-tls</jmxName>
        <bind>0.0.0.0:10993</bind>
        <connectionBacklog>2000</connectionBacklog>
        <tls socketTLS="true" startTLS="false">
            <keystore>file://${configDirectory}/keystore-imaps</keystore>
            <secret>james</secret>
            <provider>org.bouncycastle.jce.provider.BouncyCastleProvider</provider>
        </tls>
        <connectionLimit>0</connectionLimit>
        <connectionLimitPerIP>0</connectionLimitPerIP>
        <idleTimeInterval>120</idleTimeInterval>
        <idleTimeIntervalUnit>SECONDS</idleTimeIntervalUnit>
        <enableIdle>true</enableIdle>
    </imapserver>
</imapservers>
