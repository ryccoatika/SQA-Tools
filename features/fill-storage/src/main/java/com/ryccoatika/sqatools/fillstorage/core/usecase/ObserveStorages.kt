package com.ryccoatika.sqatools.fillstorage.core.usecase

import com.ryccoatika.sqatools.common.SubjectInteractor
import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.core.utils.StorageHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject

@Inject
internal class ObserveStorages(
  private val storageHelper: StorageHelper,
) : SubjectInteractor<Unit, List<Storage>>() {
  override fun createObservable(params: Unit): Flow<List<Storage>> {
    return flowOf(
      storageHelper.getAllStorageCapacity(),
    )
  }
}
