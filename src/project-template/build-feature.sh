#!/usr/bin/env bash

cd "./Features/$1" && mvn clean install \
  -Dmaven.test.skip=false \
  -Dmaven.javadoc.skip=$2 \
  -Dbuild.format=jar \
  -Dbuild.unpack=true \
  -Dbuild.includeBaseDirectory=false \
  -Dbuild.exclude=**/** &&
  rm -rf ./target/*sources.jar &&
  mv ./target/*.jar ../../ServerParts/features &&
  cd -
