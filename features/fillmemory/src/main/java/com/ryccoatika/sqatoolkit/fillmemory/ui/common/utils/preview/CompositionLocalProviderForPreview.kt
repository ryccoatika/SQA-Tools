package com.ryccoatika.sqatoolkit.fillmemory.ui.common.utils.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.ryccoatika.sqatoolkit.fillmemory.core.utils.FillMemoryTextCreator
import com.ryccoatika.sqatoolkit.fillmemory.ui.common.utils.LocalTextCreator

@Composable
internal fun CompositionLocalProviderForPreview(content: @Composable () -> Unit) {
  CompositionLocalProvider(
    LocalTextCreator provides FillMemoryTextCreator(LocalContext.current),
  ) {
    content()
  }
}
