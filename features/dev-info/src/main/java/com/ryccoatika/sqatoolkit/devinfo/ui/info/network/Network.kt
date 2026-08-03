package com.ryccoatika.sqatoolkit.devinfo.ui.info.network

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.common.extensions.viewModel
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.ui.common.ItemComposer
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalPermissionRequester
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import com.ryccoatika.sqatoolkit.devinfo.ui.info.common.InfoViewState
import me.tatarka.inject.annotations.Inject

internal typealias Network = @Composable () -> Unit

@Inject
@Composable
internal fun Network(
  viewModelFactory: () -> NetworkViewModel,
) {
  Network(
    viewModel = viewModel(factory = viewModelFactory),
  )
}

@Composable
internal fun Network(
  viewModel: NetworkViewModel,
) {
  val viewState by viewModel.state.collectAsState()
  val launcher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission(),
  ) { granted -> if (granted) viewModel.refresh() }

  CompositionLocalProvider(
    LocalPermissionRequester provides { launcher.launch(it) },
  ) {
    Network(state = viewState)
  }
}

@Composable
internal fun Network(
  state: InfoViewState,
) {
  ItemComposer(
    items = state.items,
    modifier = Modifier
      .padding(vertical = 8.dp, horizontal = 16.dp)
      .verticalScroll(state = rememberScrollState())
      .fillMaxSize(),
  )
}

@Inject
internal class NetworkType : DevInfoType {
  override val id: String
    get() = DevInfoType.NETWORK_ID

  override val order: Int
    get() = 3

  override val featureTitle: Int
    get() = R.string.di_text_network
}
