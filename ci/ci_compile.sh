#!/bin/bash -e

cd ..

./gradlew --no-daemon clean \
compileJava \
compileTestJava \
build -x test