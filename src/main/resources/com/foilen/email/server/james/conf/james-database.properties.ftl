#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied.  See the License for the
#  specific language governing permissions and limitations
#  under the License.

#  This template file can be used as example for James Server configuration
#  DO NOT USE IT AS SUCH AND ADAPT IT TO YOUR NEEDS

# See http://james.apache.org/server/3/config.html for usage

database.driverClassName=org.mariadb.jdbc.Driver
database.url=jdbc:mariadb://${emailConfig.database.hostname}:${emailConfig.database.port?c}/${emailConfig.database.database}
database.username=${emailConfig.database.username}
database.password=${emailConfig.database.password}

# Supported adapters are:
# DB2, DERBY, H2, HSQL, INFORMIX, MYSQL, ORACLE, POSTGRESQL, SQL_SERVER, SYBASE 
vendorAdapter.database=MYSQL

# Use streaming for Blobs
# This is only supported on a limited set of databases atm. You should check if its supported by your DB before enable
# it. 
# 
# See:
# http://openjpa.apache.org/builds/latest/docs/manual/ref_guide_mapping_jpa.html  #7.11.  LOB Streaming 
# 
openjpa.streaming=false

# Validate the data source before using it
datasource.testOnBorrow=true
datasource.validationQueryTimeoutSec=2
# This is different per database. See https://stackoverflow.com/questions/10684244/dbcp-validationquery-for-different-databases#10684260
datasource.validationQuery=select 1
