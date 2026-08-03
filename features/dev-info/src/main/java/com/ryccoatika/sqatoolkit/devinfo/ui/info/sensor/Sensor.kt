package com.ryccoatika.sqatoolkit.devinfo.ui.info.sensor

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.common.extensions.viewModel
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.ui.common.ItemComposer
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import com.ryccoatika.sqatoolkit.devinfo.ui.info.common.InfoViewState
import me.tatarka.inject.annotations.Inject

internal typealias Sensor = @Composable () -> Unit

@Inject
@Composable
internal fun Sensor(
  viewModelFactory: () -> SensorViewModel,
) {
  Sensor(
    viewModel = viewModel(factory = viewModelFactory),
  )
}

@Composable
internal fun Sensor(
  viewModel: SensorViewModel,
) {
  val viewState by viewModel.state.collectAsState()

  Sensor(
    state = viewState,
  )
}

@Composable
internal fun Sensor(
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
internal class SensorType : DevInfoType {
  override val id: String
    get() = DevInfoType.SENSOR_ID

  override val order: Int
    get() = 7

  override val featureTitle: Int
    get() = R.string.di_text_sensor
}
