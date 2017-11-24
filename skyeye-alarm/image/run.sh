#!/bin/bash

source /etc/profile.d/java.sh

cd /opt/jthink/jthink-config
git pull -r origin master
cd /home/deploy/skyeye-alarm-1.3.0
nohup bin/skyeye-alarm &
tail -f /opt/jthink/jthink-config/skyeye/alarm/alarm.properties