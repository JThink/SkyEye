package com.jthink.skyeye.statistics.model.value.rpc

import com.jthink.skyeye.statistics.model.value.Value

/**
  * JThink@JThink
  *
  * @author JThink
  * @version 0.0.1
  */
class SuccessFailedCntValue(_successCnt: Int, _failedCnt: Int) extends Value {

  // 成功次数
  var successCnt: Int = _successCnt
  // 失败次数
  var failedCnt: Int = _failedCnt

  def canEqual(other: Any): Boolean = other.isInstanceOf[SuccessFailedCntValue]

  override def equals(other: Any): Boolean = other match {
    case that: SuccessFailedCntValue =>
      (that canEqual this) &&
        successCnt == that.successCnt &&
        failedCnt == that.failedCnt
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(successCnt, failedCnt)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }

  override def toString = s"SuccessFailedCntValue($successCnt, $failedCnt)"
}
