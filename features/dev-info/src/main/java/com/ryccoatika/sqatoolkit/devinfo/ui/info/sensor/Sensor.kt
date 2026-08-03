package com.ryccoatika.sqatoolkit.devinfo.ui.info.sensor

import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import me.tatarka.inject.annotations.Inject

@Inject
internal class SensorType : DevInfoType {
  override val id: String
    get() = DevInfoType.SENSOR_ID

  override val order: Int
    get() = 7

  override val featureTitle: Int
    get() = R.string.di_text_sensor
}
