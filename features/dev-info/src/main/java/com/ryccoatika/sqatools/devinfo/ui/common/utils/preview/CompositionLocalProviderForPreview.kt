package com.ryccoatika.sqatools.devinfo.ui.common.utils.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.ryccoatika.sqatools.devinfo.core.utils.DevInfoTextCreator
import com.ryccoatika.sqatools.devinfo.ui.common.utils.LocalTextCreator

@Composable
internal fun CompositionLocalProviderForPreview(content: @Composable () -> Unit) {
  CompositionLocalProvider(
    LocalTextCreator provides DevInfoTextCreator(LocalContext.current),
  ) {
    content()
  }
}
