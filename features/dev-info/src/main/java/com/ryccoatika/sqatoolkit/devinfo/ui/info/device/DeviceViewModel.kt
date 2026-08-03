package com.ryccoatika.sqatoolkit.devinfo.ui.info.device

import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetDeviceInfo
import com.ryccoatika.sqatoolkit.devinfo.ui.info.common.InfoViewModel
import me.tatarka.inject.annotations.Inject

@Inject
internal class DeviceViewModel(
  private val getDeviceInfo: GetDeviceInfo,
) : InfoViewModel() {
  init {
    refresh()
  }

  override suspend fun load(): List<Item> = getDeviceInfo.executeSync(Unit)
}
