package com.ryccoatika.sqatoolkit.devinfo.ui.info.connectivity

import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetConnectivityInfo
import com.ryccoatika.sqatoolkit.devinfo.ui.info.common.InfoViewModel
import me.tatarka.inject.annotations.Inject

@Inject
internal class ConnectivityViewModel(
  private val getConnectivityInfo: GetConnectivityInfo,
) : InfoViewModel() {
  init {
    refresh()
  }

  override suspend fun load(): List<Item> = getConnectivityInfo.executeSync(Unit)
}
