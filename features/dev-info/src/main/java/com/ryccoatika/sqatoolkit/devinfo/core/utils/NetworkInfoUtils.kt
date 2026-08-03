package com.ryccoatika.sqatoolkit.devinfo.core.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.ryccoatika.sqatoolkit.common.utils.or
import me.tatarka.inject.annotations.Inject

@Inject
internal class NetworkInfoUtils(
  private val context: Context,
) {
  private fun tm() = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

  fun hasTelephony(): Boolean =
    context.packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)

  fun hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

  fun getNetworkOperator(): String = runCatching { tm().networkOperatorName }.getOrNull().or("-")
  fun getSimOperator(): String = runCatching { tm().simOperatorName }.getOrNull().or("-")
  fun getNetworkMccMnc(): String = mccMnc(runCatching { tm().networkOperator }.getOrNull())
  fun getSimMccMnc(): String = mccMnc(runCatching { tm().simOperator }.getOrNull())
  fun getSimCountry(): String = runCatching { tm().simCountryIso }.getOrNull()?.uppercase().or("-")
  fun getNetworkCountry(): String = runCatching { tm().networkCountryIso }.getOrNull()?.uppercase().or("-")

  @Suppress("DEPRECATION")
  fun getPhoneType(): String = when (runCatching { tm().phoneType }.getOrNull()) {
    TelephonyManager.PHONE_TYPE_GSM -> "GSM"
    TelephonyManager.PHONE_TYPE_CDMA -> "CDMA"
    TelephonyManager.PHONE_TYPE_SIP -> "SIP"
    else -> "None"
  }

  fun getSimState(): String = when (runCatching { tm().simState }.getOrNull()) {
    TelephonyManager.SIM_STATE_READY -> "Ready"
    TelephonyManager.SIM_STATE_ABSENT -> "Absent"
    TelephonyManager.SIM_STATE_PIN_REQUIRED -> "PIN Required"
    TelephonyManager.SIM_STATE_PUK_REQUIRED -> "PUK Required"
    TelephonyManager.SIM_STATE_NETWORK_LOCKED -> "Network Locked"
    TelephonyManager.SIM_STATE_NOT_READY -> "Not Ready"
    else -> "Unknown"
  }

  @Suppress("DEPRECATION")
  fun getSimCount(): String = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) tm().activeModemCount.toString()
    else tm().phoneCount.toString()
  }.getOrNull().or("-")

  fun isRoaming(): Boolean = runCatching { tm().isNetworkRoaming }.getOrDefault(false)

  // gated (READ_PHONE_STATE)
  fun getDataNetworkType(): String = runCatching {
    when (tm().dataNetworkType) {
      TelephonyManager.NETWORK_TYPE_LTE -> "LTE (4G)"
      TelephonyManager.NETWORK_TYPE_NR -> "NR (5G)"
      TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA (3G)"
      TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS (3G)"
      TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE (2G)"
      TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS (2G)"
      TelephonyManager.NETWORK_TYPE_UNKNOWN -> "Unknown"
      else -> "Other"
    }
  }.getOrNull().or("-")

  @Suppress("HardwareIds", "MissingPermission")
  fun getImei(): String = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) tm().imei else @Suppress("DEPRECATION") tm().deviceId
  }.getOrNull().or("Unknown")

  @Suppress("HardwareIds", "MissingPermission", "DEPRECATION")
  fun getPhoneNumber(): String = runCatching { tm().line1Number }.getOrNull().or("Unknown")

  private fun mccMnc(op: String?): String {
    if (op.isNullOrBlank() || op.length < 4) return "-"
    return "${op.substring(0, 3)} / ${op.substring(3)}"
  }
}
