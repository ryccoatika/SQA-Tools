package com.ryccoatika.sqatoolkit.devinfo.ui.info.camera

import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetCameraInfo
import com.ryccoatika.sqatoolkit.devinfo.ui.info.common.InfoViewModel
import me.tatarka.inject.annotations.Inject

@Inject
internal class CameraViewModel(
  private val getCameraInfo: GetCameraInfo,
) : InfoViewModel() {
  init {
    refresh()
  }

  override suspend fun load(): List<Item> = getCameraInfo.executeSync(Unit)
}
