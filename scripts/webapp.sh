#!/bin/sh

nohup node app.js > logs/webapp.log &
pId=$!
echo "kill $pId" > pause_app.sh
echo "bash pause_app.sh && bash pause_server.sh" > map_pause.sh
chmod u+x map_pause.sh
chmod u+x pause_app.sh
