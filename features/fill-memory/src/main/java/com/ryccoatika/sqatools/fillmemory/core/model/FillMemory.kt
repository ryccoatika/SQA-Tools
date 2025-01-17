package com.ryccoatika.sqatools.fillmemory.core.model

data class FillMemory(
  val value: Double,
) {
  data class Progress(
    val progress: Float,
    val mbFilled: Double,
    val mbFill: Double,
    val isSuccess: Boolean,
    val error: Throwable?,
  )
}
