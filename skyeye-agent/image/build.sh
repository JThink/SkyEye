#!/bin/bash

version=$1
cp ../target/distributions/*.tar .

sudo docker build --build-arg version=$version -t 192.168.88.73:8888/skyeye/skyeye-agent:$version .
sudo docker push 192.168.88.73:8888/skyeye/skyeye-agent:$version
