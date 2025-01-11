package com.ryccoatika.sqatools.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.ryccoatika.sqatools.common.SQAFeature
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatools.ui.common.utils.preview.HomePreviewParameterProvider
import com.ryccoatika.sqatools.ui.home.widget.FeatureCard
import me.tatarka.inject.annotations.Inject

internal typealias Home = @Composable () -> Unit

@Inject
@Composable
internal fun Home(
  features: Set<SQAFeature>,
) {
  Scaffold { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .padding(paddingValues),
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
private fun HomePreview(
  @PreviewParameter(HomePreviewParameterProvider::class)
  features: Set<SQAFeature>,
) {
  SQAToolsTheme {
    Home(
      features = features,
    )
  }
}
