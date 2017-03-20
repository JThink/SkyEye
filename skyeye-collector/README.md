# 项目介绍
日志采集入库、监控报警以及索引等服务
# 功能介绍
- 日志索引: 日志采集入es并建立索引
- 日志备份: 日志采集入文件并上传至hdfs备份
- 项目信息采集: 对事件日志进行名称采集
- 监控数据采集: 对监控数据建立索引存入es
- rpc trace数据采集: 采集rpc 跟踪的span数据并存入hbase
# 中间件介绍
## elasticsearch
- app-log的字段
![](column.png)
## kafka
- partition: 9个partition，项目部署3个节点，每个节点消费3个（利用kafka消费组的机制进行rebalance）
- 消息有序性: 对接的系统抽象出app和host，确保每个节点上部署的一个项目的日志只进入一个partition
- 消息有且仅消费一次: 利用同步和异步提交offset和回滚机制实现
## hbase（rpc trace数据）
1. trace表（数据表）
    1. rowkey: traceId
    2. columnFamily: span
    3. qualifier: [spanId+c, spanId+s ...](有N个span就有N*2个, c/s表示是client还是server采集到的)
    4. value: span json value
2. time_consume（索引表）
    1. rowkey: serviceId + cs时间
    2. columnFamily: trace
    3. qualifier: traceId ...
    4. value: 整个调用链条耗时
3. annotation（索引表）
    1. rowkey: serviceId + ExceptionType + cs/sr时间
    2. columnFamily: trace
    3. qualifier: traceId ...
    4. value: binaryAnnotation的value
