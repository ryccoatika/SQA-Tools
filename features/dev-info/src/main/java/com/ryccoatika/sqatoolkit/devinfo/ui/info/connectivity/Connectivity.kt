package com.ryccoatika.sqatoolkit.devinfo.ui.info.connectivity

import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import me.tatarka.inject.annotations.Inject

@Inject
internal class ConnectivityType : DevInfoType {
  override val id: String
    get() = DevInfoType.CONNECTIVITY_ID

  override val order: Int
    get() = 6

  override val featureTitle: Int
    get() = R.string.di_text_connectivity
}
