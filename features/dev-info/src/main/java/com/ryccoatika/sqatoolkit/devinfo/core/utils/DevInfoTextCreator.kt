package com.ryccoatika.sqatoolkit.devinfo.core.utils

import android.content.Context
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.core.model.Label
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

internal class DevInfoTextCreator(
  private val context: Context,
) {
  private val dateTimeFormatterLong by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
  }

  fun itemLabel(label: Label): String {
    val labelResId = when (label) {
      Label.AndroidID -> R.string.di_label_android_id
      Label.Device -> R.string.di_label_device
      Label.Model -> R.string.di_label_model
      Label.Brand -> R.string.di_label_brand
      Label.Board -> R.string.di_label_board
      Label.Hardware -> R.string.di_label_hardware
      Label.ProductCode -> R.string.di_label_product_code
      Label.Fingerprint -> R.string.di_label_fingerprint
      Label.ESim -> R.string.di_label_e_sim
      Label.Manufacturer -> R.string.di_label_manufacturer
      Label.ManufacturedDate -> R.string.di_label_manufactured_date
      Label.DeviceAge -> R.string.di_label_device_age
      Label.SalesCode -> R.string.di_label_sales_code
      Label.SalesCountry -> R.string.di_label_sales_country
    }

    return context.getString(labelResId)
  }

  fun androidVersionAndCodename(version: String, codename: String): String {
    return context.getString(R.string.di_text_version_and_codename, version, codename)
  }

  fun androidApiLevel(sdkVersion: Int): String {
    return context.getString(R.string.di_text_sdk_version, sdkVersion)
  }

  fun androidReleaseDate(releaseDate: Instant?): String {
    val dateString = releaseDate?.let {
      dateTimeFormatterLong.formatDate(releaseDate)
    } ?: context.getString(R.string.di_text_unknown)

    return context.getString(R.string.di_text_release_date, dateString)
  }

  fun longDateFormat(date: Instant?): String {
    return date?.let {
      dateTimeFormatterLong.formatDate(date)
    } ?: context.getString(R.string.di_text_unknown)
  }

  fun datePeriodFormat(date: Instant?): String {
    if (date == null) return context.getString(R.string.di_text_unknown)

    val period = Period.between(date.toLocalDate(), LocalDate.now())
    val years = period.years
    val months = period.months
    val days = period.days

    val periodString = StringBuilder()
    if (years > 0) {
      periodString.append(
        context.resources.getQuantityString(
          R.plurals.di_text_years,
          years,
          years,
        ),
      )
    }
    if (months > 0) {
      periodString.append(" ")
      periodString.append(
        context.resources.getQuantityString(
          R.plurals.di_text_months,
          months,
          months,
        ),
      )
    }
    if (days > 0) {
      periodString.append(" ")
      periodString.append(
        context.resources.getQuantityString(
          R.plurals.di_text_days,
          days,
          days,
        ),
      )
    }

    return periodString.toString().trim()
  }

  fun errorMessage(t: Throwable): String {
    return when {
      else -> t.localizedMessage.orEmpty()
    }
  }

  fun deviceInfoTypeTitle(type: DevInfoType): String {
    return context.getString(type.featureTitle)
  }
}
