package com.ryccoatika.sqatools.fillmemory.ui.common.utils

import androidx.compose.runtime.staticCompositionLocalOf
import com.ryccoatika.sqatools.fillmemory.core.utils.FillMemoryTextCreator

internal val LocalTextCreator = staticCompositionLocalOf<FillMemoryTextCreator> {
  error("LocalTextCreator not provided")
}
