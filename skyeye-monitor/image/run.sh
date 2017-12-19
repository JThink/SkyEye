#!/bin/bash

source /etc/profile.d/java.sh

cd /opt/jthink/jthink-config
git checkout $BRANCH
git pull -r origin $BRANCH
cd /home/deploy/skyeye-monitor-$VERSION
nohup bin/skyeye-monitor &
tail -f /opt/jthink/jthink-config/skyeye/monitor/monitor.properties
