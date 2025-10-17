package com.ryccoatika.sqatools.devinfo.ui.info.device

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatools.common.extensions.viewModel
import com.ryccoatika.sqatools.devinfo.R
import com.ryccoatika.sqatools.devinfo.ui.common.ItemComposer
import com.ryccoatika.sqatools.devinfo.ui.info.DevInfoType
import me.tatarka.inject.annotations.Inject

internal typealias Device = @Composable () -> Unit

@Inject
@Composable
internal fun Device(
  viewModelFactory: () -> DeviceViewModel,
) {
  Device(
    viewModel = viewModel(factory = viewModelFactory),
  )
}

@Composable
internal fun Device(
  viewModel: DeviceViewModel,
) {
  val viewState by viewModel.state.collectAsState()

  Device(
    state = viewState,
  )
}

@Composable
internal fun Device(
  state: DeviceViewState,
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
internal class DeviceType : DevInfoType {
  override val id: String
    get() = DevInfoType.DEVICE_ID

  override val order: Int
    get() = 1

  override val featureTitle: Int
    get() = R.string.di_text_device
}
