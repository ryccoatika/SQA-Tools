package com.ryccoatika.sqatoolkit.fillmemory.ui.home.floater

import androidx.compose.runtime.Immutable
import com.ryccoatika.sqatoolkit.fillmemory.core.model.FillMemory
import com.ryccoatika.sqatoolkit.fillmemory.core.model.MemoryUsage

@Immutable
internal data class FloaterFillMemoryViewState(
  val memoryUsage: MemoryUsage,
  val fillMemoryProgress: FillMemory.Progress?,
  val isClearingMemory: Boolean,
) {
  val isLoading: Boolean
    get() = isClearingMemory ||
      (
        fillMemoryProgress != null &&
          !fillMemoryProgress.isSuccess &&
          fillMemoryProgress.error == null
        )

  companion object {
    val Empty = FloaterFillMemoryViewState(
      memoryUsage = MemoryUsage.Empty,
      fillMemoryProgress = null,
      isClearingMemory = false,
    )
  }
}
