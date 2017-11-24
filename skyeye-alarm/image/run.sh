#!/bin/bash

version=$1
branch=$2

source /etc/profile.d/java.sh

cd /opt/jthink/jthink-config
git pull -r origin $branch
cd /home/deploy/skyeye-alarm-$version
nohup bin/skyeye-alarm &
tail -f /opt/jthink/jthink-config/skyeye/alarm/alarm.properties
