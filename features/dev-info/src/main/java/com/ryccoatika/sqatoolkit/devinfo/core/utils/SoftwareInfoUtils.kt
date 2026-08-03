package com.ryccoatika.sqatoolkit.devinfo.core.utils

import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaDrm
import android.os.Build
import android.os.SystemClock
import com.ryccoatika.sqatoolkit.common.utils.or
import java.time.Instant
import java.util.UUID
import me.tatarka.inject.annotations.Inject

@Inject
internal class SoftwareInfoUtils(
  private val context: Context,
  private val deviceInfoUtils: DeviceInfoUtils,
) {

  fun getDeviceReleaseAndroidVersion(): String {
    val apiLevel = NativeHelper.getProp("ro.product.first_api_level")
      .or("0")
      .toIntOrNull() ?: 0

    return deviceInfoUtils.getAndroidName(apiLevel)
  }

  fun getAndroidUI(): String {
    val version = NativeHelper.getProp("ro.build.version.oneui")
      .toIntOrNull()
      ?.takeIf { it > 0 }

    if (version != null) {
      val calculatedVersion = "${version / 10000}.${(version % 10000) / 100}"
      return "OneUI $calculatedVersion"
    }

    return ""
  }

  fun getSecurityPatch(): Instant? {
    return runCatching {
      val date = Build.VERSION.SECURITY_PATCH
      Instant.parse("${date}T00:00:00.00Z")
    }.getOrNull()
  }

  fun getJavaVMVersion(): String {
    return System.getProperty("java.vm.version").or("-")
  }

  fun getKernelVersion(): String {
    return System.getProperty("os.version").or("-")
  }

  fun getOpenGLESVersion(): String = runCatching {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
    val deviceConfigurationInfo = activityManager.deviceConfigurationInfo
    deviceConfigurationInfo.glEsVersion
  }.getOrDefault("-")

  fun getSELinux(): String {
    return NativeHelper.execute("getenforce").or("-")
  }

  fun getSystemUptime(): Instant? {
    return runCatching {
      Instant.now().minusMillis(SystemClock.uptimeMillis())
    }.getOrNull()
  }

  fun getVulkanVersion(): String {
    val version = context.packageManager.systemAvailableFeatures
      .firstOrNull { it.name == PackageManager.FEATURE_VULKAN_HARDWARE_VERSION }
      ?.version
      ?: return "-"

    // Vulkan packs the version as (major << 22) | (minor << 12) | patch
    val major = version shr 22
    val minor = (version shr 12) and 0x3FF
    val patch = version and 0xFFF
    return "$major.$minor.$patch"
  }

  fun getDrmInfo(): DrmInfo? {
    if (!MediaDrm.isCryptoSchemeSupported(WIDEVINE_UUID)) return null

    return runCatching {
      val mediaDrm = MediaDrm(WIDEVINE_UUID)
      try {
        DrmInfo(
          vendor = mediaDrm.getPropertyString(MediaDrm.PROPERTY_VENDOR).or("-"),
          version = mediaDrm.getPropertyString(MediaDrm.PROPERTY_VERSION).or("-"),
          description = mediaDrm.getPropertyString(MediaDrm.PROPERTY_DESCRIPTION).or("-"),
          algorithms = mediaDrm.getPropertyString(MediaDrm.PROPERTY_ALGORITHMS).or("-"),
          securityLevel = runCatching { mediaDrm.getPropertyString("securityLevel") }.getOrNull().or("-"),
          maxHdcpLevel = runCatching { mediaDrm.getPropertyString("maxHdcpLevel") }.getOrNull().or("-"),
        )
      } finally {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          mediaDrm.close()
        } else {
          @Suppress("DEPRECATION")
          mediaDrm.release()
        }
      }
    }.getOrNull()
  }

  fun getRuntime(): String {
    val name = System.getProperty("java.vm.name").or("ART")
    val ver = System.getProperty("java.vm.version").or("-")
    return "$name $ver"
  }

  fun isRooted(): Boolean {
    val paths = listOf(
      "/system/bin/su",
      "/system/xbin/su",
      "/sbin/su",
      "/system/app/Superuser.apk",
      "/su/bin/su",
      "/vendor/bin/su",
    )
    return paths.any { runCatching { java.io.File(it).exists() }.getOrDefault(false) } ||
      (NativeHelper.getProp("ro.debuggable") == "1" && NativeHelper.getProp("ro.secure") == "0")
  }

  fun isAbUpdate(): Boolean {
    return NativeHelper.getProp("ro.build.ab_update") == "true"
  }

  fun isTreble(): Boolean {
    return NativeHelper.getProp("ro.treble.enabled") == "true"
  }

  fun getPlayServicesVersion(): String? {
    return runCatching {
      context.packageManager.getPackageInfo("com.google.android.gms", 0).versionName
    }.getOrNull()
  }

  companion object {
    private val WIDEVINE_UUID = UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed")
  }
}

internal data class DrmInfo(
  val vendor: String,
  val version: String,
  val description: String,
  val algorithms: String,
  val securityLevel: String,
  val maxHdcpLevel: String,
)
