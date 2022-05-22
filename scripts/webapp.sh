#!/bin/sh

date=$(date +"%T")
nohup node app.js > logs/webapp_$date.log &
pId=$!
cat << EOF > pause_app.sh
kill $pId || echo "WEBAPP WITH pID=$pId WAS NOT FOUND

That could be caused by either:

Multiple attempts of launching a webapp when it was already running
In this case you can pause the webapp by running
\\\$ kill \\\$(lsof -t -i :PORT)

OR

Webapp not launched
You can locate the process by running
\\\$ lsof -i :PORT

where PORT is the port on which you're running the webapp
Example:
\\\$ lsof -i :8080

You can also use this command to ensure that the port is available:

\\\$ kill \\\$(lsof -t -i :PORT); bash webapp.sh"
EOF
echo "bash pause_app.sh; bash pause_server.sh" > map_pause.sh
chmod u+x map_pause.sh
chmod u+x pause_app.sh
