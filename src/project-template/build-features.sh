#!/usr/bin/env bash

for dir in Features/*/; do
  {
    git status | grep "${dir}" &&
    cd "${dir}" &&
    mvn clean install \
      -Dmaven.test.skip=$1 \
      -Dbuild.format=jar \
      -Dbuild.unpack=true \
      -Dbuild.includeBaseDirectory=false \
      -Dbuild.exclude=**/** &&
      rm -rf ./target/*sources.jar &&
      rm -rf ./target/*javadoc.jar &&
      mv ./target/*.jar ../../project-distribution &&
      cd -
  }
done
files=(./project-distribution/*.jar)
if [ -f "${files[0]}" ]; then
  mv ./project-distribution/*.jar ./ServerParts/features
fi
