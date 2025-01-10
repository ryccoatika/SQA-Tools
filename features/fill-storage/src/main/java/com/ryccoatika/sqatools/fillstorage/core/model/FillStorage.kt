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
}
