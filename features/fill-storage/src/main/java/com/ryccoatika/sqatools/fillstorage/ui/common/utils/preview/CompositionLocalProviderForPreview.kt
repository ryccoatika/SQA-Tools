package com.ryccoatika.sqatools.fillstorage.ui.common.utils.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.ryccoatika.sqatools.fillstorage.core.utils.FillStorageTextCreator
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.LocalTextCreator

@Composable
fun CompositionLocalProviderForPreview(content: @Composable () -> Unit) {
  CompositionLocalProvider(
    LocalTextCreator provides FillStorageTextCreator(),
  ) {
    content()
  }
}
