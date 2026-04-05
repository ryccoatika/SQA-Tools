package com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.ryccoatika.sqatoolkit.fillstorage.core.utils.FillStorageTextCreator
import com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.LocalTextCreator

@Composable
internal fun CompositionLocalProviderForPreview(content: @Composable () -> Unit) {
  CompositionLocalProvider(
    LocalTextCreator provides FillStorageTextCreator(LocalContext.current),
  ) {
    content()
  }
}
