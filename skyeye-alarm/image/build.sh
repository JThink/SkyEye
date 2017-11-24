#!/bin/bash

version=$1
branch=$2
cp ../target/distributions/*.tar .
sudo docker build -t --build-arg branch=$branch --build-arg version=$version 192.168.88.73:8888/skyeye/skyeye-alarm:$version .
sudo docker push 192.168.88.73:8888/skyeye/skyeye-alarm:$version
