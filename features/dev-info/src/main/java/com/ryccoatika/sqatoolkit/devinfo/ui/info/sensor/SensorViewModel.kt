package com.ryccoatika.sqatoolkit.devinfo.ui.info.sensor

import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetSensorInfo
import com.ryccoatika.sqatoolkit.devinfo.ui.info.common.InfoViewModel
import me.tatarka.inject.annotations.Inject

@Inject
internal class SensorViewModel(
  private val getSensorInfo: GetSensorInfo,
) : InfoViewModel() {
  init {
    refresh()
  }

  override suspend fun load(): List<Item> = getSensorInfo.executeSync(Unit)
}
