package com.ryccoatika.sqatools.fillstorage.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatools.common.extensions.viewModel
import com.ryccoatika.sqatools.common.ui.AppTopBar
import com.ryccoatika.sqatools.common.ui.VerticalSpace
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatools.common.ui.widget.DropdownButtonMenu
import com.ryccoatika.sqatools.fillstorage.R
import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.di.FillStorageScope
import com.ryccoatika.sqatools.fillstorage.ui.common.StorageChart
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.preview.CompositionLocalProviderForPreview
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.preview.HomePreviewParameterProvider
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

internal typealias Home = @Composable (
  navigateUp: () -> Unit,
) -> Unit

@FillStorageScope
@Inject
@Composable
internal fun Home(
  viewModelFactory: () -> HomeViewModel,
  @Assisted navigateUp: () -> Unit,
) {
  Home(
    viewModel = viewModel(factory = viewModelFactory),
    navigateUp = navigateUp,
  )
}

@Composable
private fun Home(
  viewModel: HomeViewModel,
  navigateUp: () -> Unit,
) {
  val viewState by viewModel.state.collectAsState()

  Home(
    state = viewState,
    onMetricChanged = viewModel::updateMetric,
    navigateUp = navigateUp,
  )
}

@Composable
private fun Home(
  state: HomeViewState,
  onMetricChanged: (Storage.Metric) -> Unit,
  navigateUp: () -> Unit,
) {
  Scaffold(
    topBar = {
      HomeTopBar(
        state = state,
        onMetricChanged = onMetricChanged,
        navigateUp = navigateUp,
      )
    },
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .padding(paddingValues)
        .padding(10.dp),
    ) {
      state.storages.forEach { storage ->
        StorageChart(storage)
        10.VerticalSpace()
      }
    }
  }
}

@Composable
private fun HomeTopBar(
  state: HomeViewState,
  onMetricChanged: (Storage.Metric) -> Unit,
  navigateUp: () -> Unit,
) {
  val textCreator = LocalTextCreator.current

  AppTopBar(
    title = stringResource(R.string.title_fill_storage),
    onBackPressed = navigateUp,
    actions = {
      DropdownButtonMenu(
        text = textCreator.storageMetricText(state.metric),
        options = Storage.Metric.entries,
        optionText = textCreator::storageMetricText,
        onSelected = onMetricChanged,
      )
    },
  )
}

@Preview
@Composable
private fun HomePreview(
  @PreviewParameter(HomePreviewParameterProvider::class) homeViewState: HomeViewState,
) {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      Home(
        state = homeViewState,
        onMetricChanged = {},
        navigateUp = {},
      )
    }
  }
}
