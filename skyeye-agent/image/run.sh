#!/bin/bash

source /etc/profile.d/java.sh

cd /home/deploy/skyeye-agent-$VERSION
nohup bin/skyeye-agent &
tail -f /opt/jthink/jthink-config/README.md
