package com.ryccoatika.sqatools.fillstorage.core.usecase

import com.ryccoatika.sqatools.common.SubjectInteractor
import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.core.utils.StorageHelper
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
internal class ObserveStorage(
  private val storageHelper: StorageHelper,
) : SubjectInteractor<ObserveStorage.Params, Storage>() {
  override fun createObservable(params: Params): Flow<Storage> {
    return storageHelper.observeStorageCapacity(params.path)
  }

  data class Params(
    val path: String,
  )
}
