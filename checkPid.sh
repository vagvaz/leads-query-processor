#!/bin/bash
FILE=$1
echo "Checking $FILE does not exists"
COUNTER=0;
while [ ! -f "$FILE" ]; do
    printf "."
    sleep 1
    COUNTER=$((COUNTER + 1))
done
echo "$COUNTER seconds passed, file $FILE created."

#example usage
#put it under .vertx_mods and
#./checkPid.sh /tmp/iman.pid && vertx runMod gr.tuc.softnet~deployer-comp-mod~1.0-SNAPSHOT -conf deployer33.json -cluster
