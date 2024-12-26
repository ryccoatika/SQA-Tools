package com.ryccoatika.sqatools.fillstorage.core.usecase

import com.ryccoatika.sqatools.common.SubjectInteractor
import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.core.utils.StorageHelper
import com.ryccoatika.sqatools.fillstorage.di.FillStorageScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject

@FillStorageScope
@Inject
internal class ObserveStorages(
  private val storageHelper: StorageHelper,
) : SubjectInteractor<ObserveStorages.Params, List<Storage>>() {

  override fun createObservable(params: Params): Flow<List<Storage>> {
    return flowOf(
      storageHelper.getAllStorageCapacity(params.metrics),
    )
  }

  data class Params(
    val metrics: Storage.Metrics,
  )
}
