package com.ryccoatika.sqatools.fillstorage.ui.manage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.ryccoatika.sqatools.common.extensions.viewModel
import com.ryccoatika.sqatools.common.ui.AppTopBar
import com.ryccoatika.sqatools.common.ui.VerticalSpace
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatools.fillstorage.R
import com.ryccoatika.sqatools.fillstorage.core.model.Storage
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatools.fillstorage.ui.common.utils.preview.CompositionLocalProviderForPreview
import com.ryccoatika.sqatools.fillstorage.ui.manage.widget.ChartBar
import com.ryccoatika.sqatools.fillstorage.ui.manage.widget.FillStorageField
import com.ryccoatika.sqatools.fillstorage.ui.manage.widget.FillStorageOptions
import com.ryccoatika.sqatools.fillstorage.ui.manage.widget.StorageChart
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

internal typealias Manage = @Composable (
  navigateUp: () -> Unit,
) -> Unit

@Inject
@Composable
internal fun Manage(
  viewModelFactory: (SavedStateHandle) -> ManageViewModel,
  @Assisted navigateUp: () -> Unit,
) {
  Manage(
    viewModel = viewModel(factory = viewModelFactory),
    navigateUp = navigateUp,
  )
}

@Composable
private fun Manage(
  viewModel: ManageViewModel,
  navigateUp: () -> Unit,
) {
  val viewState by viewModel.state.collectAsState()

  Manage(
    state = viewState,
    navigateUp = navigateUp,
  )
}

@Composable
private fun Manage(
  state: ManageViewState,
  navigateUp: () -> Unit,
) {
  val textCreator = LocalTextCreator.current
  val percentage = state.storage.usedSpace / state.storage.totalSpace

  Scaffold(
    topBar = {
      ManageTopBar(
        storageType = state.storage.type,
        navigateUp = navigateUp,
      )
    },
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .padding(paddingValues)
        .padding(16.dp),
    ) {
      StorageChart(
        bars = generateChartBars(state),
        label = textCreator.percentageText(percentage),
        barWidth = 32.dp,
        modifier = Modifier.fillMaxWidth(),
      )
      16.VerticalSpace()
      FillStorageField(
        onFill = {},
      )
      8.VerticalSpace()
      FillStorageOptions(
        onFill = {},
      )
    }
  }
}

@Composable
private fun generateChartBars(state: ManageViewState): List<ChartBar> = buildList {
  val textCreator = LocalTextCreator.current

  val storage = state.storage
  val usedSpace = storage.usedSpace / storage.totalSpace
  val freeSpace = storage.freeSpace / storage.totalSpace

  add(
    ChartBar(
      value = usedSpace,
      color = Color.Green,
      label = stringResource(
        R.string.fs_text_used_space,
        textCreator.storageUsedSpaceText(storage),
      ),
    ),
  )
  add(
    ChartBar(
      value = freeSpace,
      color = Color.Gray,
      label = stringResource(
        R.string.fs_text_free_space,
        textCreator.storageFreeSpaceText(storage),
      ),
    ),
  )
}

@Composable
private fun ManageTopBar(
  storageType: Storage.Type,
  navigateUp: () -> Unit,
) {
  val textCreator = LocalTextCreator.current

  AppTopBar(
    title = textCreator.storageTypeManageTitle(storageType),
    onBackPressed = navigateUp,
  )
}

@PreviewLightDark
@Composable
private fun ManagePreview() {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      Manage(
        state = ManageViewState.Empty,
        navigateUp = {},
      )
    }
  }
}
