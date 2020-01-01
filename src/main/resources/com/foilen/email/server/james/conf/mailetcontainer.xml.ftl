<?xml version="1.0"?>

<!--
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.    See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.    The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.    See the License for the
    specific language governing permissions and limitations
    under the License.
 -->

<mailetcontainer enableJmx="true">

  <context>
    <postmaster>${emailConfig.postmasterEmail}</postmaster>
  </context>

  <spooler>
    <threads>20</threads>
  </spooler>

  <processors>

    <processor state="transport" enableJmx="true">
    
      <mailet match="All" class="com.foilen.james.components.mailet.LogInfo">
        <text>Is in transport</text>
      </mailet>
      
      <!-- Remove all BCC -->
      <mailet match="All" class="RemoveMimeHeader">
          <name>bcc</name>
          <onMailetException>ignore</onMailetException>
      </mailet>
      
      <mailet match="All" class="ToProcessor">
        <processor>root</processor>
      </mailet>
      
    </processor>
    
    <processor state="root" enableJmx="true">
    
      <mailet match="All" class="com.foilen.james.components.mailet.LogInfo">
        <text>Is in root</text>
      </mailet>
    
      <mailet match="All" class="PostmasterAlias" />
      <mailet match="All" class="RecipientToLowerCase" />
      <mailet match="RelayLimit=30" class="Null" />

      <!-- Check if authenticated -->
      <mailet match="SMTPAuthSuccessful" class="SetMimeHeader">
        <name>X-UserIsAuth</name>
        <value>true</value>
      </mailet>

      <#if emailConfig.enableDebugDumpMessagesDetails >
        <mailet match="All" class="com.foilen.james.components.mailet.DumpAllLoggerInfo" />
      </#if>
      
      <!-- Check recipient's redirections -->
      <mailet match="All" class="com.foilen.james.components.mailet.ExactAndCatchAllRedirections">
        <cacheMaxTimeInSeconds>10</cacheMaxTimeInSeconds>
        <cacheMaxEntries>1000</cacheMaxEntries>
      </mailet>
      
      <!-- Local delivery -->
      <mailet match="RecipientIsLocal" class="ToProcessor">
        <processor>localProcessor</processor>
      </mailet>

      <!-- Local delivery - The domain is managed locally, but the local mailbox does not exist -->
      <mailet match="HostIsLocal" class="ToProcessor">
        <processor>localAccountDoesNotExistProcessor</processor>
        <notice>550 - Requested action not taken: no such user here</notice>
      </mailet>
      
      <!-- Outgoing -->
      <mailet match="HasHeader=X-UserIsAuth" class="ToProcessor">
        <processor>outgoingAuthProcessor</processor>
      </mailet>
      
      <mailet match="All" class="ToProcessor">
        <processor>outgoingAnonymousProcessor</processor>
      </mailet>
      
    </processor>

    <processor state="localProcessor" enableJmx="true">
    
      <mailet match="All" class="com.foilen.james.components.mailet.LogInfo">
        <text>Is in localProcessor</text>
      </mailet>
      
      <mailet match="All" class="AddDeliveredToHeader" />
      <mailet match="All" class="LocalDelivery" />
    
    </processor>
    
    <processor state="outgoingAuthProcessor" enableJmx="true">
    
      <mailet match="All" class="com.foilen.james.components.mailet.LogInfo">
        <text>Is in outgoingAuthProcessor</text>
      </mailet>
      
      <!-- Relay emails per domain to different gateways -->
      <#list emailConfig.domainAndRelais as domainAndRelay>
      
        <mailet match="SenderIsRegex=(.*)@${domainAndRelay.domain}" class="com.foilen.james.components.mailet.LogInfo">
          <text>Remote delivery via the gateway for SenderIsRegex=(.*)@${domainAndRelay.domain}</text>
        </mailet>
        <mailet match="SenderIsRegex=(.*)@${domainAndRelay.domain}" class="RemoteDelivery">
          <outgoing>outgoing-${domainAndRelay.domain}</outgoing>
    
          <delayTime>5000, 100000, 500000</delayTime>
          <maxRetries>25</maxRetries>
          <maxDnsProblemRetries>0</maxDnsProblemRetries>
          <deliveryThreads>10</deliveryThreads>
          <sendpartial>true</sendpartial>
          <bounceProcessor>bounceProcessor</bounceProcessor>
          <gateway>${domainAndRelay.hostname}</gateway>
          <gatewayPort>${domainAndRelay.port}</gatewayPort>
          <gatewayUsername>${domainAndRelay.username}</gatewayUsername>
          <gatewayPassword>${domainAndRelay.password}</gatewayPassword>
        </mailet>
      </#list>
      
      <mailet match="All" class="ToProcessor">
        <processor>outgoingDirectRelayProcessor</processor>
      </mailet>
      
    </processor>

    <processor state="outgoingDirectRelayProcessor" enableJmx="true">
    
      <!-- Relay -->
      <mailet match="All" class="com.foilen.james.components.mailet.LogInfo">
        <text>Remote delivery via the server directly (no gateway)</text>
      </mailet>
      <mailet match="All" class="RemoteDelivery">
        <outgoing>outgoing</outgoing>

        <delayTime>5000, 100000, 500000</delayTime>
        <maxRetries>25</maxRetries>
        <maxDnsProblemRetries>0</maxDnsProblemRetries>
        <deliveryThreads>10</deliveryThreads>
        <sendpartial>true</sendpartial>
        <bounceProcessor>bounceProcessor</bounceProcessor>
      </mailet>
      
    </processor>
    
    <processor state="outgoingAnonymousProcessor" enableJmx="true">
    
      <mailet match="All" class="com.foilen.james.components.mailet.LogInfo">
        <text>Is in outgoingAnonymousProcessor</text>
      </mailet>
      
      <!-- If is a redirection -> Send directly (since could be spam) -->
      <mailet match="HasHeader=isRedirection" class="ToProcessor">
        <processor>outgoingDirectRelayProcessor</processor>
      </mailet>
      
      <!-- If is not a redirection -> Deny -->
      <mailet match="All" class="ToProcessor">
        <processor>outgoingDeniedProcessor</processor>
        <notice>550 - Requested action not taken: relaying denied</notice>
      </mailet>
      
    </processor>
    
    <processor state="localAccountDoesNotExistProcessor" enableJmx="true">
    
      <mailet match="All" class="com.foilen.james.components.mailet.LogInfo">
        <text>Is in localAccountDoesNotExistProcessor</text>
      </mailet>
    
      <mailet match="All" class="Bounce">
        <attachment>none</attachment>
      </mailet>
      
    </processor>

    <processor state="outgoingDeniedProcessor" enableJmx="true">
    
      <#if !emailConfig.disableRelayDeniedNotifyPostmaster >
        <mailet match="All" class="NotifyPostmaster">
          <sender>unaltered</sender>
          <attachError>true</attachError>
          <prefix>[RELAY-DENIED]</prefix>
          <passThrough>true</passThrough>
          <to>postmaster</to>
          <debug>true</debug>
        </mailet>
      </#if>

      <mailet match="All" class="Null" />
      
    </processor>

    <processor state="bounceProcessor" enableJmx="true">
    
      <#if !emailConfig.disableBounceNotifyPostmaster >
        <mailet match="All" class="NotifyPostmaster">
          <sender>unaltered</sender>
          <attachError>true</attachError>
          <prefix>[BOUNCE]</prefix>
          <passThrough>true</passThrough>
          <to>postmaster</to>
          <debug>true</debug>
        </mailet>
      </#if>
      
      <#if !emailConfig.disableBounceNotifySender >
        <mailet match="All" class="DSNBounce">
          <passThrough>true</passThrough>
        </mailet>
      </#if>
      
      <mailet match="All" class="Null" />
      
    </processor>
    
    <processor state="error" enableJmx="true">
    
      <mailet match="All" class="NotifyPostmaster">
        <sender>unaltered</sender>
        <attachError>true</attachError>
        <prefix>[ERROR]</prefix>
        <passThrough>false</passThrough>
        <to>postmaster</to>
        <debug>true</debug>
      </mailet>
      
    </processor>
    
  </processors>

</mailetcontainer>
