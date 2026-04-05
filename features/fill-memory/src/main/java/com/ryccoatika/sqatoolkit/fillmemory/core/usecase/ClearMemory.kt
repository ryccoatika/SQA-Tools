package com.ryccoatika.sqatoolkit.fillmemory.core.usecase

import com.ryccoatika.sqatoolkit.common.Interactor
import com.ryccoatika.sqatoolkit.fillmemory.core.utils.MemoryHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
internal class ClearMemory(
  private val memoryHelper: MemoryHelper,
) : Interactor<Unit>() {
  override suspend fun doWork(params: Unit) = withContext(Dispatchers.IO) {
    memoryHelper.clearAllMemory()
  }
}
