#!/bin/sh

date=$(date +"%T")
nohup java -jar app.jar > logs/server_$date.log &
pId=$!
cat << EOF > pause_server.sh
kill $pId || echo "SERVER WITH pID=$pId WAS NOT FOUND

That could be caused by either:

Multiple attempts of launching a server when it was already running
In this case you can pause the server by running
\\\$ kill \\\$(lsof -t -i :PORT)

OR

Server not launched
You can locate the process by running
\\\$ lsof -i :PORT

where PORT is the port on which you're running the server
Example:
\\\$ lsof -i :8081

You can also use this command to ensure that the port is available:

\\\$ kill \\\$(lsof -t -i :PORT); bash server.sh"
EOF

echo "bash pause_app.sh; bash pause_server.sh" > map_pause.sh
chmod u+x map_pause.sh
chmod u+x pause_server.sh
