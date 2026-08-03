package com.ryccoatika.sqatoolkit.devinfo.ui.info.software

import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import me.tatarka.inject.annotations.Inject

@Inject
internal class SoftwareType : DevInfoType {
  override val id: String
    get() = DevInfoType.SOFTWARE_ID

  override val order: Int
    get() = 4

  override val featureTitle: Int
    get() = R.string.di_text_software
}
