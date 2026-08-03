package com.ryccoatika.sqatoolkit.devinfo.ui.info.software

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

internal typealias Software = @Composable () -> Unit

@Inject
@Composable
internal fun Software(
  viewModelFactory: () -> SoftwareViewModel,
) {
  Software(
    viewModel = viewModel(factory = viewModelFactory),
  )
}

@Composable
internal fun Software(
  viewModel: SoftwareViewModel,
) {
  val viewState by viewModel.state.collectAsState()

  Software(
    state = viewState,
  )
}

@Composable
internal fun Software(
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
internal class SoftwareType : DevInfoType {
  override val id: String
    get() = DevInfoType.SOFTWARE_ID

  override val order: Int
    get() = 4

  override val featureTitle: Int
    get() = R.string.di_text_software
}
