package com.ryccoatika.sqatoolkit.fillmemory.ui.common.utils

import androidx.compose.runtime.staticCompositionLocalOf
import com.ryccoatika.sqatoolkit.fillmemory.core.utils.FillMemoryTextCreator

internal val LocalTextCreator = staticCompositionLocalOf<FillMemoryTextCreator> {
  error("LocalTextCreator not provided")
}
