package com.ryccoatika.sqatools.fillmemory.core.usecase

import com.ryccoatika.sqatools.common.ProgressInteractor
import com.ryccoatika.sqatools.fillmemory.core.model.FillMemory as FillMemoryModel
import com.ryccoatika.sqatools.fillmemory.core.utils.MemoryHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
internal class FillMemory(
  private val memoryHelper: MemoryHelper,
) : ProgressInteractor<FillMemory.Params, FillMemoryModel.Progress>() {

  override fun doWork(params: Params, progressEmitter: (FillMemoryModel.Progress) -> Unit): Job = CoroutineScope(Dispatchers.IO).launch {
    memoryHelper.fillMemory(
      fillMemory = params.fillMemory,
      onProgress = { progressEmitter(it) },
    )
  }

  data class Params(
    val fillMemory: FillMemoryModel,
  )
}
