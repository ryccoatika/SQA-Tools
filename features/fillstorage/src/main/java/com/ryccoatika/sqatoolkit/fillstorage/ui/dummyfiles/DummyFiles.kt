package com.ryccoatika.sqatoolkit.fillstorage.ui.dummyfiles

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.FolderOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.SavedStateHandle
import com.ryccoatika.sqatoolkit.common.extensions.viewModel
import com.ryccoatika.sqatoolkit.common.ui.AppTopBar
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.common.ui.widget.EmptyState
import com.ryccoatika.sqatoolkit.fillstorage.R
import com.ryccoatika.sqatoolkit.fillstorage.core.model.DummyFile
import com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.preview.CompositionLocalProviderForPreview
import com.ryccoatika.sqatoolkit.fillstorage.ui.common.utils.preview.DummyFilesPreviewParameterProvider
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

typealias DummyFiles = @Composable (
  navigateUp: () -> Unit,
) -> Unit

@Inject
@Composable
internal fun DummyFiles(
  viewModelFactory: (SavedStateHandle) -> DummyFilesViewModel,
  @Assisted navigateUp: () -> Unit,
) {
  DummyFiles(
    viewModel = viewModel(factory = viewModelFactory),
    navigateUp = navigateUp,
  )
}

@Composable
private fun DummyFiles(
  viewModel: DummyFilesViewModel,
  navigateUp: () -> Unit,
) {
  val viewState by viewModel.state.collectAsState()

  DummyFiles(
    state = viewState,
    navigateUp = navigateUp,
    deleteFile = viewModel::deleteFile,
    deleteAll = viewModel::deleteAllFiles,
  )
}

@Composable
private fun DummyFiles(
  state: DummyFilesViewState,
  navigateUp: () -> Unit,
  deleteFile: (DummyFile) -> Unit,
  deleteAll: () -> Unit,
) {
  Scaffold(
    topBar = {
      DummyFilesTopBar(
        navigateUp = navigateUp,
        deleteAll = deleteAll,
        enableDeleteAll = !state.isLoading && state.files.isNotEmpty(),
      )
    },
  ) { paddingValues ->
    Column(
      modifier = Modifier.padding(paddingValues),
    ) {
      if (state.isLoading) {
        LinearProgressIndicator(
          modifier = Modifier.fillMaxWidth(),
        )
      }
      if (state.files.isEmpty() && !state.isLoading) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.fillMaxSize(),
        ) {
          EmptyState(
            icon = Icons.Rounded.FolderOff,
            title = stringResource(R.string.fs_empty_dummy_files_title),
            message = stringResource(R.string.fs_empty_dummy_files_message),
          )
        }
      } else {
        LazyColumn {
          items(
            items = state.files,
            key = { it.name },
          ) { file ->
            ListItem(
              headlineContent = {
                Text(
                  text = file.name,
                  style = MaterialTheme.typography.bodyLarge,
                )
              },
              supportingContent = {
                Text(
                  text = stringResource(R.string.fs_text_mb_value_no_decimal, file.sizeInMB),
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              },
              trailingContent = {
                IconButton(
                  onClick = {
                    deleteFile(file)
                  },
                  enabled = !state.isLoading,
                  colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                  ),
                ) {
                  Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                  )
                }
              },
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
          }
        }
      }
    }
  }
}

@Composable
private fun DummyFilesTopBar(
  enableDeleteAll: Boolean,
  navigateUp: () -> Unit,
  deleteAll: () -> Unit,
) {
  AppTopBar(
    title = stringResource(R.string.fs_title_dummy_files),
    onBackPressed = navigateUp,
    actions = {
      TextButton(
        onClick = deleteAll,
        enabled = enableDeleteAll,
      ) {
        Text(stringResource(R.string.fs_button_delete_all))
      }
    },
  )
}

@PreviewLightDark
@Composable
private fun DummyFilesPreview(
  @PreviewParameter(DummyFilesPreviewParameterProvider::class) state: DummyFilesViewState,
) {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      DummyFiles(
        state = state,
        navigateUp = {},
        deleteFile = {},
        deleteAll = {},
      )
    }
  }
}
