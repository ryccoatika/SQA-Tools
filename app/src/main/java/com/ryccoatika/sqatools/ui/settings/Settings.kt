package com.ryccoatika.sqatools.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme
import me.tatarka.inject.annotations.Inject

internal typealias Settings = @Composable () -> Unit

@Composable
@Inject
internal fun Settings() {
  Settings(modifier = Modifier)
}

@Composable
private fun Settings(
  modifier: Modifier,
) {
}

@Preview
@Composable
private fun SettingsPreview() {
  SQAToolsTheme {
    Settings(modifier = Modifier)
  }
}
