package com.ryccoatika.sqatools.devinfo.ui.info

import androidx.annotation.StringRes

internal interface DevInfoType {
  val id: String

  val order: Int

  @get:StringRes
  val featureTitle: Int

  companion object {
    const val BASIC_DEVICE_ID = "BASIC_DEVICE_INFORMATION"
    const val HARDWARE_ID = "HARDWARE_INFORMATION"
    const val NETWORK_ID = "NETWORK_INFORMATION"
    const val SOFTWARE_ID = "SOFTWARE_INFORMATION"
    const val CAMERA_ID = "CAMERA_INFORMATION"
    const val CONNECTIVITY_ID = "CONNECTIVITY_INFORMATION"
    const val SENSOR_ID = "SENSOR_INFORMATION"
    const val ADVANCE_DEVICE_ID = "ADVANCE_DEVICE_INFORMATION"
  }
}
