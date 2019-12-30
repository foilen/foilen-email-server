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


<pop3servers>
    <pop3server enabled="true">
        <jmxName>pop3server</jmxName>
        <bind>0.0.0.0:10110</bind>
        <connectionBacklog>200</connectionBacklog>
        <tls socketTLS="false" startTLS="true">
            <keystore>file://${configDirectory}/keystore-pop3s</keystore>
            <secret>james</secret>
            <provider>org.bouncycastle.jce.provider.BouncyCastleProvider</provider>
        </tls>
        <connectiontimeout>1200</connectiontimeout>
        <connectionLimit>0</connectionLimit>
        <connectionLimitPerIP>0</connectionLimitPerIP>
        <handlerchain>
            <handler class="org.apache.james.pop3server.core.CoreCmdHandlerLoader"/>
        </handlerchain>
    </pop3server>
</pop3servers>
