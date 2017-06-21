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

每个项目都需要修改gradle文件中的私服地址（这样才能打包deploy到自己的本地私服）

## skyeye-base

本项目没有具体的业务逻辑，主要是各个模块通用的类定义，如：常量、dto、dapper相关、公用util，所以该项目无需部署，只需要打包。

```shell
cd skyeye-base
gradle clean install uploadArchives
```

## skyeye-client

本项目主要是提供给对接的项目使用，包含了log4j和logback的自定义appender和项目注册相关，所以该项目无需部署，只需要打包提供给对接方对接。

```shell
cd skyeye-client
gradle clean install uploadArchives
```

## skyeye-data

本项目主要是用来提供和数据操作相关的中间件，具体分为以下5个子modoule。本项目无需部署，只需要打包。

```shell
cd skyeye-data
gradle clean install uploadArchives
```

### skyeye-data-dubbox

该项目主要是自定义的spring-boot的dubbox starter，为spring-boot相关的项目使用dubbox提供简易的方式并集成spring-boot的auto configuration，见我的另一个开源项目：https://github.com/JThink/spring-boot-starter-dubbox

### skyeye-data-hbase

该项目主要是自定义的spring-boot的hbase starter，为hbase的query和更新等操作提供简易的api并集成spring-boot的auto configuration，见我的另一个开源项目：https://github.com/JThink/spring-boot-starter-hbase

### skyeye-data-http

该项目主要使用连接池简单封装了http的请求，如果项目中使用的spring版本较高可以使用RestTemplate代替。

### skyeye-data-jpa

该项目主要是jpa相关的定义，包含domain、repository、dto相关的定义，主要用来操作mysql的查询。

### skyeye-data-rabbitmq

该项目主要封装了报警模块中存取rabbitmq中消息的相关代码。

## skyeye-trace

该项目封装了所有rpc trace相关的代码，包含rpc数据采集器、分布式唯一ID生成、分布式递增ID生成、注册中心、采样器、跟踪器等功能，该项目无需部署，只需要打包。

### dubbox

由于使用dubbox，为了能够采集到dubbox里面的rpc数据，需要修改dubbox的源码，见：https://github.com/JThink/dubbox/tree/skyeye-trace，该项目主要实现了rpc跟踪的具体实现，需要单独打包。

```shell
git clone https://github.com/JThink/dubbox.git
cd dubbox
git checkout skyeye-trace
修改相关pom中的私服地址
mvn clean install deploy -Dmaven.test.skip=true
```

## 软件安装

如果软件版本和以下所列不一致，需要修改gradle中的依赖版本，并且需自行测试可用性（hadoop、hbase、spark等相应的版本可以自己来指定，代码层面无需修改，需要修改依赖）。

| 软件名           | 版本             | 备注                                       |
| :------------ | -------------- | ---------------------------------------- |
| mysql         | 5.5+           |                                          |
| elasticsearch | 2.3.3          | 未测试5.x版本（开发的时候最新版本只有2.3.x），需要假设sql引擎，见:https://github.com/NLPchina/elasticsearch-sql/ |
| kafka         | 0.10.0.1       | 如果spark的版本较低，那么需要将kafka的日志的格式降低，具体在kafka的配置项加入：log.message.format.version=0.8.2，该项按需配置 |
| jdk           | 1.7+           |                                          |
| zookeeper     | 3.4.6          |                                          |
| rabbitmq      | 3.5.7          |                                          |
| hbase         | 1.0.0-cdh5.4.0 | 不支持1.x以下的版本，比如0.9x.x                     |
| gradle        | 3.0            |                                          |
| hadoop        | 2.6.0-cdh5.4.0 |                                          |
| spark         | 1.3.0-cdh5.4.0 |                                          |
| redis         | 3.x            | 单机版即可                                    |

### 初始化

### mysql

```shell
mysql -uroot -p
source skyeye-data/skyeye-data-jpa/src/main/resources/sql/init.sql
```

### hbase

创建三张表，用来保存rpc的数据（一张数据表，两张二级索引表）

```Shell
hbase shell
执行skyeye-collector/src/main/resources/shell/hbase/hbase这个文件里面的内容
```

### elasticsearch

首先安装相应的es python的module，然后再创建索引，根据需要修改es的的ip、端口

```shell
cd skyeye-collector/src/main/resources/shell/es/
./install.sh
cd app-log
bash start.sh app-log http://192.168.xx.xx:9200,http://192.168.xx.xx:9200,......
cd event-log
bash start.sh event-log http://192.168.xx.xx:9200,http://192.168.xx.xx:9200,......
```

### kafka

创建相应的topic，根据需要修改—partitions和zk的ip、端口的值，如果日志量特别大可以适当提高这个值

```Shell
kafka-topics.sh --create --zookeeper 192.168.xx.xx:2181,192.168.xx.xx:2181,192.168.xx.xx:2181/kafka/0.10.0.1 --replication-factor 3 --partitions 9 --topic app-log
```

### zookeeper

初始化注册中心的节点信息

```shell
./zkCli.sh
执行skyeye-monitor/src/main/resources/shell/zk这个文件里面的内容
```

### rabbitmq

相关项目启动的时候会自动创建相关的队列

## skyeye-alarm

### 配置文件

配置文件外部化，需要在机器上创建配置文件

```shell
ssh 到部署节点
mkdir -p /opt/jthink/jthink-config/skyeye/alarm
vim alarm.properties

# log_mailer request queue
rabbit.request.addresses=localhost:5672
rabbit.request.username=jthink
rabbit.request.password=jthink
rabbit.request.vhost=/dev
rabbit.request.channelCacheSize=50
rabbit.request.queue=log_mailer
rabbit.request.exchange=direct.log
rabbit.request.routingKey=log.key

# mail
mail.jthink.smtphost=smtp.xxx.com
mail.jthink.port=25
mail.jthink.from=xxx@xxx.com
mail.jthink.cc=xxx@xxx.com
mail.jthink.password=jthink_0926
```

需要修改rabbitmq和邮件相关的配置

### 打包部署

```shell
cd skyeye-alarm
gradle clean distZip -x test
cd target/distributions
unzip skyeye-alarm-x.x.x.zip(替换相应的x为自己的版本)

cd skyeye-alarm-x.x.x
nohup bin/skyeye-alarm &
```

## skyeye-collector

### 配置文件

配置文件外部化，需要在机器上创建配置文件，根据对接系统的个数和产生日志的量进行部署，最好部署3个节点（每个节点消费3个partition的数据）

```shell
ssh 到部署节点
mkdir -p /opt/jthink/jthink-config/skyeye/collector
vim collector.properties

# kafka config
kafka.brokers=riot01:9092,riot02:9092,riot03:9092
kafka.topic=app-log
kafka.group.indexer=es-indexer-consume-group
kafka.poll.timeout=100
kafka.group.collect=info-collect-consume-group
kafka.group.backup=log-backup-consume-group
kafka.group.rpc.trace=rpc-trace-consume-group
kafka.hdfs.file.root=/tmp/monitor-center/
kafka.hdfs.file.server.id=0                					# 如果部署多个节点，第一个节点值为0，第二个节点就是1，第三个节点是2，以此类推

# es config
es.ips=riot01,riot02,riot03
es.cluster=mondeo					# 需要修改成搭建es的时候那个值
es.port=9300
es.sniff=true
es.index=app-log
es.doc=log
es.index.event=event-log
es.doc.event=log

# redis config
redis.host=localhost
redis.port=6379
redis.password=

# mysql config
database.address=localhost:3306
database.name=monitor-center
database.username=root
database.password=root

# log_mailer request queue
rabbit.request.addresses=localhost:5672
rabbit.request.username=jthink
rabbit.request.password=jthink
rabbit.request.vhost=/dev
rabbit.request.channelCacheSize=50
rabbit.request.queue=log_mailer
rabbit.request.exchange=direct.log
rabbit.request.routingKey=log.key

# zk
zookeeper.zkServers=riot01:2181,riot02:2181,riot03:2181
zookeeper.sessionTimeout=60000
zookeeper.connectionTimeout=5000

# hdfs
hadoop.hdfs.namenode.port=8020
hadoop.hdfs.namenode.host=192.168.88.131
hadoop.hdfs.user=qianjicheng
hadoop.hdfs.baseDir=/user/qianjicheng/JThink/
upload.log.cron=0 30 0 * * ?							# 按需修改，每天零点30分上传前一天的日志到hdfs，建议不改

# hbase config
hbase.quorum=panda-01
```

需要修改相关的配置，注释过的是要注意的，别的ip和端口根据需要进行修改（rabbitmq的配置需和alarm一致）

### 打包部署

多个节点部署需要部署多次

```shell
cd skyeye-collector
gradle clean distZip -x test
cd target/distributions
unzip skyeye-collector-x.x.x.zip(替换相应的x为自己的版本)

cd skyeye-collector-x.x.x
nohup bin/skyeye-collector &
```
## skyeye-monitor

### 配置文件

配置文件外部化，需要在机器上创建配置文件

```shell
ssh 到部署节点
mkdir -p /opt/jthink/jthink-config/skyeye/monitor
vim monitor.properties

# zk
zookeeper.zkServers=riot01:2181,riot02:2181,riot03:2181
zookeeper.sessionTimeout=60000
zookeeper.connectionTimeout=5000
zookeeper.baseSleepTimeMs=1000
zookeeper.maxRetries=3

# log_mailer request queue
rabbit.request.addresses=localhost:5672
rabbit.request.username=jthink
rabbit.request.password=jthink
rabbit.request.vhost=/dev
rabbit.request.channelCacheSize=50
rabbit.request.queue=log_mailer
rabbit.request.exchange=direct.log
rabbit.request.routingKey=log.key

# mysql config
database.address=localhost:3306
database.name=monitor-center
database.username=root
database.password=root
```

需要修改相关的配置（rabbitmq的配置需和alarm一致，zk也需要前后一致）

### 打包部署

```shell
cd skyeye-monitor
gradle clean distZip -x test
cd target/distributions
unzip skyeye-monitor-x.x.x.zip(替换相应的x为自己的版本)

cd skyeye-monitor-x.x.x
nohup bin/skyeye-monitor &
```
## skyeye-web

### 配置文件

配置文件外部化，需要在机器上创建配置文件

```shell
ssh 到部署节点
mkdir -p /opt/jthink/jthink-config/skyeye/web
vim web.properties

# server
serverAddress=0.0.0.0
serverPort=8090

# mysql config
database.address=localhost:3306
database.name=monitor-center
database.username=root
database.password=root

# es sql url
es.sql.url=http://riot01:9200/_sql?sql=
es.sql.sql=select * from app-log/log
es.query.delay=10
es.sql.index.event=event-log/log

# log_mailer request queue
rabbit.request.addresses=localhost:5672
rabbit.request.username=jthink
rabbit.request.password=jthink
rabbit.request.vhost=/dev
rabbit.request.channelCacheSize=50
rabbit.request.queue=log_mailer
rabbit.request.exchange=direct.log
rabbit.request.routingKey=log.key

# monitor
monitor.es.window=*/10 * * * * ?					# 监控代码执行的周期，建议不修改
monitor.es.mail=leviqian@sina.com
monitor.es.interval=10								# 采集多久之前的数据进行分析（单位：分钟），建议按需修改
monitor.es.middlewareResponseTime=1000				# 中间件操作耗时大于多少（毫秒），建议根据实际报警情况来定，防止出现报警风暴(比如大于5秒，这个值就要设置5000)
monitor.es.middlewareThreshold=0.1					# 中间件的报警阈值（耗时大于 monitor.es.middlewareResponseTime 该值的比例大于该值），该值需要按照实际运行过程的情况不断得调节，防止出现报警风暴
monitor.es.apiResponseTime=1000
monitor.es.apiThreshold=0.1
monitor.es.thirdResponseTime=1000
monitor.es.thirdThreshold=0.1
```

需要修改相关的配置（rabbitmq的配置需和alarm一致，es也需要前后一致），注释过的是要注意的

### 打包部署

```shell
cd skyeye-web
gradle clean distZip -x test
cd target/distributions
unzip skyeye-web-x.x.x.zip(替换相应的x为自己的版本)

cd skyeye-web-x.x.x
nohup bin/skyeye-web &
```

# 交流方式
1. QQ群: 624054633
2. Email: leviqian@sina.com
3. blog: http://blog.csdn.net/jthink_
