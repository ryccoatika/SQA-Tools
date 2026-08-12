package com.ryccoatika.sqatoolkit.fillmemory.core.usecase

import com.ryccoatika.sqatoolkit.common.SubjectInteractor
import com.ryccoatika.sqatoolkit.fillmemory.core.model.MemoryUsage
import com.ryccoatika.sqatoolkit.fillmemory.core.utils.MemoryHelper
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
internal class ObserveMemoryUsage(
  private val memoryHelper: MemoryHelper,
) : SubjectInteractor<Unit, MemoryUsage>() {
  override fun createObservable(params: Unit): Flow<MemoryUsage> {
    return memoryHelper.observeMemoryUsage()
  }
}
