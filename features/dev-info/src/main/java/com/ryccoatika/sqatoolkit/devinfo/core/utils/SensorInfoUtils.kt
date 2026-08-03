package com.ryccoatika.sqatoolkit.devinfo.core.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.ryccoatika.sqatoolkit.common.utils.or
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.ExpandableGroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem
import java.util.Locale
import me.tatarka.inject.annotations.Inject

@Inject
internal class SensorInfoUtils(
  private val context: Context,
) {
  private fun s(id: Int) = context.getString(id)

  fun getSensors(): List<ExpandableGroupItem> = runCatching {
    val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    sm.getSensorList(Sensor.TYPE_ALL).map { sensor ->
      ExpandableGroupItem(
        title = sensor.name.or("-"),
        summary = sensor.vendor.or("-"),
        items = listOf(
          RawTextItem(s(R.string.di_label_sensor_vendor), sensor.vendor.or("-")),
          RawTextItem(s(R.string.di_label_sensor_type), sensorType(sensor)),
          RawTextItem(s(R.string.di_label_sensor_version), sensor.version.toString()),
          RawTextItem(s(R.string.di_label_sensor_power), "%.2f mA".format(Locale.US, sensor.power)),
          RawTextItem(s(R.string.di_label_sensor_resolution), sensor.resolution.toString()),
          RawTextItem(s(R.string.di_label_sensor_range), sensor.maximumRange.toString()),
          RawTextItem(s(R.string.di_label_sensor_min_delay), "${sensor.minDelay} µs"),
          RawTextItem(s(R.string.di_label_sensor_max_delay), "${sensor.maxDelay} µs"),
          RawTextItem(s(R.string.di_label_sensor_reporting), reportingMode(sensor)),
          RawTextItem(s(R.string.di_label_sensor_wakeup), yn(sensor.isWakeUpSensor)),
        ),
      )
    }
  }.getOrDefault(emptyList())

  private fun sensorType(sensor: Sensor): String = sensor.stringType.or("-")

  private fun reportingMode(sensor: Sensor): String = when (sensor.reportingMode) {
    Sensor.REPORTING_MODE_CONTINUOUS -> "Continuous"
    Sensor.REPORTING_MODE_ON_CHANGE -> "On Change"
    Sensor.REPORTING_MODE_ONE_SHOT -> "One Shot"
    Sensor.REPORTING_MODE_SPECIAL_TRIGGER -> "Special Trigger"
    else -> "-"
  }

  private fun yn(v: Boolean) = if (v) s(R.string.di_text_yes) else s(R.string.di_text_no)
}
