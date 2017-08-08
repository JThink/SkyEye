#!/bin/bash

# 索引
# urls http://192.168.88.70:9200,http://192.168.88.71:9200,http://192.168.88.72:9200
# call: bash start.sh event-log http://192.168.88.70:9200,http://192.168.88.71:9200,http://192.168.88.72:9200

index=$1
urls=$2

echo '创建索引开始'
python create-index.py ${index} ${urls}
echo '创建索引结束'