#!/bin/bash

source /etc/profile.d/java.sh

cd /opt/jthink/jthink-config
git checkout $BRANCH
git pull -r origin $BRANCH
cd /home/deploy/hi-log-log4j2-$VERSION
nohup bin/hi-log-log4j2 &
tail -f /opt/jthink/jthink-config/skyeye/alarm/alarm.properties
