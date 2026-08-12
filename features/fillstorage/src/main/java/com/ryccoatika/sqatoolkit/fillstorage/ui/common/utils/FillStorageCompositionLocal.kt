package com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils

import androidx.compose.runtime.staticCompositionLocalOf
import com.ryccoatika.sqatoolkit.fillstorage.core.utils.FillStorageTextCreator

internal val LocalTextCreator = staticCompositionLocalOf<FillStorageTextCreator> {
  error("LocalTextCreator not provided")
}
