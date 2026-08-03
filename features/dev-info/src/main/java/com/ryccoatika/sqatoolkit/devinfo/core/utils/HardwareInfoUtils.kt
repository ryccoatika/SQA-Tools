package com.ryccoatika.sqatoolkit.devinfo.core.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.GLES20
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.DisplayMetrics
import android.view.WindowManager
import com.ryccoatika.sqatoolkit.common.utils.or
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem
import java.io.File
import java.util.Locale
import kotlin.math.sqrt
import me.tatarka.inject.annotations.Inject

@Inject
internal class HardwareInfoUtils(
  private val context: Context,
) {
  fun getChipset(): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val mfr = runCatching { Build.SOC_MANUFACTURER }.getOrDefault("")
      val model = runCatching { Build.SOC_MODEL }.getOrDefault("")
      if (mfr.isNotBlank() || model.isNotBlank()) return "$mfr $model".trim()
    }
    return NativeHelper.getProp("ro.board.platform").or(Build.HARDWARE.or("")).or("-")
  }

  fun getArchitecture(): String = runCatching { Build.SUPPORTED_ABIS.firstOrNull() }.getOrNull().or("-")

  fun getSupportedAbis(): String =
    runCatching { Build.SUPPORTED_ABIS.joinToString(", ") }.getOrNull().or("-")

  fun getCoreCount(): Int = runCatching { Runtime.getRuntime().availableProcessors() }.getOrDefault(0)

  fun getCpuGovernor(): String =
    readFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor").or("-")

  fun getCpuFreqRange(): String = runCatching {
    val min = readFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq").toLongOrNull()
    val max = readFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq").toLongOrNull()
    if (min == null || max == null) return "-"
    "%.1f - %.1f GHz".format(Locale.US, min / 1_000_000.0, max / 1_000_000.0)
  }.getOrDefault("-")

  fun getRamTotal(): String = runCatching { formatBytes(memInfo().totalMem) }.getOrDefault("-")
  fun getRamAvailable(): String = runCatching { formatBytes(memInfo().availMem) }.getOrDefault("-")
  fun getRamThreshold(): String = runCatching { formatBytes(memInfo().threshold) }.getOrDefault("-")
  fun getMemoryClass(): String = runCatching { "${activityManager().memoryClass} MB" }.getOrDefault("-")
  fun getLargeMemoryClass(): String =
    runCatching { "${activityManager().largeMemoryClass} MB" }.getOrDefault("-")

  fun getInternalStorage(): String = statFsString(Environment.getDataDirectory().path)
  fun getSystemStorage(): String = statFsString(Environment.getRootDirectory().path)

  fun getResolution(): String = runCatching {
    val m = displayMetrics()
    "${m.widthPixels} x ${m.heightPixels}"
  }.getOrDefault("-")

  fun getDensity(): String = runCatching {
    val m = displayMetrics()
    "${m.densityDpi} dpi (${densityBucket(m.densityDpi)})"
  }.getOrDefault("-")

  fun getRefreshRate(): String {
    val rate = runCatching {
      @Suppress("DEPRECATION")
      (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.refreshRate
    }.getOrNull() ?: return "-"
    return "%.0f Hz".format(Locale.US, rate)
  }

  fun getScreenSize(): String = runCatching {
    val m = displayMetrics()
    val x = m.widthPixels / m.xdpi
    val y = m.heightPixels / m.ydpi
    val inches = sqrt((x * x + y * y).toDouble())
    "%.1f\"".format(Locale.US, inches)
  }.getOrDefault("-")

  fun isHdr(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
    return runCatching {
      @Suppress("DEPRECATION")
      (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        .defaultDisplay.hdrCapabilities?.supportedHdrTypes?.isNotEmpty() == true
    }.getOrDefault(false)
  }

  // battery
  private fun batteryIntent(): Intent? = runCatching {
    context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
  }.getOrNull()

  fun getBatteryTechnology(): String =
    batteryIntent()?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY).or("-")

  fun getBatteryHealth(): String = when (batteryIntent()?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)) {
    BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
    BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
    BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
    BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
    BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
    else -> "-"
  }

  fun getBatteryStatus(): String = when (batteryIntent()?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
    BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
    BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
    BatteryManager.BATTERY_STATUS_FULL -> "Full"
    BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
    else -> "-"
  }

  fun getBatteryCapacity(): String = runCatching {
    val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    val pct = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    if (pct in 0..100) "$pct%" else "-"
  }.getOrDefault("-")

  fun getBatteryVoltage(): String {
    val mv = batteryIntent()?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
    return if (mv > 0) "%.3f V".format(Locale.US, mv / 1000.0) else "-"
  }

  fun getBatteryTemperature(): String {
    val t = batteryIntent()?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
    return if (t > 0) "%.1f °C".format(Locale.US, t / 10.0) else "-"
  }

  // gpu (offscreen EGL query)
  fun getGpuItems(context: Context): List<Item> {
    val info = queryGpu()
    return listOf(
      RawTextItem(context.getString(R.string.di_label_gpu_vendor), info?.vendor.or("-")),
      RawTextItem(context.getString(R.string.di_label_gpu_renderer), info?.renderer.or("-")),
      RawTextItem(context.getString(R.string.di_label_gpu_version), info?.version.or("-")),
    )
  }

  private data class GpuStrings(val vendor: String, val renderer: String, val version: String)

  private fun queryGpu(): GpuStrings? = runCatching {
    val display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
    val ver = IntArray(2)
    EGL14.eglInitialize(display, ver, 0, ver, 1)
    val cfgAttrs = intArrayOf(
      EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
      EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
      EGL14.EGL_NONE,
    )
    val cfgs = arrayOfNulls<EGLConfig>(1)
    val numCfg = IntArray(1)
    EGL14.eglChooseConfig(display, cfgAttrs, 0, cfgs, 0, 1, numCfg, 0)
    val ctxAttrs = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
    val ctx = EGL14.eglCreateContext(display, cfgs[0], EGL14.EGL_NO_CONTEXT, ctxAttrs, 0)
    val pbAttrs = intArrayOf(EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_NONE)
    val surface = EGL14.eglCreatePbufferSurface(display, cfgs[0], pbAttrs, 0)
    EGL14.eglMakeCurrent(display, surface, surface, ctx)
    val result = GpuStrings(
      vendor = GLES20.glGetString(GLES20.GL_VENDOR).or("-"),
      renderer = GLES20.glGetString(GLES20.GL_RENDERER).or("-"),
      version = GLES20.glGetString(GLES20.GL_VERSION).or("-"),
    )
    EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
    EGL14.eglDestroySurface(display, surface)
    EGL14.eglDestroyContext(display, ctx)
    EGL14.eglTerminate(display)
    result
  }.getOrNull()

  private fun memInfo(): ActivityManager.MemoryInfo {
    val mi = ActivityManager.MemoryInfo()
    activityManager().getMemoryInfo(mi)
    return mi
  }

  private fun activityManager() = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

  @Suppress("DEPRECATION")
  private fun displayMetrics(): DisplayMetrics {
    val m = DisplayMetrics()
    (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(m)
    return m
  }

  private fun statFsString(path: String): String = runCatching {
    val sf = StatFs(path)
    val total = sf.blockCountLong * sf.blockSizeLong
    val free = sf.availableBlocksLong * sf.blockSizeLong
    "${formatBytes(free)} free / ${formatBytes(total)}"
  }.getOrDefault("-")

  private fun readFile(path: String): String =
    runCatching { File(path).readText().trim() }.getOrDefault("")

  private fun densityBucket(dpi: Int): String = when {
    dpi <= 120 -> "ldpi"
    dpi <= 160 -> "mdpi"
    dpi <= 240 -> "hdpi"
    dpi <= 320 -> "xhdpi"
    dpi <= 480 -> "xxhdpi"
    else -> "xxxhdpi"
  }

  private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "-"
    val gb = bytes / 1_073_741_824.0
    if (gb >= 1) return "%.2f GB".format(Locale.US, gb)
    val mb = bytes / 1_048_576.0
    return "%.0f MB".format(Locale.US, mb)
  }
}
