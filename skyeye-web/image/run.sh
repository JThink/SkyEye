#!/bin/bash

source /etc/profile.d/java.sh

cd /opt/jthink/jthink-config
git checkout $BRANCH
git pull -r origin $BRANCH
cd /home/deploy/skyeye-web-$VERSION
nohup bin/skyeye-web &
tail -f /opt/jthink/jthink-config/skyeye/web/web.properties
