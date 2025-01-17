package com.ryccoatika.sqatools.fillmemory.ui.home

import androidx.compose.runtime.Immutable
import com.ryccoatika.sqatools.fillmemory.core.model.FillMemory
import com.ryccoatika.sqatools.fillmemory.core.model.MemoryUsage

@Immutable
data class HomeViewState(
  val history: List<MemoryUsage>,
  val memoryUsage: MemoryUsage,
  val fillMemoryProgress: FillMemory.Progress?,
  val isClearingMemory: Boolean,
) {
  val isLoading: Boolean
    get() = isClearingMemory || (fillMemoryProgress != null && !fillMemoryProgress.isSuccess)

  companion object {
    val Empty = HomeViewState(
      history = emptyList(),
      memoryUsage = MemoryUsage.Empty,
      fillMemoryProgress = null,
      isClearingMemory = false,
    )
  }
}
