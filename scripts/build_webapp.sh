#!/bin/bash

cd src/main/webapp &&
bash buildscript.sh &&
cd ../../.. &&

mkdir -p build &&
mkdir -p build/logs 



echo "Webapp has been built"
