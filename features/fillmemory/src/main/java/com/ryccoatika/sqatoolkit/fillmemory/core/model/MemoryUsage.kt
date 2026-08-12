package com.ryccoatika.sqatoolkit.fillmemory.core.model

import java.math.BigDecimal
import java.math.RoundingMode

data class MemoryUsage(
  val totalMemory: BigDecimal,
  val availableMemory: BigDecimal,
  val dummyMemory: BigDecimal,
) {
  val usedMemory: BigDecimal
    get() = totalMemory - availableMemory

  val usedMemoryPercent: Int
    get() = try {
      (usedMemory.divide(totalMemory, 3, RoundingMode.HALF_UP).toFloat() * 100).toInt()
    } catch (_: ArithmeticException) {
      0
    }

  val totalMemoryInMB: BigDecimal
    get() = totalMemory / 1024 / 1024

  val availableMemoryInMB: BigDecimal
    get() = availableMemory / 1024 / 1024

  val usedMemoryInMB: BigDecimal
    get() = usedMemory / 1024 / 1024

  val dummyMemoryInMB: BigDecimal
    get() = dummyMemory / 1024 / 1024

  private operator fun BigDecimal.div(other: Int): BigDecimal {
    return this.divide(BigDecimal(other), 2, RoundingMode.HALF_UP)
  }

  companion object {
    val Empty = MemoryUsage(
      totalMemory = BigDecimal.ZERO,
      availableMemory = BigDecimal.ZERO,
      dummyMemory = BigDecimal.ZERO,
    )
  }
}
