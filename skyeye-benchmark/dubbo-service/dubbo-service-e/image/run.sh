#!/bin/bash

source /etc/profile.d/java.sh

cd /opt/jthink/jthink-config
git checkout $BRANCH
git pull -r origin $BRANCH
cd /home/deploy/dubbo-service-e-$VERSION
nohup bin/dubbo-service-e &
tail -f /opt/jthink/jthink-config/skyeye/alarm/alarm.properties
