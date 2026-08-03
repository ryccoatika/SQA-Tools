package com.ryccoatika.sqatoolkit.devinfo.core.utils

import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.ryccoatika.sqatoolkit.common.utils.or
import me.tatarka.inject.annotations.Inject

@Inject
internal class ConnectivityInfoUtils(
  private val context: Context,
) {
  private fun wifi() = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
  private fun pm() = context.packageManager

  fun hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
      PackageManager.PERMISSION_GRANTED

  fun hasBluetoothConnectPermission(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
      ContextCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) ==
        PackageManager.PERMISSION_GRANTED

  fun isWifiEnabled(): Boolean = runCatching { wifi().isWifiEnabled }.getOrDefault(false)
  fun is5GhzSupported(): Boolean = runCatching { wifi().is5GHzBandSupported }.getOrDefault(false)
  fun is6GhzSupported(): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && runCatching { wifi().is6GHzBandSupported }.getOrDefault(false)
  fun isWifiAwareSupported(): Boolean = pm().hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)
  fun isWifiDirectSupported(): Boolean = pm().hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)

  fun getSsid(): String = runCatching {
    @Suppress("DEPRECATION") val ssid = wifi().connectionInfo.ssid
    ssid
  }.getOrNull().or("-")

  fun getBssid(): String = runCatching {
    @Suppress("DEPRECATION") val bssid = wifi().connectionInfo.bssid
    bssid
  }.getOrNull().or("-")

  fun getLinkSpeed(): String = runCatching {
    @Suppress("DEPRECATION") val speed = wifi().connectionInfo.linkSpeed
    "$speed Mbps"
  }.getOrNull().or("-")

  fun getFrequency(): String = runCatching {
    @Suppress("DEPRECATION") val freq = wifi().connectionInfo.frequency
    "$freq MHz"
  }.getOrNull().or("-")

  fun getRssi(): String = runCatching {
    @Suppress("DEPRECATION") val rssi = wifi().connectionInfo.rssi
    "$rssi dBm"
  }.getOrNull().or("-")

  fun getIpAddress(): String = runCatching {
    @Suppress("DEPRECATION") val ip = wifi().connectionInfo.ipAddress
    "%d.%d.%d.%d".format(ip and 0xff, ip shr 8 and 0xff, ip shr 16 and 0xff, ip shr 24 and 0xff)
  }.getOrNull().or("-")

  fun isBluetoothSupported(): Boolean = pm().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
  fun isBleSupported(): Boolean = pm().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
  fun isBleAdvertiserSupported(): Boolean = runCatching {
    (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)
      .adapter?.isMultipleAdvertisementSupported == true
  }.getOrDefault(false)

  @Suppress("MissingPermission")
  fun getBluetoothName(): String = runCatching {
    (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter?.name
  }.getOrNull().or("-")

  fun isNfcSupported(): Boolean = pm().hasSystemFeature(PackageManager.FEATURE_NFC)
  fun isUsbHostSupported(): Boolean = pm().hasSystemFeature(PackageManager.FEATURE_USB_HOST)
  fun isEthernetSupported(): Boolean = pm().hasSystemFeature(PackageManager.FEATURE_ETHERNET)
}
