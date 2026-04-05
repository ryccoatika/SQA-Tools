package com.ryccoatika.sqatoolkit.devinfo.ui.info.sensor

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import me.tatarka.inject.annotations.Inject

internal typealias Sensor = @Composable () -> Unit

@Inject
@Composable
internal fun Sensor() {
  Scaffold { padding ->
    Text(text = "Sensor", modifier = Modifier.padding(padding))
  }
}

@Inject
internal class SensorType : DevInfoType {
  override val id: String
    get() = DevInfoType.SENSOR_ID

  override val order: Int
    get() = 3

  override val featureTitle: Int
    get() = R.string.di_text_sensor
}
