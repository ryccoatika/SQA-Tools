package com.ryccoatika.sqatools.devinfo.core.utils

import android.content.Context

internal class DevInfoTextCreator(
  private val context: Context,
) {
  fun errorMessage(t: Throwable): String {
    return when {
      else -> t.localizedMessage ?: ""
    }
  }
}
