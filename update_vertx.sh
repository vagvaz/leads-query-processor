#!/bin/bash
for i in `cat dirs.sh`; do 
  #cp -a $i/target/mods/* $VERTX_MODS/
  rsync -r -az --delete $i/target/mods/* $VERTX_MODS/
done

