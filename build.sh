#!/bin/bash

version=$1
branch=$2

pwd=`pwd`

echo "start build skyeye-agent"
cd $pwd/skyeye-agent/image
sudo bash build.sh $version
echo "finished build skyeye-agent"

echo "start build skyeye-alarm"
cd $pwd/skyeye-alarm/image
sudo bash build.sh $version $branch
echo "finished build skyeye-alarm"

echo "start build skyeye-monitor"
cd $pwd/skyeye-monitor/image
sudo bash build.sh $version $branch
echo "finished build skyeye-monitor"

echo "start build skyeye-web"
cd $pwd/skyeye-web/image
sudo bash build.sh $version $branch
echo "finished build skyeye-web"

echo "start build skyeye-collector-backup"
cd $pwd/skyeye-collector/skyeye-collector-backup/image
sudo bash build.sh $version $branch
echo "finished build skyeye-collector-backup"

echo "start build skyeye-collector-indexer"
cd $pwd/skyeye-collector/skyeye-collector-indexer/image
sudo bash build.sh $version $branch
echo "finished build skyeye-collector-indexer"

echo "start build skyeye-collector-metrics"
cd $pwd/skyeye-collector/skyeye-collector-metrics/image
sudo bash build.sh $version $branch
echo "finished build skyeye-collector-metrics"

echo "start build skyeye-collector-trace"
cd $pwd/skyeye-collector/skyeye-collector-trace/image
sudo bash build.sh $version $branch
echo "finished build skyeye-collector-trace"
