#!/bin/bash

cd src/main/webapp &&
bash buildscript.sh &&
cd ../../.. &&
mvn package -Dmaven.test.skip &&
# java -jar target/mundane-assignment-police-0.0.1-SNAPSHOT.jar
mv target/mundane-assignment-police-0.0.1-SNAPSHOT.jar target/app.jar &&

mkdir -p build &&
cp target/app.jar build/app.jar &&
cp scripts/server.sh build/server.sh &&
cp scripts/webapp.sh build/webapp.sh &&
cp src/main/webapp/app.js build/app.js &&
cp src/main/webapp/dist/ build/dist/ -r &&
cp src/main/webapp/node_modules/ build/node_modules/ -r &&
mkdir -p build/logs
