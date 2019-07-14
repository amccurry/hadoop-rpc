#!/bin/bash
set -e

# Before running this commit new version in pom file in master branch and have master checked out.

VERSION="${1}"

git checkout -b ${VERSION}-release

mvn clean install
mvn \
 install:install-file \
 -DgroupId=hadoop-rpc \
 -DartifactId=hadoop-rpc \
 -Dversion=${VERSION} \
 -Dfile=./target/hadoop-rpc-${VERSION}.jar \
 -Dpackaging=jar \
 -DgeneratePom=true \
 -DlocalRepositoryPath=. \
 -DcreateChecksum=true

git add hadoop-rpc/
git commit -a -m "Adding maven repo info."

git tag ${VERSION}
git push origin ${VERSION}
git push origin ${VERSION}-release
git checkout master
