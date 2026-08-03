package com.ryccoatika.sqatoolkit.devinfo.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.ui.graphics.vector.ImageVector
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType

internal fun tabIcon(id: String): ImageVector = when (id) {
  DevInfoType.DEVICE_ID -> Icons.Rounded.PhoneAndroid
  DevInfoType.HARDWARE_ID -> Icons.Rounded.Memory
  DevInfoType.NETWORK_ID -> Icons.Rounded.SignalCellularAlt
  DevInfoType.SOFTWARE_ID -> Icons.Rounded.Android
  DevInfoType.CAMERA_ID -> Icons.Rounded.PhotoCamera
  DevInfoType.CONNECTIVITY_ID -> Icons.Rounded.Wifi
  DevInfoType.SENSOR_ID -> Icons.Rounded.Sensors
  else -> Icons.Rounded.Info
}
