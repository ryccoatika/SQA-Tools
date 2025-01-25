package com.ryccoatika.sqatools.devinfo.ui.common.utils

import androidx.compose.runtime.staticCompositionLocalOf
import com.ryccoatika.sqatools.devinfo.core.utils.DevInfoTextCreator

internal val LocalTextCreator = staticCompositionLocalOf<DevInfoTextCreator> {
  error("LocalTextCreator not provided")
}
