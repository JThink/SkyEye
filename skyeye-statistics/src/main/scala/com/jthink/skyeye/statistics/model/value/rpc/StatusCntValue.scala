package com.jthink.skyeye.statistics.model.value.rpc

import com.jthink.skyeye.statistics.model.value.Value

/**
  * JThink@JThink
  *
  * @author JThink
  * @version 0.0.1
  */
class StatusCntValue(_status: String, _cnt: Int) extends Value {

  // status
  var status: String = _status
  // 该状态统计的次数
  var cnt: Int = _cnt

  def canEqual(other: Any): Boolean = other.isInstanceOf[StatusCntValue]

  override def equals(other: Any): Boolean = other match {
    case that: StatusCntValue =>
      (that canEqual this) &&
        status == that.status &&
        cnt == that.cnt
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(status, cnt)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString = s"StatusCntValue($status, $cnt)"
}
