package com.jthink.skyeye.statistics.task

import com.alibaba.fastjson.JSON
import com.jthink.skyeye.base.constant.{Constants, EventType}
import com.jthink.skyeye.base.dapper.Span
import com.jthink.skyeye.base.dto.{EventLog, LogDto, RpcTraceLog}
import com.jthink.skyeye.statistics.properties.TaskProperties
import kafka.serializer.{DefaultDecoder, StringDecoder}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired

/**
  * JThink@JThink
  *
  * rpc跟踪数据的统计
  *
  * @author JThink
  * @version 0.0.1
  */
class RpcStatisticTask extends Task {

  private val LOGGER: Logger = LoggerFactory.getLogger(classOf[RpcStatisticTask])

  @Autowired
  @transient
  var taskProperties: TaskProperties = _

  override def doTask(): Unit = {
    LOGGER.info("start to statistic api")

//    val conf = new SparkConf().setAppName(this.taskProperties.getRpcJobName)
//    val ssc = new StreamingContext(conf, Seconds(1))
//
//    val kafkaConfig = Map(
//      Constants.KAFKA_GROUP_ID_CONFIG -> this.config.apiConsumeGroup,
//      Constants.KAFKA_BOOTSTRAP_SERVERS_CONFIG -> this.config.kafkaServers,
//      Constants.KAFKA_AUTO_OFFSET_RESET_CONFIG -> this.config.offsetReset
//    )
//
//    // 创建stream对象
//    val stream = KafkaUtils.createDirectStream[Array[Byte], String, DefaultDecoder, StringDecoder](
//      ssc,
//      kafkaConfig,
//      Set(this.config.topic)
//    )

    // 过滤出rpc_trace类型的日志事件并且该Span是采样数据，将日志数据转换为Span对象
//    val rpcTraceLog = stream.map(_._2).filter(this.isRpcTraceLog(_))
//
//    rpcTraceLog.map(_.)
  }

  /**
    * 过滤出rpc_trace类型的日志事件并且该Span是采样数据
    * @param line
    * @return
    */
  private def isRpcTraceLog(line: String): Boolean = {
    val logDto = new LogDto(line)
    if (null != logDto) {
      val log = logDto.getMessageMax
      val eventType = EventLog.parseEventType(log).symbol
      if (eventType.equals(EventType.rpc_trace.symbol())) {
        val rpcTraceLog: RpcTraceLog = RpcTraceLog.parseRpcTraceLog(log)
        val span: Span = JSON.parseObject(rpcTraceLog.getLog, classOf[Span])
        span.getSample
      }
    }
    false
  }

  /**
    * 将日志字符串转换为TODO
    * @param line
    */
  private def convertToKV(line: String): Span = {
    val logDto = new LogDto(line)
    val log: RpcTraceLog = RpcTraceLog.parseRpcTraceLog(logDto.getMessageMax)
    val span: Span = JSON.parseObject(log.getLog, classOf[Span])
    span
  }
}
