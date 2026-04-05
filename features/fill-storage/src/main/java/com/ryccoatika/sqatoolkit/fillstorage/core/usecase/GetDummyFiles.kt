package com.ryccoatika.sqatoolkit.fillstorage.core.usecase

import com.ryccoatika.sqatoolkit.common.ResultInteractor
import com.ryccoatika.sqatoolkit.fillstorage.core.model.DummyFile
import com.ryccoatika.sqatoolkit.fillstorage.core.utils.StorageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
internal class GetDummyFiles(
  private val storageHelper: StorageHelper,
) : ResultInteractor<GetDummyFiles.Params, List<DummyFile>>() {

  override suspend fun doWork(params: Params): List<DummyFile> = withContext(Dispatchers.IO) {
    storageHelper.getDummyFilesPath(params.path).map { path ->
      DummyFile(
        name = path.name,
        path = path.absolutePath,
        sizeInMB = storageHelper.bytesToMB(path.length()),
      )
    }
  }

  data class Params(
    val path: String,
  )
}
