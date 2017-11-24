#!/bin/bash

version=$1
branch=$2
cp ../target/distributions/*.tar .

sudo docker build --build-arg version=$version --build-arg branch=$branch -t 192.168.88.73:8888/skyeye/skyeye-collector-indexer:$version .
sudo docker push 192.168.88.73:8888/skyeye/skyeye-collector-indexer:$version
