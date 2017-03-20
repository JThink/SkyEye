package com.jthink.skyeye.statistics.task

/**
  * JThink@JThink
  *
  * spark任务的父类
  * @author JThink
  * @version 0.0.1
  */
trait Task extends Serializable {

  def doTask()
}
