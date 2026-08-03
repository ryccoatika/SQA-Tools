package com.ryccoatika.sqatoolkit.devinfo.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.euicc.EuiccManager
import com.ryccoatika.sqatoolkit.common.utils.or
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.Locale
import me.tatarka.inject.annotations.Inject

@Inject
internal class DeviceInfoUtils(
  private val context: Context,
) {

  @SuppressLint("HardwareIds")
  fun getAndroidId(): String {
    val androidId = runCatching {
      Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID,
      )
    }.getOrNull()
    return androidId.orEmpty()
  }

  fun getAndroidName(): String {
    return getAndroidName(Build.VERSION.SDK_INT)
  }

  fun getAndroidName(apiLevel: Int): String {
    return when (apiLevel) {
      24 -> "Nougat"
      25 -> "Nougat MR1"
      26 -> "Oreo"
      27 -> "Oreo MR1"
      28 -> "Pie"
      29 -> "Android 10"
      30 -> "Android 11"
      31 -> "Android 12"
      32 -> "Android 12L"
      33 -> "Android 13"
      34 -> "Android 14"
      35 -> "Android 15"
      36 -> "Android 16"
      37 -> "Android 17"
      else -> "-"
    }
  }

  fun getCodename(): String {
    return when (Build.VERSION.SDK_INT) {
      24, 25 -> "New York Cheesecake"
      26, 27 -> "Oatmeal Cookie"
      28 -> "Pistachio Ice Cream"
      29 -> "Quince Tart"
      30 -> "Red Velvet Cake"
      31 -> "Snow Cone"
      32 -> "Snow Cone v2"
      33 -> "Tiramisu"
      34 -> "Upside Down Cake"
      35 -> "Vanilla Ice Cream"
      36 -> "Baklava"
      else -> "-"
    }
  }

  fun getAndroidReleaseDate(): Instant? {
    return when (Build.VERSION.SDK_INT) {
      24 -> OffsetDateTime.parse("2016-08-22T09:29:40-07:00")
      25 -> OffsetDateTime.parse("2016-10-20T11:52:02-07:00")
      26 -> OffsetDateTime.parse("2017-08-21T10:19:51-07:00")
      27 -> OffsetDateTime.parse("2017-12-05T10:32:42-08:00")
      28 -> OffsetDateTime.parse("2018-07-24T14:03:51-07:00")
      29 -> OffsetDateTime.parse("2019-08-23T09:19:51-07:00")
      30 -> OffsetDateTime.parse("2020-09-07T10:28:03-07:00")
      31 -> OffsetDateTime.parse("2021-10-04T10:59:04-07:00")
      32 -> OffsetDateTime.parse("2022-03-07T11:21:46-08:00")
      33 -> OffsetDateTime.parse("2022-08-15T09:50:39-07:00")
      34 -> OffsetDateTime.parse("2023-10-03T22:56:25-07:00")
      35 -> OffsetDateTime.parse("2024-09-03T09:53:32-07:00")
      36 -> OffsetDateTime.parse("2025-06-10T09:45:15-07:00")
      else -> null
    }?.toInstant()
  }

  fun checkESimSupport(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
      return false
    }
    try {
      val euiccManager = context.getSystemService(Context.EUICC_SERVICE) as EuiccManager
      return euiccManager.isEnabled
    } catch (_: Exception) {
      return false
    }
  }

  fun getManufacturedDate(): Instant? {
    val dateString = NativeHelper.getProp("ril.rfcal_date") // yyyymmdd
    if (dateString.isEmpty()) return null
    val year: Int
    val month: Int
    val day: Int
    when {
      dateString.contains(".") -> {
        val date = dateString.split(".")
        if (date.size != 3) return null
        year = date[0].toInt()
        month = date[1].toInt()
        day = date[2].toInt()
      }

      dateString.contains("/") -> {
        val date = dateString.split("/")
        if (date.size != 3) return null
        year = date[0].toInt()
        month = date[1].toInt()
        day = date[2].toInt()
      }

      else -> {
        year = dateString.substring(0, 4).toInt()
        month = dateString.substring(4, 6).toInt()
        day = dateString.substring(6, 8).toInt()
      }
    }
    return LocalDate.of(year, month, day).atStartOfDay().toInstant(OffsetDateTime.now().offset)
  }

  fun getSalesCode(): String {
    return NativeHelper.getProp("ro.csc.sales_code").or("-")
  }

  fun getSalesCountry(): String {
    val countryCode = NativeHelper.getProp("ro.csc.country_code")
    val countryIsoCode = NativeHelper.getProp("ro.csc.countryiso_code")
    return SALES_COUNTRY_FORMAT.format(
      countryCode,
      countryIsoCode,
      countryCodeToFlag(countryIsoCode),
    )
  }

  private fun countryCodeToFlag(countryCode: String): String {
    if (countryCode.length != 2) return ""
    val offset = 127397
    val upper = countryCode.uppercase(Locale.ROOT)
    return String(Character.toChars(upper[0].code + offset)) + String(Character.toChars(upper[1].code + offset))
  }

  companion object {
    private const val SALES_COUNTRY_FORMAT = "%s (%s) %s"
  }
}
