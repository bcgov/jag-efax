#!/bin/bash

# updates the version for the java project to a non-snapshot version tag

RELEASE_VERSION=$(mvn -f pom.xml versions:set -DartifactId=* -DgroupId=* -DnewVersion=$RELEASE_VERSION)

echo $RELEASE_VERSION
