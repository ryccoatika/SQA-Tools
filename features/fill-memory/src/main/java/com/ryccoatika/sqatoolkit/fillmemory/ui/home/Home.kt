package com.ryccoatika.sqatoolkit.fillmemory.ui.home

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.common.extensions.viewModel
import com.ryccoatika.sqatoolkit.common.ui.AppTopBar
import com.ryccoatika.sqatoolkit.common.ui.widget.SectionCard
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.fillmemory.R
import com.ryccoatika.sqatoolkit.fillmemory.core.model.FillMemory
import com.ryccoatika.sqatoolkit.fillmemory.ui.common.utils.preview.CompositionLocalProviderForPreview
import com.ryccoatika.sqatoolkit.fillmemory.ui.home.floater.service.FloaterService
import com.ryccoatika.sqatoolkit.fillmemory.ui.home.widget.FillMemoryOptions
import com.ryccoatika.sqatoolkit.fillmemory.ui.home.widget.FillMemoryProgress
import com.ryccoatika.sqatoolkit.fillmemory.ui.home.widget.MemoryGraph
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

internal typealias Home = @Composable (
  navigateUp: () -> Unit,
) -> Unit

@Inject
@Composable
internal fun Home(
  viewModelFactory: () -> HomeViewModel,
  @Assisted
  navigateUp: () -> Unit,
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
    navigateUp = navigateUp,
    fillMemory = viewModel::fillMemory,
    clearMemory = viewModel::clearMemory,
    dismissProgress = viewModel::dismissProgress,
  )
}

@Composable
private fun Home(
  state: HomeViewState,
  navigateUp: () -> Unit,
  fillMemory: (FillMemory) -> Unit,
  clearMemory: () -> Unit,
  dismissProgress: () -> Unit,
) {
  Scaffold(
    topBar = {
      HomeTopBar(
        navigateUp = navigateUp,
      )
    },
  ) { paddingValues ->

    state.fillMemoryProgress?.let { progress ->
      FillMemoryProgress(
        progress = progress,
        onDismissRequest = dismissProgress,
      )
    }

    Column(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
    ) {
      SectionCard(
        title = stringResource(R.string.fm_title),
        icon = Icons.Rounded.Memory,
      ) {
        MemoryGraph(
          memoryUsages = state.history,
          modifier = Modifier.fillMaxWidth(),
        )
      }
      SectionCard {
        FillMemoryOptions(
          onFill = fillMemory,
          enabled = !state.isLoading,
          modifier = Modifier.fillMaxWidth(),
        )
        Button(
          onClick = clearMemory,
          enabled = !state.isLoading,
          shape = MaterialTheme.shapes.medium,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(
            text = stringResource(R.string.fm_button_clear_memory),
          )
        }
      }
    }
  }
}

@Composable
private fun HomeTopBar(
  navigateUp: () -> Unit,
) {
  val context = LocalContext.current
  AppTopBar(
    title = stringResource(R.string.fm_title),
    onBackPressed = navigateUp,
    actions = {
      IconButton(
        onClick = {
          if (!Settings.canDrawOverlays(context)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
              data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
          } else {
            FloaterService.show(context)
          }
        },
      ) {
        Icon(
          imageVector = Icons.Default.PictureInPicture,
          contentDescription = null,
        )
      }
    },
  )
}

@Preview
@Composable
private fun HomePreview() {
  CompositionLocalProviderForPreview {
    SQAToolsTheme {
      Home(
        state = HomeViewState.Empty,
        navigateUp = {},
        fillMemory = {},
        clearMemory = {},
        dismissProgress = {},
      )
    }
  }
}
