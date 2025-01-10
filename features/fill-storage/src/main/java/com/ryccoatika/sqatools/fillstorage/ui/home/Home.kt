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
import com.ryccoatika.sqatools.common.ui.widget.DropdownButtonMenuType
import com.ryccoatika.sqatools.fillstorage.R
import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.preview.CompositionLocalProviderForPreview
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.preview.HomePreviewParameterProvider
import com.ryccoatika.sqatools.fillstorage.ui.home.widget.StorageCard
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

internal typealias Home = @Composable (
  openManageStorage: (path: String) -> Unit,
  navigateUp: () -> Unit,
) -> Unit

@Inject
@Composable
internal fun Home(
  viewModelFactory: () -> HomeViewModel,
  @Assisted openManageStorage: (path: String) -> Unit,
  @Assisted navigateUp: () -> Unit,
) {
  Home(
    viewModel = viewModel(factory = viewModelFactory),
    openManageStorage = openManageStorage,
    navigateUp = navigateUp,
  )
}

@Composable
private fun Home(
  viewModel: HomeViewModel,
  openManageStorage: (path: String) -> Unit,
  navigateUp: () -> Unit,
) {
  val viewState by viewModel.state.collectAsState()

  Home(
    state = viewState,
    onMetricChanged = viewModel::updateMetric,
    openManageStorage = openManageStorage,
    navigateUp = navigateUp,
  )
}

@Composable
private fun Home(
  state: HomeViewState,
  openManageStorage: (path: String) -> Unit,
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
        StorageCard(
          storage = storage,
          onManageButtonClicked = {
            openManageStorage(storage.path)
          },
        )
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
    title = stringResource(R.string.fs_title),
    onBackPressed = navigateUp,
    actions = {
      DropdownButtonMenu(
        text = textCreator.storageMetricText(state.metric),
        buttonType = DropdownButtonMenuType.TextButton,
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
        openManageStorage = {},
        navigateUp = {},
      )
    }
  }
}
