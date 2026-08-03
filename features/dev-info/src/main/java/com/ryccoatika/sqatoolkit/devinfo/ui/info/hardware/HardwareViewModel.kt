package com.ryccoatika.sqatoolkit.devinfo.ui.info.hardware

import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetHardwareInfo
import com.ryccoatika.sqatoolkit.devinfo.ui.info.common.InfoViewModel
import me.tatarka.inject.annotations.Inject

@Inject
internal class HardwareViewModel(
  private val getHardwareInfo: GetHardwareInfo,
) : InfoViewModel() {
  init {
    refresh()
  }

  override suspend fun load(): List<Item> = getHardwareInfo.executeSync(Unit)
}
