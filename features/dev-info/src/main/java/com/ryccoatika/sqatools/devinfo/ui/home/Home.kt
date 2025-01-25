package com.ryccoatika.sqatools.devinfo.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ryccoatika.sqatools.common.ui.AppTopBar
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatools.devinfo.R
import com.ryccoatika.sqatools.devinfo.ui.common.utils.preview.CompositionLocalProviderForPreview
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

internal typealias Home = @Composable (
  navigateUp: () -> Unit,
) -> Unit

@Inject
@Composable
internal fun Home(
  @Assisted
  navigateUp: () -> Unit,
) {
  Scaffold(
    topBar = {
      HomeTopBar(
        navigateUp = navigateUp,
      )
    },
  ) { paddingValues ->
    Column(
      modifier = Modifier.padding(paddingValues),
    ) {}
  }
}

@Composable
private fun HomeTopBar(
  navigateUp: () -> Unit,
) {
  AppTopBar(
    title = stringResource(R.string.di_title),
    onBackPressed = navigateUp,
  )
}

@Preview
@Composable
private fun HomePreview() {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      Home(
        navigateUp = {},
      )
    }
  }
}
