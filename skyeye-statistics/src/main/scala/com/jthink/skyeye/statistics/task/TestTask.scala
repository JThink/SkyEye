package com.jthink.skyeye.statistics.task

import com.jthink.skyeye.statistics.properties.TaskProperties
import org.apache.spark.SparkContext
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
  * JThink@JThink
  *
  * 算法具体逻辑
  *
  * @author JThink
  * @version 0.0.1
  */
@Component
class TestTask extends Task {

  private val LOGGER: Logger = LoggerFactory.getLogger(classOf[TestTask])

  @Autowired
  @transient
  var taskProperties: TaskProperties = _

  @Autowired
  @transient
  var sparkContext: SparkContext = _

  /**
    * 具体的业务逻辑
    */
  override def doTask(): Unit = {
    LOGGER.info("task test start")
    // TODO: 具体的业务逻辑
    LOGGER.info("task test stop")
  }

}
