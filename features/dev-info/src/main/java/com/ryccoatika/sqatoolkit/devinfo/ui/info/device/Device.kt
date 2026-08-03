package com.ryccoatika.sqatoolkit.devinfo.ui.info.device

import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import me.tatarka.inject.annotations.Inject

@Inject
internal class DeviceType : DevInfoType {
  override val id: String
    get() = DevInfoType.DEVICE_ID

  override val order: Int
    get() = 1

  override val featureTitle: Int
    get() = R.string.di_text_device
}
