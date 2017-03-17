# 项目介绍
监控组Hbase查询api封装
- 参考spring-data-hadoop-hbase-2.4.0.RELEASE版本的封装（使用hbase1.0.0的api）
- 封装成spring-boot-hbase-starter提供使用

# 版本对应
 brood-hbase版本 | hbase版本
:------  |:-----
 0.1.0  | 1.0.0-cdh5.4.0

# 使用方式
## 加入依赖
``` xml
compile "monitor-center:brood-hbase:0.1.0"
```
## 在application.properties中指定hbase的quorum
``` xml
spring.data.hbase.quorum=panda-01
```
