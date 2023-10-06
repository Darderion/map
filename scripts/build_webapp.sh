#!/bin/bash

cd src/main/webapp &&
bash buildscript.sh &&
cd ../../.. &&

mkdir -p build &&
mkdir -p build/logs &&

cp scripts/webapp.sh build/webapp.sh &&
cp src/main/webapp/app.js build/app.js &&
cp src/main/webapp/dist/ build/dist/ -r &&
cp src/main/webapp/node_modules/ build/node_modules/ -r &&

echo "Webapp has been built"
