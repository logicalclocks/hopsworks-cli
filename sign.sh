#!/bin/bash
set -e
VERSION=`grep -o -a -m 1 -h -r "version>.*</version" ./pom.xml | head -1 | sed "s/version//g" | sed "s/>//" | sed "s/<\///g"`
echo "Version is $VERSION"
gpg2 -ab target/hopsworks-cli-${VERSION}-shaded.jar
gpg2 --verify target/hopsworks-cli-${VERSION}-shaded.jar.asc

