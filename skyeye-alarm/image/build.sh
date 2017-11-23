#!/bin/bash

version=$1
sudo docker build -t 192.168.88.73:8888/skyeye/skyeye-alarm:$version .
sudo docker push 192.168.88.73:8888/skyeye/skyeye-alarm:$version
