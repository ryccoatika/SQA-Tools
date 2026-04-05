package com.ryccoatika.sqatoolkit.fillstorage.core.model

internal data class FillStorage(
  val value: Double,
  val type: Type,
) {
  enum class Type {
    MB,
    GB,
    PERCENT,
  }

  data class Progress(
    val progress: Float,
    val mbFilled: Double,
    val mbFill: Double,
    val isSuccess: Boolean,
    val error: Throwable?,
  )
}
