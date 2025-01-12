package com.ryccoatika.sqatools.fillstorage.core.usecase

import com.ryccoatika.sqatools.common.Interactor
import com.ryccoatika.sqatools.fillstorage.core.model.DummyFile
import com.ryccoatika.sqatools.fillstorage.core.utils.StorageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
internal class DeleteFile(
  private val storageHelper: StorageHelper,
) : Interactor<DeleteFile.Params>() {

  override suspend fun doWork(params: Params) = withContext(Dispatchers.IO) {
    when (params) {
      is Params.AllFiles -> storageHelper.deleteAllDummyFiles(params.path)
      is Params.SingleFile -> storageHelper.deleteFile(params.dummyFile.path)
    }
  }

  sealed class Params {
    data class AllFiles(val path: String) : Params()
    data class SingleFile(val dummyFile: DummyFile) : Params()
  }
}
