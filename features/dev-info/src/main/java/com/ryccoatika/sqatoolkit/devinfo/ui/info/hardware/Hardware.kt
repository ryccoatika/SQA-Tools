package com.ryccoatika.sqatoolkit.devinfo.ui.info.hardware

import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import me.tatarka.inject.annotations.Inject

@Inject
internal class HardwareType : DevInfoType {
  override val id: String
    get() = DevInfoType.HARDWARE_ID

  override val order: Int
    get() = 2

  override val featureTitle: Int
    get() = R.string.di_text_hardware
}
