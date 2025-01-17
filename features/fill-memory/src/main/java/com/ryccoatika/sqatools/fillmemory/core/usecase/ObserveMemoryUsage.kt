package com.ryccoatika.sqatools.fillmemory.core.usecase

import com.ryccoatika.sqatools.common.SubjectInteractor
import com.ryccoatika.sqatools.fillmemory.core.model.MemoryUsage
import com.ryccoatika.sqatools.fillmemory.core.utils.MemoryHelper
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
