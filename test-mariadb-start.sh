#!/bin/bash

set -e

# Set environment
export LANG="C.UTF-8"
export VERSION=master-SNAPSHOT

RUN_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $RUN_PATH

FOLDER_DATA=$PWD/_data
mkdir -p $FOLDER_DATA

# Start mariadb
INSTANCE=foilen-email-server_db
DBNAME=james

cat > $FOLDER_DATA/createDb.sh << _EOF
#!/bin/bash
mysql -uroot -pABC << _EOFF
  CREATE DATABASE $DBNAME;
_EOFF
_EOF
chmod +x $FOLDER_DATA/createDb.sh

if ! docker ps | grep $INSTANCE ; then
	echo '###[ Start mariadb ]###'
	docker run \
	  --rm \
	  --name $INSTANCE \
	  --env MYSQL_ROOT_PASSWORD=ABC \
	  --env DBNAME=$DBNAME \
	  --publish 3306:3306 \
	  --volume $FOLDER_DATA:/data \
	  -d mariadb:10.3.6
  
  echo '###[ Wait 20 seconds ]###'
  sleep 20s
  echo '###[ Create the MariaDB database ]###'
  docker exec -ti $INSTANCE /data/createDb.sh
fi

# Start phpmyadmin
if ! docker ps | grep ${INSTANCE}_phpmyadmin ; then
	echo '###[ Start phpmyadmin ]###'
	docker run \
	  --rm \
	  --name ${INSTANCE}_phpmyadmin \
	  -d \
	  --link ${INSTANCE}:db \
	  -p 12345:80 \
	  phpmyadmin/phpmyadmin
  
fi

echo You can go on http://127.0.0.1:12345/ with user "root" and pass "ABC"
