package com.ryccoatika.sqatools.fillmemory.core.usecase

import com.ryccoatika.sqatools.common.Interactor
import com.ryccoatika.sqatools.fillmemory.core.utils.MemoryHelper
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
