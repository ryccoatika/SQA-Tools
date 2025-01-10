package com.ryccoatika.sqatools.fillstorage.core.usecase

import com.ryccoatika.sqatools.common.ResultInteractor
import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.core.utils.StorageHelper
import me.tatarka.inject.annotations.Inject

@Inject
internal class GetStorage(
  private val storageHelper: StorageHelper,
) : ResultInteractor<GetStorage.Params, Storage>() {
  override suspend fun doWork(params: Params): Storage {
    return storageHelper.getStorageCapacity(params.path)
  }

  data class Params(
    val path: String,
  )
}
