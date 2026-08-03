package com.ryccoatika.sqatoolkit.devinfo.ui.info.network

import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetNetworkInfo
import com.ryccoatika.sqatoolkit.devinfo.ui.info.common.InfoViewModel
import me.tatarka.inject.annotations.Inject

@Inject
internal class NetworkViewModel(
  private val getNetworkInfo: GetNetworkInfo,
) : InfoViewModel() {
  init {
    refresh()
  }

  override suspend fun load(): List<Item> = getNetworkInfo.executeSync(Unit)
}
