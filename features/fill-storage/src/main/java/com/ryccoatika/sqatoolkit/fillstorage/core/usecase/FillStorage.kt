package com.ryccoatika.sqatoolkit.fillstorage.core.usecase

import com.ryccoatika.sqatoolkit.common.ProgressInteractor
import com.ryccoatika.sqatoolkit.fillstorage.core.model.FillStorage as FillStorageModel
import com.ryccoatika.sqatoolkit.fillstorage.core.model.Storage
import com.ryccoatika.sqatoolkit.fillstorage.core.utils.StorageHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
internal class FillStorage(
  private val storageHelper: StorageHelper,
) : ProgressInteractor<FillStorage.Params, FillStorageModel.Progress>() {

  override fun doWork(params: Params, progressEmitter: (FillStorageModel.Progress) -> Unit): Job = CoroutineScope(Dispatchers.IO).launch {
    storageHelper.fillStorage(
      storage = params.storage,
      fillStorage = params.fillStorage,
      onProgress = { progressEmitter(it) },
    )
  }

  data class Params(
    val storage: Storage,
    val fillStorage: FillStorageModel,
  )
}
