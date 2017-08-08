#!/bin/sh

# zk servers, 192.168.88.70:2181,192.168.88.71:2181,192.168.88.72:2181/kafka/0.10.0.1
urls=$1

bin/kafka-topics.sh --create --zookeeper ${urls} --replication-factor 3 --partitions 3 --topic app-log