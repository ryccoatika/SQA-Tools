package com.ryccoatika.sqatoolkit.devinfo.ui.common.utils

import androidx.compose.runtime.staticCompositionLocalOf
import com.ryccoatika.sqatoolkit.devinfo.core.utils.DevInfoTextCreator

internal val LocalTextCreator = staticCompositionLocalOf<DevInfoTextCreator> {
  error("LocalTextCreator not provided")
}
