# SkyEye
对java、scala等运行于jvm的程序进行实时日志采集、索引和可视化，对系统进行进程级别的监控，对系统内部的操作进行策略性的报警、对分布式的rpc调用进行trace跟踪以便于进行性能分析

# 交流方式

1. QQ群: 624054633
2. Email: leviqian@sina.com
3. blog: [blog](http://blog.csdn.net/jthink_)

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

# 项目介绍
对java、scala等运行于jvm的程序进行实时日志采集、索引和可视化，对系统进行进程级别的监控，对系统内部的操作进行策略性的报警、对分布式的rpc调用进行trace跟踪以便于进行性能分析

- 日志实时采集（支持log4j、logback和log4j2）
- 日志实时页面实时展示（支持关键字过滤）
- 历史日志查询（支持多种条件过滤，支持sql语句查询）
- app实时部署位置展示（机器和文件夹）
- app实时日志采集状态展示
- app历史部署位置展示
- api请求实时统计和历史统计
- 第三方请求实时统计和历史统计
- 基于dubbox的rpc调用数据收集和调用链展示（支持多种条件检索）
- 系统上下线报警
- 系统内嵌采集器报警
- 中间件、api、第三方、job执行异常报警（策略报警和异常报警）

# 部署步骤

修改根目录gradle文件中的私服地址（这样才能打包deploy到自己的本地私服）
打包：gradle clean install upload -x test

## 容器部署

需要自己修改每个项目下的image下的Dockerfile文件

PS: rancher一键部署skyeye后期出教程，基本符合持续交付的场景。

```shell
sudo bash build.sh 1.3.0 master
```

## skyeye-base

本项目没有具体的业务逻辑，主要是各个模块通用的类定义，如：常量、dto、dapper相关、公用util，所以该项目无需部署，只需要打包。

## skyeye-client

本项目主要是提供给对接的项目使用，包含了log4j和logback的自定义appender和项目注册相关，所以该项目无需部署，只需要打包提供给对接方对接。

## skyeye-data

本项目主要是用来提供和数据操作相关的中间件，具体分为以下5个子modoule。本项目无需部署，只需要打包。

### skyeye-data-dubbox

该项目主要是自定义的spring-boot的dubbox starter，为spring-boot相关的项目使用dubbox提供简易的方式并集成spring-boot的auto configuration，见我的另一个开源项目：[spring-boot-starter-dubbox](https://github.com/JThink/spring-boot-starter-dubbox)

### skyeye-data-hbase

该项目主要是自定义的spring-boot的hbase starter，为hbase的query和更新等操作提供简易的api并集成spring-boot的auto configuration，见我的另一个开源项目：[spring-boot-starter-hbase](https://github.com/JThink/spring-boot-starter-hbase)

### skyeye-data-httpl

该项目主要使用连接池简单封装了http的请求，如果项目中使用的spring版本较高可以使用RestTemplate代替。

### skyeye-data-jpa

该项目主要是jpa相关的定义，包含domain、repository、dto相关的定义，主要用来操作mysql的查询。

### skyeye-data-rabbitmq

该项目主要封装了报警模块中存取rabbitmq中消息的相关代码。

## skyeye-trace

该项目封装了所有rpc trace相关的代码，包含rpc数据采集器、分布式唯一ID生成、分布式递增ID生成、注册中心、采样器、跟踪器等功能，该项目无需部署，只需要打包。

### dubbox

由于使用dubbox，为了能够采集到dubbox里面的rpc数据，需要修改dubbox的源码，见我修改的dubbox项目：[dubbox](https://github.com/JThink/dubbox/tree/skyeye-trace-1.3.0)，该项目主要实现了rpc跟踪的具体实现，需要单独打包。

```shell
git clone https://github.com/JThink/dubbox.git
cd dubbox
git checkout skyeye-trace-1.3.0
修改相关pom中的私服地址
mvn clean install deploy -Dmaven.test.skip=true
```

## 软件安装

如果软件版本和以下所列不一致，需要修改gradle中的依赖版本，并且需自行测试可用性（hadoop、hbase、spark等相应的版本可以自己来指定，代码层面无需修改，需要修改依赖）。

| 软件名           | 版本             | 备注                                       |
| :------------ | -------------- | ---------------------------------------- |
| mysql         | 5.5+           |                                          |
| elasticsearch | 2.3.3          | 未测试5.x版本（开发的时候最新版本只有2.3.x），需要假设sql引擎，见: [elasticsearch-sql](https://github.com/NLPchina/elasticsearch-sql/)，需要安装IK分词并启动，见: [es ik分词](http://blog.csdn.net/jthink_/article/details/51878738) |
| kafka         | 0.10.0.1       | 如果spark的版本较低，那么需要将kafka的日志的格式降低，具体在kafka的配置项加入：log.message.format.version=0.8.2，该项按需配置 |
| jdk           | 1.7+           |                                          |
| zookeeper     | 3.4.6          |                                          |
| rabbitmq      | 3.5.7          |                                          |
| hbase         | 1.0.0-cdh5.4.0 | 不支持1.x以下的版本，比如0.9x.x                     |
| gradle        | 3.0+           |                                          |
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
执行skyeye-collector/skyeye-collector-trace/src/main/resources/shell/hbase这个文件里面的内容
```

### elasticsearch

首先安装相应的es python的module，然后再创建索引，根据需要修改es的的ip、端口

```shell
cd skyeye-collector/skyeye-collector-indexer/src/main/resources/shell
./install.sh
bash start.sh app-log http://192.168.xx.xx:9200,http://192.168.xx.xx:9200,......
cd skyeye-collector/skyeye-collector-metrics/src/main/resources/shell
bash start.sh event-log http://192.168.xx.xx:9200,http://192.168.xx.xx:9200,......

注意点：如果es版本为5.x，那么需要修改skyeye-collector/src/main/resources/shell/es/app-log/create-index.py的49和50行为下面内容：
'messageSmart': { 'type': 'text', 'analyzer': 'ik_smart', 'search_analyzer': 'ik_smart', 'include_in_all': 'true', 'boost': 8},
'messageMax': { 'type': 'text', 'analyzer': 'ik_max_word', 'search_analyzer': 'ik_max_word', 'include_in_all': 'true', 'boost': 8}
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

本项目从v1.0.0版本开始按不同的kafka消费group组织子module以实现可插拔的功能模块，主要包含如下5个module：

- skyeye-collector-core: 收集项目的所有公用的配置和公用代码，改module不需要部署
- skyeye-collector-backup: 对采集的所有日志进行备份
- skyeye-collector-indexer: 对采集的所有日志进行索引存入es
- kyeye-collector-metrics: 对事件日志进行meta  data的采集和相关报警metrics进行索引存入es
- skyeye-collector-trace: 对rpc跟踪数据进行采集入hbase

## 打包

```shell
cd skyeye-collector
gradle clean build -x test
```

### skyeye-collector-backup

#### 配置文件

配置文件外部化，需要在机器上创建配置文件，根据对接系统的个数和产生日志的量进行部署，最好部署3个节点（每个节点消费3个partition的数据）

```shell
ssh 到部署节点
mkdir -p /opt/jthink/jthink-config/skyeye/collector
vim collector-backup.properties

# kafka config
kafka.brokers=riot01:9092,riot02:9092,riot03:9092
kafka.topic=app-log
kafka.consume.group=log-backup-consume-group
kafka.poll.timeout=100

# hdfs
hadoop.hdfs.namenode.port=8020
hadoop.hdfs.namenode.host=192.168.88.131
hadoop.hdfs.user=xxx
hadoop.hdfs.baseDir=/user/xxx/JThink/
hadoop.hdfs.fileRoot=/tmp/monitor-center/
upload.log.cron=0 30 0 * * ?
```

### 部署

多个节点部署需要部署多次

```shell
cd skyeye-collector-backup/target/distributions
unzip skyeye-collector-backup-x.x.x.zip(替换相应的x为自己的版本)

cd skyeye-collector-backup-x.x.x
nohup bin/skyeye-collector-backup &
```
### skyeye-collector-indexer

#### 配置文件

配置文件外部化，需要在机器上创建配置文件，根据对接系统的个数和产生日志的量进行部署，最好部署3个节点（每个节点消费3个partition的数据）

```shell
ssh 到部署节点
mkdir -p /opt/jthink/jthink-config/skyeye/collector
vim collector-indexer.properties

# kafka config
kafka.brokers=riot01:9092,riot02:9092,riot03:9092
kafka.topic=app-log
kafka.consume.group=es-indexer-consume-group
kafka.poll.timeout=100

# es config
es.ips=riot01,riot02,riot03
es.cluster=mondeo
es.port=9300
es.sniff=true
es.index=app-log
es.doc=log
```

### 部署

多个节点部署需要部署多次

```shell
cd skyeye-collector-indexer/target/distributions
unzip skyeye-collector-indexer-x.x.x.zip(替换相应的x为自己的版本)

cd skyeye-collector-indexer-x.x.x
nohup bin/skyeye-collector-indexer &
```

### skyeye-collector-metrics

#### 配置文件

配置文件外部化，需要在机器上创建配置文件，根据对接系统的个数和产生日志的量进行部署，最好部署3个节点（每个节点消费3个partition的数据）

```shell
ssh 到部署节点
mkdir -p /opt/jthink/jthink-config/skyeye/collector
vim collector-metrics.properties

# kafka config
kafka.brokers=riot01:9092,riot02:9092,riot03:9092
kafka.topic=app-log
kafka.consume.group=info-collect-consume-group
kafka.poll.timeout=100

# es config
es.ips=riot01,riot02,riot03
es.cluster=mondeo
es.port=9300
es.sniff=true
es.index=event-log
es.doc=log

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
```

### 部署

多个节点部署需要部署多次

```shell
cd skyeye-collector-metrics/target/distributions
unzip skyeye-collector-metrics-x.x.x.zip(替换相应的x为自己的版本)

cd skyeye-collector-metrics-x.x.x
nohup bin/skyeye-collector-metrics &
```

### skyeye-collector-trace

#### 配置文件

配置文件外部化，需要在机器上创建配置文件，根据对接系统的个数和产生日志的量进行部署，最好部署3个节点（每个节点消费3个partition的数据）

```shell
ssh 到部署节点
mkdir -p /opt/jthink/jthink-config/skyeye/collector
vim collector-trace.properties

# kafka config
kafka.brokers=riot01:9092,riot02:9092,riot03:9092
kafka.topic=app-log
kafka.consume.group=rpc-trace-consume-group
kafka.poll.timeout=100

# redis config
redis.host=localhost
redis.port=6379
redis.password=

# mysql config
database.address=localhost:3306
database.name=monitor-center
database.username=root
database.password=root

# hbase config
hbase.quorum=panda-01,panda-01,panda-03
hbase.rootDir=hdfs://panda-01:8020/hbase
hbase.zookeeper.znode.parent=/hbase
```

### 部署

多个节点部署需要部署多次

```shell
cd skyeye-collector-trace/target/distributions
unzip skyeye-collectortracemetrics-x.x.x.zip(替换相应的x为自己的版本)

cd skyeye-collector-trace-x.x.x
nohup bin/skyeye-collector-trace &
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
monitor.es.interval=0 */1 * * * ?					# 监控代码执行的周期，建议不修改
monitor.es.mail=leviqian@sina.com

# hbase config
hbase.quorum=panda-01,panda-01,panda-03
hbase.rootDir=hdfs://panda-01:8020/hbase
hbase.zookeeper.znode.parent=/hbase
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

# 项目对接

需要进行日志采集的项目需要按照如下操作

## logback
### 依赖
gradle或者pom中加入skyeye-client的依赖

``` xml
compile "skyeye:skyeye-client-logback:1.3.0"
```
### 配置
在logback.xml中加入一个kafkaAppender，并在properties中配置好相关的值，如下（rpc这个项目前支持none和dubbo，所以如果项目中有dubbo服务的配置成dubbo，没有dubbo服务的配置成none，以后会支持其他的rpc框架，如：thrift、spring cloud等）：

``` xml
<property name="APP_NAME" value="your-app-name" />
<!-- kafka appender -->
<appender name="kafkaAppender" class="com.jthink.skyeye.client.logback.appender.KafkaAppender">
    <encoder class="com.jthink.skyeye.client.logback.encoder.KafkaLayoutEncoder">
      <layout class="ch.qos.logback.classic.PatternLayout">
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS};${CONTEXT_NAME};HOSTNAME;%thread;%-5level;%logger{96};%line;%msg%n</pattern>
      </layout>
    </encoder>
    <topic>app-log</topic>
    <rpc>none</rpc>
    <zkServers>riot01.jthink.com:2181,riot02.jthink.com:2181,riot03.jthink.com:2181</zkServers>
    <mail>xxx@xxx.com</mail>
    <keyBuilder class="com.jthink.skyeye.client.logback.builder.AppHostKeyBuilder" />

    <config>bootstrap.servers=riot01.jthink.com:9092,riot02.jthink.com:9092,riot03.jthink.com:9092</config>
    <config>acks=0</config>
    <config>linger.ms=100</config>
    <config>max.block.ms=5000</config>
  </appender>
```
## log4j
### 依赖
gradle或者pom中加入skyeye-client的依赖

``` xml
compile "skyeye:skyeye-client-log4j:1.3.0"
```
### 配置
在log4j.xml中加入一个kafkaAppender，并在properties中配置好相关的值，如下（rpc这个项目前支持none和dubbo，所以如果项目中有dubbo服务的配置成dubbo，没有dubbo服务的配置成none，以后会支持其他的rpc框架，如：thrift、spring cloud等）：

``` xml
<appender name="kafkaAppender" class="com.jthink.skyeye.client.log4j.appender.KafkaAppender">
        <param name="topic" value="app-log"/>
        <param name="zkServers" value="riot01.jthink.com:2181,riot02.jthink.com:2181,riot03.jthink.com:2181"/>
        <param name="app" value="xxx"/>
        <param name="rpc" value="dubbo"/>
        <param name="mail" value="xxx@xxx.com"/>
        <param name="bootstrapServers" value="riot01.jthink.com:9092,riot02.jthink.com:9092,riot03.jthink.com:9092"/>
        <param name="acks" value="0"/>
        <param name="maxBlockMs" value="2000"/>
        <param name="lingerMs" value="100"/>

        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d{yyyy-MM-dd HH:mm:ss.SSS};APP_NAME;HOSTNAME;%t;%p;%c;%L;%m%n"/>
        </layout>
    </appender>
```
## log4j2

### 依赖

gradle或者pom中加入skyeye-client的依赖

``` xml
compile "skyeye:skyeye-client-log4j2:1.3.0"
```

### 配置

在log4j2.xml中加入一个KafkaCustomize，并在properties中配置好相关的值，如下（rpc这个项目前支持none和dubbo，所以如果项目中有dubbo服务的配置成dubbo，没有dubbo服务的配置成none，以后会支持其他的rpc框架，如：thrift、spring cloud等）：

```xml
<KafkaCustomize name="KafkaCustomize" topic="app-log" zkServers="riot01.jthink.com:2181,riot02.jthink.com:2181,riot03.jthink.com:2181"
                mail="qianjc@unionpaysmart.com" rpc="none" app="${APP_NAME}">
  <ThresholdFilter level="info" onMatch="ACCEPT" onMismatch="DENY"/>
  <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS};${APP_NAME};HOSTNAME;%t;%-5level;%logger{96};%line;%msg%n"/>
  <Property name="bootstrap.servers">riot01.jthink.com:9092,riot02.jthink.com:9092,riot03.jthink.com:9092</Property>
  <Property name="acks">0</Property>
  <Property name="linger.ms">100</Property>
</KafkaCustomize>
```

## 注意点

## logback
- logback在对接kafka的时候有个bug，[jira bug](https://jira.qos.ch/browse/LOGBACK-1328)，所以需要将root level设置为INFO（不能是DEBUG）

### log4j
由于log4j本身的appender比较复杂难写，所以在稳定性和性能上没有logback支持得好，应用能使用logback请尽量使用logback
### rpc trace
使用自己打包的dubbox（[dubbox](https://github.com/JThink/dubbox/tree/skyeye-trace-1.3.0)），在soa中间件dubbox中封装了rpc的跟踪

``` shell
compile "com.101tec:zkclient:0.10"
compile ("com.alibaba:dubbo:2.8.4-skyeye-trace-1.3.0") {
  exclude group: 'org.springframework', module: 'spring'
}
```
### spring boot

如果项目使用的是spring-boot+logback，那么需要将spring-boot对logback的初始化去掉，防止初始化的时候在zk注册两次而报错，具体见我的几篇博客就可以解决：

http://blog.csdn.net/jthink_/article/details/52513963

http://blog.csdn.net/jthink_/article/details/52613953

http://blog.csdn.net/jthink_/article/details/73106745

## 埋点

### 日志类型
| 日志类型             | 说明                        |
| :--------------- | :------------------------ |
| normal           | 正常入库日志                    |
| invoke_interface | api调用日志                   |
| middleware_opt   | 中间件操作日志(目前仅支持hbase和mongo) |
| job_execute      | job执行日志                   |
| rpc_trace        | rpc trace跟踪日志             |
| custom_log       | 自定义埋点日志                   |
| thirdparty_call  | 第三方系统调用日志                 |
### 正常日志

``` shell
LOGGER.info("我是测试日志打印")
```
### api日志

``` shell
// 参数依次为EventType(事件类型)、api、账号、请求耗时、成功还是失败、具体自定义的日志内容
LOGGER.info(ApiLog.buildApiLog(EventType.invoke_interface, "/app/status", "800001", 100, EventLog.MONITOR_STATUS_SUCCESS, "我是mock api成功日志").toString());
LOGGER.info(ApiLog.buildApiLog(EventType.invoke_interface, "/app/status", "800001", 10, EventLog.MONITOR_STATUS_FAILED, "我是mock api失败日志").toString());
```
### 中间件日志

``` shell
// 参数依次为EventType(事件类型)、MiddleWare(中间件名称)、操作耗时、成功还是失败、具体自定义的日志内容
LOGGER.info(EventLog.buildEventLog(EventType.middleware_opt, MiddleWare.HBASE.symbol(), 100, EventLog.MONITOR_STATUS_SUCCESS, "我是mock middle ware成功日志").toString());
LOGGER.info(EventLog.buildEventLog(EventType.middleware_opt, MiddleWare.MONGO.symbol(), 10, EventLog.MONITOR_STATUS_FAILED, "我是mock middle ware失败日志").toString());
```
### job执行日志

```
// job执行仅仅处理失败的日志（成功的不做处理，所以只需要构造失败的日志）, 参数依次为EventType(事件类型)、job 的id号、操作耗时、失败、具体自定义的日志内容
LOGGER.info(EventLog.buildEventLog(EventType.job_execute, "application_1477705439920_0544", 10, EventLog.MONITOR_STATUS_FAILED, "我是mock job exec失败日志").toString());
```

### 第三方请求日志

```
// 参数依次为EventType(事件类型)、第三方名称、操作耗时、成功还是失败、具体自定义的日志内容
LOGGER.info(EventLog.buildEventLog(EventType.thirdparty_call, "xx1", 100, EventLog.MONITOR_STATUS_FAILED, "我是mock third 失败日志").toString());
LOGGER.info(EventLog.buildEventLog(EventType.thirdparty_call, "xx1", 100, EventLog.MONITOR_STATUS_SUCCESS, "我是mock third 成功日志").toString());
LOGGER.info(EventLog.buildEventLog(EventType.thirdparty_call, "xx2", 100, EventLog.MONITOR_STATUS_SUCCESS, "我是mock third 成功日志").toString());
LOGGER.info(EventLog.buildEventLog(EventType.thirdparty_call, "xx2", 100, EventLog.MONITOR_STATUS_FAILED, "我是mock third 失败日志").toString());
```
