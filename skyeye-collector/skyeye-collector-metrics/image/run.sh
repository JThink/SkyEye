#!/bin/bash

source /etc/profile.d/java.sh

cd /opt/jthink/jthink-config
git checkout $BRANCH
git pull -r origin $BRANCH
cd /home/deploy/skyeye-collector-metrics-$VERSION
nohup bin/skyeye-collector-metrics &
tail -f /opt/jthink/jthink-config/skyeye/collector/collector-metrics.properties
