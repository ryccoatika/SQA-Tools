package com.ryccoatika.sqatools.fillstorage.core.model

internal data class FillStorage(
  val value: Float,
  val type: Type,
) {
  enum class Type {
    MB,
    GB,
    PERCENT,
  }

  data class Progress(
    val progress: Float,
    val mbFilled: Float,
    val mbFill: Float,
    val isSuccess: Boolean,
    val error: Throwable?,
  )
}
