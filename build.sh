#!/bin/bash

# set the project dir
home_dir=`pwd`
base_dir=$home_dir/skyeye-base
data_dir=$home_dir/skyeye-data
client_dir=$home_dir/skyeye-client
trace_dir=$home_dir/skyeye-trace
alarm_dir=$home_dir/skyeye-alarm
collector_dir=$home_dir/skyeye-collector
monitor_dir=$home_dir/skyeye-monitor
web_dir=$home_dir/skyeye-web

# compile the base project
echo 'start to compile skyeye-base...'
cd $base_dir
gradle clean install uploadArchives
echo 'finish compile skyeye-base...'

# compile the data project
echo 'start to compile skyeye-data...'
cd $data_dir
gradle clean install uploadArchives
echo 'finish compile skyeye-data...'

# compile the trace project
echo 'start to compile skyeye-trace...'
cd $trace_dir
gradle clean install uploadArchives
echo 'finish compile skyeye-trace...'

# compile the client project
echo 'start to compile skyeye-client...'
cd $client_dir
gradle clean install uploadArchives
echo 'finish compile skyeye-client...'

# compile the alarm project
echo 'start to compile skyeye-alarm...'
cd $alarm_dir
gradle clean distZip -x test
echo 'finish compile skyeye-alarm...'

# compile the collector project
echo 'start to compile skyeye-collector...'
cd $collector_dir
gradle clean build -x test
echo 'finish compile skyeye-collector...'

# compile the monitor project
echo 'start to compile skyeye-monitor...'
cd $monitor_dir
gradle clean distZip -x test
echo 'finish compile skyeye-monitor...'

# compile the web project
echo 'start to compile skyeye-web...'
cd $web_dir
gradle clean distZip -x test
echo 'finish compile skyeye-web...'
