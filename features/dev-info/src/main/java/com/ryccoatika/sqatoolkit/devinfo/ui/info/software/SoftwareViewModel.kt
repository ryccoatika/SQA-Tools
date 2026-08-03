package com.ryccoatika.sqatoolkit.devinfo.ui.info.software

import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetSoftwareInfo
import com.ryccoatika.sqatoolkit.devinfo.ui.info.common.InfoViewModel
import me.tatarka.inject.annotations.Inject

@Inject
internal class SoftwareViewModel(
  private val getSoftwareInfo: GetSoftwareInfo,
) : InfoViewModel() {
  init {
    refresh()
  }

  override suspend fun load(): List<Item> = getSoftwareInfo.executeSync(Unit)
}
