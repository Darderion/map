#!/bin/sh

date=$(date +"%T")
nohup npx serve webapp/ -s > logs/webapp_$date.log &
pId=$!
cat << EOF > pause_webapp.sh
kill \\\$(lsof -t -i :3000)
EOF

echo "bash pause_server.sh; bash pause_server.sh" > map_pause.sh
chmod u+x map_pause.sh
chmod u+x pause_webapp.sh

