package com.jthink.skyeye.statistics.configuration

import com.jthink.skyeye.statistics.properties.TaskProperties
import org.apache.spark.{SparkConf, SparkContext}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}

/**
  * JThink@JThink
  *
  * @author JThink
  * @version 0.0.1
  */
@Configuration
class SparkConfiguration {

  @Autowired
  @transient
  var taskProperties: TaskProperties = _

  @Bean
  def sparkContext(): SparkContext = {
    val conf = new SparkConf().setAppName(this.taskProperties.getRpcJobName)
    val sc = new SparkContext(conf)
    sc
  }
}
