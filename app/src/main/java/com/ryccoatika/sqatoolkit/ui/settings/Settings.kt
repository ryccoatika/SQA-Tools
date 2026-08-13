package com.ryccoatika.sqatoolkit.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.R
import com.ryccoatika.sqatoolkit.common.extensions.viewModel
import com.ryccoatika.sqatoolkit.common.ui.theme.FeatureAccent
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.common.ui.theme.ThemeMode
import com.ryccoatika.sqatoolkit.common.ui.widget.GradientHero
import com.ryccoatika.sqatoolkit.common.ui.widget.SectionCard
import me.tatarka.inject.annotations.Inject

internal typealias Settings = @Composable () -> Unit

@Inject
@Composable
internal fun Settings(
  viewModelFactory: () -> SettingsViewModel,
) {
  Settings(viewModel = viewModel(factory = viewModelFactory))
}

@Composable
private fun Settings(viewModel: SettingsViewModel) {
  val themeMode by viewModel.themeMode.collectAsState()
  Settings(
    themeMode = themeMode,
    onThemeModeChange = viewModel::setThemeMode,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Settings(
  themeMode: ThemeMode,
  onThemeModeChange: (ThemeMode) -> Unit,
) {
  val context = LocalContext.current
  val version = remember {
    runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
      .getOrNull().orEmpty()
  }

  val themeOptions = listOf(
    ThemeMode.LIGHT to R.string.settings_theme_light,
    ThemeMode.DARK to R.string.settings_theme_dark,
    ThemeMode.SYSTEM to R.string.settings_theme_auto,
  )

  Scaffold(
    contentWindowInsets = WindowInsets(0),
  ) { paddingValues ->
    Column(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.padding(paddingValues),
    ) {
      GradientHero(
        title = stringResource(R.string.settings_title),
        accent = FeatureAccent.Neutral,
        applyStatusBarInset = true,
      )
      Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(16.dp),
      ) {
      SectionCard(title = stringResource(R.string.settings_appearance), icon = Icons.Rounded.Palette) {
        Text(stringResource(R.string.settings_theme), style = MaterialTheme.typography.bodyLarge)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
          themeOptions.forEachIndexed { index, (mode, labelRes) ->
            SegmentedButton(
              selected = themeMode == mode,
              onClick = { onThemeModeChange(mode) },
              shape = SegmentedButtonDefaults.itemShape(index = index, count = themeOptions.size),
            ) {
              Text(stringResource(labelRes))
            }
          }
        }
      }
        SectionCard(title = stringResource(R.string.settings_about), icon = Icons.Rounded.Info) {
          Text(stringResource(R.string.app_name), style = MaterialTheme.typography.bodyLarge)
          Text(
            text = stringResource(R.string.settings_version, version),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
    }
  }
}

@Preview
@Composable
private fun SettingsPreview() {
  SQAToolsTheme {
    Settings(
      themeMode = ThemeMode.SYSTEM,
      onThemeModeChange = {},
    )
  }
}
