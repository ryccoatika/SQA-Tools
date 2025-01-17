package com.ryccoatika.sqatools.fillmemory.ui.common.utils.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.ryccoatika.sqatools.fillmemory.core.utils.FillMemoryTextCreator
import com.ryccoatika.sqatools.fillmemory.ui.common.utils.LocalTextCreator

@Composable
internal fun CompositionLocalProviderForPreview(content: @Composable () -> Unit) {
  CompositionLocalProvider(
    LocalTextCreator provides FillMemoryTextCreator(LocalContext.current),
  ) {
    content()
  }
}
