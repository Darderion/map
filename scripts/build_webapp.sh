#!/bin/bash

cd src/main/webapp &&
bash buildscript.sh &&
cd ../../.. &&

mkdir -p build &&
mkdir -p build/logs &&

mkdir -p build/webapp &&
cp -r src/main/webapp/build/. build/webapp &&
cp scripts/webapp.sh build/webapp.sh &&

echo "Webapp has been built"
