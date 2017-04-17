package com.jthink.skyeye.statistics.model.key.rpc

import com.jthink.skyeye.statistics.model.key.Key

/**
  * JThink@JThink
  *
  * 统计的key
  *
  * @author JThink
  * @version 0.0.1
  */
class TimeApiKey(_time: String, _api: String) extends Key {

  // time, 到秒的String
  var time: String = _time
  // api
  var api: String = _api

  def canEqual(other: Any): Boolean = other.isInstanceOf[TimeApiKey]

  override def equals(other: Any): Boolean = other match {
    case that: TimeApiKey =>
      (that canEqual this) &&
        time == that.time &&
        api == that.api
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(time, api)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString = s"TimeApiKey($time, $api)"
}
