package com.ryccoatika.sqatools.fillstorage.ui.common.utils

import androidx.compose.runtime.staticCompositionLocalOf
import com.ryccoatika.sqatools.fillstorage.core.utils.FillStorageTextCreator

internal val LocalTextCreator = staticCompositionLocalOf<FillStorageTextCreator> {
  error("LocalTextCreator not provided")
}
