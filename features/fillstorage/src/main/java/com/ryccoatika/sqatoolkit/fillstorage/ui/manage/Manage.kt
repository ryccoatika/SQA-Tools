package com.ryccoatika.sqatoolkit.fillstorage.ui.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.ryccoatika.sqatoolkit.common.extensions.viewModel
import com.ryccoatika.sqatoolkit.common.ui.AppTopBar
import com.ryccoatika.sqatoolkit.common.ui.theme.FeatureAccent
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.common.ui.theme.usageStatusColor
import com.ryccoatika.sqatoolkit.common.ui.widget.GradientHero
import com.ryccoatika.sqatoolkit.common.ui.widget.SectionCard
import com.ryccoatika.sqatoolkit.fillstorage.core.model.FillStorage
import com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.preview.CompositionLocalProviderForPreview
import com.ryccoatika.sqatoolkit.fillstorage.ui.manage.widget.ChartBar
import com.ryccoatika.sqatoolkit.fillstorage.ui.manage.widget.FillStorageField
import com.ryccoatika.sqatoolkit.fillstorage.ui.manage.widget.FillStorageOptions
import com.ryccoatika.sqatoolkit.fillstorage.ui.manage.widget.FillStorageProgress
import com.ryccoatika.sqatoolkit.fillstorage.ui.manage.widget.StorageChart
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

internal typealias Manage = @Composable (
  navigateUp: () -> Unit,
  openDummyFiles: (path: String) -> Unit,
) -> Unit

@Inject
@Composable
internal fun Manage(
  viewModelFactory: (SavedStateHandle) -> ManageViewModel,
  @Assisted navigateUp: () -> Unit,
  @Assisted openDummyFiles: (path: String) -> Unit,
) {
  Manage(
    viewModel = viewModel(factory = viewModelFactory),
    navigateUp = navigateUp,
    openDummyFiles = openDummyFiles,
  )
}

@Composable
private fun Manage(
  viewModel: ManageViewModel,
  navigateUp: () -> Unit,
  openDummyFiles: (path: String) -> Unit,
) {
  val viewState by viewModel.state.collectAsState()

  Manage(
    state = viewState,
    navigateUp = navigateUp,
    fillStorage = viewModel::fillStorage,
    dismissProgress = viewModel::dismissProgress,
    onFolderClicked = {
      openDummyFiles(viewModel.path)
    },
  )
}

@Composable
private fun Manage(
  state: ManageViewState,
  navigateUp: () -> Unit,
  fillStorage: (FillStorage) -> Unit,
  dismissProgress: () -> Unit,
  onFolderClicked: () -> Unit,
) {
  val textCreator = LocalTextCreator.current

  Scaffold(
    topBar = {
      ManageTopBar(
        state = state,
        navigateUp = navigateUp,
        onFolderClicked = onFolderClicked,
      )
    },
  ) { paddingValues ->

    state.fillStorageProgress?.let { progress ->
      FillStorageProgress(
        progress = progress,
        onDismissRequest = dismissProgress,
      )
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
    ) {
      GradientHero(
        title = textCreator.storageTitle(state.storage),
        subtitle = textCreator.storageCapacityDesc(state.storage),
        accent = FeatureAccent.Storage,
      )
      Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .padding(16.dp),
      ) {
        SectionCard(
          modifier = Modifier.fillMaxWidth(),
        ) {
          StorageChart(
            bars = generateChartBars(state),
            label = textCreator.storageUsedSpacePercentage(state.storage),
            barWidth = 32.dp,
            modifier = Modifier.fillMaxWidth(),
          )
        }
        SectionCard(
          modifier = Modifier.fillMaxWidth(),
        ) {
          FillStorageField(
            onFill = fillStorage,
            enabled = state.fillStorageProgress == null,
            modifier = Modifier.fillMaxWidth(),
          )
          FillStorageOptions(
            onFill = fillStorage,
            enabled = state.fillStorageProgress == null,
            modifier = Modifier.fillMaxWidth(),
          )
        }
      }
    }
  }
}

@Composable
private fun generateChartBars(state: ManageViewState): List<ChartBar> = buildList {
  val textCreator = LocalTextCreator.current

  val storage = state.storage
  val nonDummyFiles = storage.nonDummyFilesPercent
  val freeSpace = storage.freeSpacePercent
  val dummyFiles = storage.dummyFilesPercent

  add(
    ChartBar(
      value = nonDummyFiles,
      color = usageStatusColor(storage.usedSpacePercent),
      label = textCreator.storageNonDummyFilesLabel(storage),
    ),
  )
  add(
    ChartBar(
      value = dummyFiles,
      color = MaterialTheme.colorScheme.tertiary,
      label = textCreator.storageDummyFilesLabel(storage),
    ),
  )
  add(
    ChartBar(
      value = freeSpace,
      color = MaterialTheme.colorScheme.surfaceVariant,
      label = textCreator.storageFreeSpaceLabel(storage),
    ),
  )
}

@Composable
private fun ManageTopBar(
  state: ManageViewState,
  navigateUp: () -> Unit,
  onFolderClicked: () -> Unit,
) {
  val textCreator = LocalTextCreator.current

  AppTopBar(
    title = textCreator.storageTitle(state.storage),
    onBackPressed = navigateUp,
    actions = {
      IconButton(
        onClick = onFolderClicked,
      ) {
        Icon(
          imageVector = Icons.Outlined.FolderOpen,
          contentDescription = null,
        )
      }
    },
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
        fillStorage = {},
        dismissProgress = {},
        onFolderClicked = {},
      )
    }
  }
}
