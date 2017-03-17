# 项目介绍
监控组数据抽象层

## 子项目介绍
- skyeye-data-hbase: 抽象出hbase（1.x的CDH版本新api）的Get、Put、Delete、Update等操作并制作成spring-boot的starter，并集成了中间件数据采集（即使用该module的项目无需单独再写接入中间件采集的代码）
- skyeye-data-http: 抽象出http的get操作提供给监控组所有的项目使用
- skyeye-data-jpa: jpa相关的操作
- skyeye-data-rabbitmq: 抽象出报警队列的读取和消费，提供给监控组多个项目使用