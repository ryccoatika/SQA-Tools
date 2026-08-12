package com.ryccoatika.sqatoolkit.ui.settings

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.R
import com.ryccoatika.sqatoolkit.common.extensions.viewModel
import com.ryccoatika.sqatoolkit.common.ui.theme.FeatureAccent
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
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
  val dynamicColor by viewModel.useDynamicColor.collectAsState()
  Settings(
    dynamicColor = dynamicColor,
    onDynamicColorChange = viewModel::setDynamicColor,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Settings(
  dynamicColor: Boolean,
  onDynamicColorChange: (Boolean) -> Unit,
) {
  val context = LocalContext.current
  val version = remember {
    runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
      .getOrNull().orEmpty()
  }
  val dynamicSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

  Scaffold(
    topBar = { GradientHero(title = stringResource(R.string.settings_title), accent = FeatureAccent.Neutral) },
  ) { paddingValues ->
    Column(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier
        .padding(paddingValues)
        .padding(16.dp),
    ) {
      SectionCard(title = stringResource(R.string.settings_appearance), icon = Icons.Rounded.Palette) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.settings_dynamic_color), style = MaterialTheme.typography.bodyLarge)
            Text(
              text = stringResource(
                if (dynamicSupported) R.string.settings_dynamic_color_desc
                else R.string.settings_dynamic_color_unsupported,
              ),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          Switch(
            checked = dynamicColor && dynamicSupported,
            enabled = dynamicSupported,
            onCheckedChange = onDynamicColorChange,
          )
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

@Preview
@Composable
private fun SettingsPreview() {
  SQAToolsTheme {
    Settings(
      dynamicColor = true,
      onDynamicColorChange = {},
    )
  }
}
