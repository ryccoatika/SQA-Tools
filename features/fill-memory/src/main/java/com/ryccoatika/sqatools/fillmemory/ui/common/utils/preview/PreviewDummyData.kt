@file:Suppress("ktlint:standard:filename")

package com.ryccoatika.sqatools.fillmemory.ui.common.utils.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.ryccoatika.sqatools.fillmemory.core.model.MemoryUsage

private fun memoryUsagesIncrease(total: Int = 100): List<MemoryUsage> = (1..total).map {
  MemoryUsage(
    totalMemory = total.toBigDecimal(),
    availableMemory = total.toBigDecimal() - it.toBigDecimal(),
    dummyMemory = 0.toBigDecimal(),
  )
}
private fun memoryUsagesDecrease(total: Int = 100): List<MemoryUsage> = (total downTo 1).map {
  MemoryUsage(
    totalMemory = total.toBigDecimal(),
    availableMemory = total.toBigDecimal() - it.toBigDecimal(),
    dummyMemory = 0.toBigDecimal(),
  )
}

internal class MemoryGraphParameterProvider : PreviewParameterProvider<List<MemoryUsage>> {
  override val values: Sequence<List<MemoryUsage>>
    get() = sequenceOf(
      memoryUsagesIncrease(),
      memoryUsagesDecrease(),
    )
}
