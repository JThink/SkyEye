#!/bin/bash

source /etc/profile.d/java.sh

cd /opt/jthink/jthink-config
git checkout $BRANCH
git pull -r origin $BRANCH
cd /home/deploy/skyeye-collector-backup-$VERSION
nohup bin/skyeye-collector-backup &
tail -f /opt/jthink/jthink-config/skyeye/collector/collector-backup.properties
