#!/bin/sh

nohup java -jar app.jar > logs/server.log &
pId=$!
echo "kill $pId" > pause_server.sh
echo "bash pause_app.sh && bash pause_server.sh" > map_pause.sh
chmod u+x map_pause.sh
chmod u+x pause_server.sh
