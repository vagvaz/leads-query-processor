#!/bin/bash
FILE=$1
echo "Checking $FILE does not exists"

while [ ! -f "$FILE" ]; do
    echo "."
    sleep 1
done
echo "Now File $FILE does exists"

#example usage
#put it under .vertx_mods and
#./checkPid.sh /tmp/iman.pid && vertx runMod gr.tuc.softnet~deployer-comp-mod~1.0-SNAPSHOT -conf deployer33.json -cluster
