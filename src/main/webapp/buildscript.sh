#!/bin/bash

# /mundane-assignment-police/src/main/webapp
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"
[ -s "$NVM_DIR/bash_completion" ] && \. "$NVM_DIR/bash_completion"

# nvm install 18.17.0 &&
# nvm use 18.17.0 &&
# npm install -g npm@latest &&
npm install &&
npm run build -- --fix &&
# npm install --global serve &&
mkdir -p ../resources/static &&
rm -rf ../resources/static/js &&
cp -a build/. ../resources/static/ &&
rm -rf ../resources/static/app &&
mkdir ../resources/static/app &&
mv ../resources/static/index.html ../resources/static/app/index.html
# serve -s build 

