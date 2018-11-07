#!/bin/bash
set -e

gpg2 -ab target/hopsworks-cli-0.1-shaded.jar
gpg2 --verify target/hopsworks-cli-0.1-shaded.jar.asc

