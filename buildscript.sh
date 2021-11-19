#!/bin/bash

cd src/main/webapp &&
bash buildscript.sh &&
cd ../../.. &&
mvn package -Dmaven.test.skip &&
# java -jar target/mundane-assignment-police-0.0.1-SNAPSHOT.jar
mv target/mundane-assignment-police-0.0.1-SNAPSHOT.jar target/app.jar
