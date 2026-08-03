package com.ryccoatika.sqatoolkit.devinfo.core.usecase

import com.ryccoatika.sqatoolkit.common.ResultInteractor
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.utils.CameraInfoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
internal class GetCameraInfo(
  private val cameraInfoUtils: CameraInfoUtils,
) : ResultInteractor<Unit, List<Item>>() {
  override suspend fun doWork(params: Unit): List<Item> = withContext(Dispatchers.IO) {
    cameraInfoUtils.getCameras()
  }
}
