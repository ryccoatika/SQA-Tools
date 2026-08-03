package com.ryccoatika.sqatoolkit.devinfo.core.utils

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.MediaRecorder
import android.util.Range
import com.ryccoatika.sqatoolkit.common.utils.or
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.ExpandableGroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem
import java.util.Locale
import me.tatarka.inject.annotations.Inject

@Inject
internal class CameraInfoUtils(
  private val context: Context,
) {
  private fun s(id: Int) = context.getString(id)

  fun getCameras(): List<ExpandableGroupItem> = runCatching {
    val cm = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    cm.cameraIdList.mapNotNull { id -> buildCamera(cm, id) }
  }.getOrDefault(emptyList())

  private fun buildCamera(cm: CameraManager, id: String): ExpandableGroupItem? = runCatching {
    val c = cm.getCameraCharacteristics(id)
    val facing = when (c.get(CameraCharacteristics.LENS_FACING)) {
      CameraCharacteristics.LENS_FACING_FRONT -> s(R.string.di_camera_front)
      CameraCharacteristics.LENS_FACING_BACK -> s(R.string.di_camera_back)
      else -> s(R.string.di_camera_external)
    }
    val mp = c.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)?.let {
      "%.1f MP".format(Locale.US, (it.width.toLong() * it.height) / 1_000_000.0)
    }.or("-")
    val level = hardwareLevel(c.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL))
    val map = c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

    val items = listOf(
      RawTextItem(s(R.string.di_label_facing), facing),
      RawTextItem(s(R.string.di_label_hardware_level), level),
      RawTextItem(s(R.string.di_label_megapixels), mp),
      RawTextItem(s(R.string.di_label_sensor_size), sensorSize(c)),
      RawTextItem(
        s(R.string.di_label_focal_lengths),
        floatList(c.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS), "mm"),
      ),
      RawTextItem(
        s(R.string.di_label_apertures),
        floatList(c.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES), "f/"),
      ),
      RawTextItem(s(R.string.di_label_flash), yn(c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true)),
      RawTextItem(
        s(R.string.di_label_max_zoom),
        c.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)
          ?.let { "%.1fx".format(Locale.US, it) }.or("-"),
      ),
      RawTextItem(
        s(R.string.di_label_iso_range),
        rangeString(c.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE)),
      ),
      RawTextItem(s(R.string.di_label_max_photo), maxSize(map, ImageFormat.JPEG)),
      RawTextItem(s(R.string.di_label_max_video), maxVideoSize(map)),
      RawTextItem(s(R.string.di_label_ois), yn(hasOis(c))),
      RawTextItem(s(R.string.di_label_raw), yn(hasRaw(c))),
    )
    ExpandableGroupItem(
      title = "$facing ($id)",
      summary = "$mp · $level",
      items = items,
    )
  }.getOrNull()

  private fun hardwareLevel(v: Int?): String = when (v) {
    CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY -> "LEGACY"
    CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED -> "LIMITED"
    CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL -> "FULL"
    CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_3 -> "LEVEL_3"
    else -> "-"
  }

  private fun sensorSize(c: CameraCharacteristics): String {
    val sz = c.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE) ?: return "-"
    return "%.1f x %.1f mm".format(Locale.US, sz.width, sz.height)
  }

  private fun floatList(arr: FloatArray?, suffix: String): String {
    if (arr == null || arr.isEmpty()) return "-"
    return arr.joinToString(", ") {
      if (suffix == "f/") "f/%.1f".format(Locale.US, it) else "%.1f%s".format(Locale.US, it, suffix)
    }
  }

  private fun rangeString(r: Range<Int>?): String = r?.let { "${it.lower} - ${it.upper}" }.or("-")

  private fun maxSize(map: StreamConfigurationMap?, format: Int): String {
    val sizes = runCatching { map?.getOutputSizes(format) }.getOrNull() ?: return "-"
    val max = sizes.maxByOrNull { it.width.toLong() * it.height } ?: return "-"
    return "${max.width} x ${max.height}"
  }

  private fun maxVideoSize(map: StreamConfigurationMap?): String {
    val sizes = runCatching { map?.getOutputSizes(MediaRecorder::class.java) }.getOrNull() ?: return "-"
    val max = sizes.maxByOrNull { it.width.toLong() * it.height } ?: return "-"
    return "${max.width} x ${max.height}"
  }

  private fun hasOis(c: CameraCharacteristics): Boolean =
    c.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION)?.any { it != 0 } == true

  private fun hasRaw(c: CameraCharacteristics): Boolean =
    c.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
      ?.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW) == true

  private fun yn(v: Boolean) = if (v) s(R.string.di_text_yes) else s(R.string.di_text_no)
}
