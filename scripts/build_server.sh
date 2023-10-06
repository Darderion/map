#!/bin/bash

# mvn package -Dmaven.test.skip &&
mvn package &&
mv target/mundane-assignment-police-0.0.1-SNAPSHOT.jar target/app.jar &&

mkdir -p build &&
mkdir -p build/logs &&

cp target/app.jar build/app.jar &&
cp scripts/server.sh build/server.sh &&

echo "Server has been built"
