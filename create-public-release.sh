#!/bin/bash

set -e

RUN_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $RUN_PATH

# Check params
if [ $# -ne 1 ]
	then
		echo Usage: $0 version;
    echo E.g: $0 0.1.0
		echo Version is MAJOR.MINOR.BUGFIX
		echo Latest version:
		git describe --abbrev=0
		exit 1;
fi

export VERSION=$1

./step-update-copyrights.sh
./step-clean-compile.sh
./step-create-docker-image.sh
./step-upload-docker-image.sh
./step-git-tag.sh

echo ----[ Operation completed successfully ]----

echo
echo You can see published items on https://hub.docker.com/r/foilen/foilen-crm/tags/
echo You can send the tag: git push --tags
