#!/bin/bash -e

TRIGGER=$1

echo Triggered by ${TRIGGER}

cd ..

./gradlew --no-daemon clean \
compileJava \
compileTestJava \
sonarqube -x test --info -DbuildTrigger=${TRIGGER}