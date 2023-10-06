#!/bin/bash

# /mundane-assignment-police/src/main/webapp
npm install &&
npm run build --fix &&
mkdir -p ../resources/static &&
rm -r -f ../resources/static/js &&
cp -a dist/. ../resources/static/ &&
rm -r -f ../resources/static/app &&
mkdir ../resources/static/app &&
mv ../resources/static/index.html ../resources/static/app/index.html
