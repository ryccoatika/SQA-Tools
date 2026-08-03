package com.ryccoatika.sqatoolkit.devinfo.ui.info.network

import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import me.tatarka.inject.annotations.Inject

@Inject
internal class NetworkType : DevInfoType {
  override val id: String
    get() = DevInfoType.NETWORK_ID

  override val order: Int
    get() = 3

  override val featureTitle: Int
    get() = R.string.di_text_network
}
