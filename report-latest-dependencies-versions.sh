#!/bin/bash

set -e

RUN_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $RUN_PATH

echo ----[ Compile ]----
./gradlew dependencyUpdates --refresh-dependencies | tee _report-latest.txt
