#!/bin/bash
set -e
mvn clean install -DskipTests
mv target/appassembler hopsworks-cli
tar zcf hopsworks-cli.tar.gz hopsworks-cli
rm -rf hopsworks-cli
scp hopsworks-cli.tar.gz glassfish@snurran.sics.se:/var/www/hops
