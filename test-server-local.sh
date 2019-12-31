#!/bin/bash

set -e

# Set environment
export LANG="C.UTF-8"
export VERSION=master-SNAPSHOT

RUN_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $RUN_PATH

FOLDER_DATA=$PWD/_data
mkdir -p $FOLDER_DATA

# Build
echo '###[ Create build ]###'
./step-update-copyrights.sh
./step-compile-no-tests.sh
./step-create-docker-image.sh

# Config
DB_HOST=$(docker inspect foilen-email-server_db | tr -s ' ' ' ' | grep '"IPAddress"' | head -n 1 | cut -d '"' -f 4)
cat > $FOLDER_DATA/james-config.json << _EOF
{
  "database" : {
    "hostname" : "$DB_HOST",
    "database" : "james",
    "port" : 3306,
    "username" : "root",
    "password" : "ABC"
  },
  "postmasterEmail" : "account@localhost.foilen-lab.com",
  "enableDebugDumpMessagesDetails" : false,
  "disableRelayDeniedNotifyPostmaster" : false,
  "disableBounceNotifyPostmaster" : false,
  "disableBounceNotifySender" : false,
  "domainAndRelais" : []
}
_EOF

# Start
echo '###[ Start Server ]###'
USER_ID=$(id -u)
docker run -ti \
  --rm \
  --name foilen-email-server\
  --publish 25:10025 \
  --publish 110:10110 \
  --publish 143:10143 \
  --publish 587:10587 \
  --user $USER_ID \
  --volume $FOLDER_DATA:/workdir \
  foilen-email-server:master-SNAPSHOT \
  --jamesConfigFile /workdir/james-config.json \
  --workDir /workdir \
  --debug
