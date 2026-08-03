package com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.preview

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.ryccoatika.sqatoolkit.devinfo.core.utils.DevInfoTextCreator
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalSnackbarHostState
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalTextCreator

@Composable
internal fun CompositionLocalProviderForPreview(content: @Composable () -> Unit) {
  CompositionLocalProvider(
    LocalTextCreator provides DevInfoTextCreator(LocalContext.current),
    LocalSnackbarHostState provides remember { SnackbarHostState() },
  ) {
    content()
  }
}
