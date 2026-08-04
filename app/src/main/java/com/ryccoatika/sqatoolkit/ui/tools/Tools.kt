package com.ryccoatika.sqatoolkit.ui.tools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.R
import com.ryccoatika.sqatoolkit.common.SQAFeature
import com.ryccoatika.sqatoolkit.common.ui.AppTopBar
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.ui.common.utils.preview.ToolsPreviewParameterProvider
import com.ryccoatika.sqatoolkit.ui.tools.widget.FeatureCard
import me.tatarka.inject.annotations.Inject

internal typealias Tools = @Composable () -> Unit

@OptIn(ExperimentalMaterial3Api::class)
@Inject
@Composable
internal fun Tools(
  features: Set<SQAFeature>,
) {
  Scaffold(
    topBar = {
      AppTopBar(title = stringResource(R.string.app_name))
    },
  ) { paddingValues ->
    LazyColumn(
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.padding(paddingValues),
    ) {
      items(features.toList()) { feature ->
        FeatureCard(
          feature = feature,
        )
      }
    }
  }
}

@PreviewLightDark
@Composable
private fun ToolsPreview(
  @PreviewParameter(ToolsPreviewParameterProvider::class)
  features: Set<SQAFeature>,
) {
  SQAToolsTheme {
    Tools(
      features = features,
    )
  }
}
