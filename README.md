# SkyEye
对java、scala等运行于jvm的程序进行实时日志采集、索引和可视化，对系统进行进程级别的监控，对系统内部的操作进行策略性的报警、对分布式的rpc调用进行trace跟踪以便于进行性能分析
# 架构
![](architecture.png)
- APP: 接入skyeye-client的系统会通过kafkaAppender向kafka写入日志
- es-indexer-group: kafka的es消费组，读取kafka的数据并批量bulk到es
- monitor-group: kafka的监控消费组，app在日志中进行各种event埋点（如：第三方异常报警、请求耗时异常报警等）
- business-group: kafka的业务消费组
- trace-group: 通过日志进行rpc调用trace跟踪（dapper论文）
- es: 日志存储db，并建立相关索引
- zookeeper: app注册中心
- monitor: 监控中心，监听zookeeper注册中心中相应的节点变化进行监控报警
- rabbitmq: 监控报警缓冲队列
- alert: 具体报警手段，包括邮件和微信
# 部署步骤
修改每个项目中gradle文件的私服地址等其他相关信息
修改每个项目中的properties文件中的配置项（配置文件外部化和内部化都需要修改）
## skyeye-base

``` shell
cd skyeye-base
gradle clean install uploadArchives
```
## skyeye-client

``` shell
cd skyeye-client
gradle clean install uploadArchives
```
## skyeye-data

``` shell
cd skyeye-data
gradle clean install uploadArchives
```
## skyeye-trace

``` shell
cd skyeye-trace
gradle clean install uploadArchives
```
## skyeye-alarm

``` shell
cd skyeye-alarm
gradle clean distZip -x test
cd target/distributions
unzip skyeye-alarm-x.x.x.zip(替换相应的x为自己的版本)
```
## skyeye-collector

根据对接系统的个数和产生日志的量进行部署，最好部署3个节点（每个节点消费3个partition的数据）
``` shell
cd skyeye-collector
gradle clean distZip -x test
cd target/distributions
unzip skyeye-collector-x.x.x.zip(替换相应的x为自己的版本)

cd skyeye-collector-x.x.x
nohup bin/skyeye-collector &
```
## skyeye-monitor

``` shell
cd skyeye-monitor
gradle clean distZip -x test
cd target/distributions
unzip skyeye-monitor-x.x.x.zip(替换相应的x为自己的版本)

cd skyeye-monitor-x.x.x
nohup bin/skyeye-monitor &
```
## skyeye-web

``` shell
cd skyeye-web
gradle clean distZip -x test
cd target/distributions
unzip skyeye-web-x.x.x.zip(替换相应的x为自己的版本)

cd skyeye-web-x.x.x
nohup bin/skyeye-web &
```

交流QQ: 516028058