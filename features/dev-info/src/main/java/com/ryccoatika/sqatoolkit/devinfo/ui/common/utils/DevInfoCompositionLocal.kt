package com.ryccoatika.sqatoolkit.devinfo.ui.common.utils

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf
import com.ryccoatika.sqatoolkit.devinfo.core.utils.DevInfoTextCreator

internal val LocalTextCreator = staticCompositionLocalOf<DevInfoTextCreator> {
  error("LocalTextCreator not provided")
}

internal val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
  error("LocalSnackbarHostState not provided")
}

internal val LocalPermissionRequester = staticCompositionLocalOf<(String) -> Unit> {
  {}
}
