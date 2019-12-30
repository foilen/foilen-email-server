#!/bin/bash

set -e

RUN_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $RUN_PATH

echo ----[ Prepare folder for docker image ]----
DOCKER_BUILD=$RUN_PATH/build/docker

rm -rf $DOCKER_BUILD
mkdir -p $DOCKER_BUILD/app

cp -v build/distributions/foilen-email-server-$VERSION.zip $DOCKER_BUILD/app/foilen-email-server.zip
cp -v docker-release/* $DOCKER_BUILD

cd $DOCKER_BUILD/app
unzip foilen-email-server.zip
rm foilen-email-server.zip
mv foilen-email-server-$VERSION/* .
rm -rf foilen-email-server-$VERSION

echo ----[ Docker image folder content ]----
find $DOCKER_BUILD

echo ----[ Build docker image ]----
DOCKER_IMAGE=foilen-email-server:$VERSION
docker build -t $DOCKER_IMAGE $DOCKER_BUILD
docker tag $DOCKER_IMAGE foilen/$DOCKER_IMAGE

rm -rf $DOCKER_BUILD
