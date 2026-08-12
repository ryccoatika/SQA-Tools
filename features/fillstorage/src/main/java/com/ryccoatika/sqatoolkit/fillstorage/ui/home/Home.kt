package com.ryccoatika.sqatoolkit.fillstorage.ui.home

import androidx.compose.foundation.layout.Arrangement
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
import com.ryccoatika.sqatoolkit.common.extensions.viewModel
import com.ryccoatika.sqatoolkit.common.ui.AppTopBar
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.fillstorage.R
import com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.preview.CompositionLocalProviderForPreview
import com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.preview.HomePreviewParameterProvider
import com.ryccoatika.sqatoolkit.fillstorage.ui.home.widget.StorageCard
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
    openManageStorage = openManageStorage,
    navigateUp = navigateUp,
  )
}

@Composable
private fun Home(
  state: HomeViewState,
  openManageStorage: (path: String) -> Unit,
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
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier
        .padding(paddingValues)
        .padding(16.dp),
    ) {
      state.storages.forEach { storage ->
        StorageCard(
          storage = storage,
          onManageButtonClicked = {
            openManageStorage(storage.path)
          },
        )
      }
    }
  }
}

@Composable
private fun HomeTopBar(
  navigateUp: () -> Unit,
) {
  AppTopBar(
    title = stringResource(R.string.fs_title),
    onBackPressed = navigateUp,
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
        openManageStorage = {},
        navigateUp = {},
      )
    }
  }
}
