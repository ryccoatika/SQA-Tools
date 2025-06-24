package com.ryccoatika.sqatools.devinfo.core.utils

import android.content.Context
import com.ryccoatika.sqatools.devinfo.ui.info.DevInfoType

internal class DevInfoTextCreator(
  private val context: Context,
) {
  fun errorMessage(t: Throwable): String {
    return when {
      else -> t.localizedMessage ?: ""
    }
  }

  fun deviceInfoTypeTitle(type: DevInfoType): String {
    return context.getString(type.featureTitle)
  }
}
