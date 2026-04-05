package com.ryccoatika.sqatoolkit.devinfo.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.common.ui.AppTopBar
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoScreens
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

internal typealias Main = @Composable (
  navigateUp: () -> Unit,
) -> Unit

@Inject
@Composable
internal fun Main(
  types: Set<DevInfoType>,
  screens: DevInfoScreens,
  @Assisted
  navigateUp: () -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  var selectedDevInfoType by remember { mutableStateOf(types.firstOrNull()) }
  val pagerState = rememberPagerState { types.size }
  val textCreator = LocalTextCreator.current

  LaunchedEffect(pagerState) {
    snapshotFlow { pagerState.currentPage }.collect { page ->
      selectedDevInfoType = types.elementAt(page)
    }
  }

  Scaffold(
    topBar = {
      MainTopBar(
        navigateUp = navigateUp,
      )
    },
  ) { paddingValues ->
    Column(
      modifier = Modifier.padding(paddingValues),
    ) {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp),
      ) {
        items(
          items = types.toList(),
          key = { it.id },
        ) { type ->
          FilterChip(
            selected = type == selectedDevInfoType,
            label = {
              Text(textCreator.deviceInfoTypeTitle(type))
            },
            onClick = {
              selectedDevInfoType = type
              coroutineScope.launch {
                pagerState.animateScrollToPage(
                  page = types.indexOf(type),
                )
              }
            },
          )
        }
      }
      HorizontalPager(
        state = pagerState,
        modifier = Modifier.weight(1f),
        key = { types.elementAt(it).id },
      ) {
        when (types.elementAt(it).id) {
          DevInfoType.DEVICE_ID -> screens.device()
          DevInfoType.HARDWARE_ID -> screens.hardware()
          DevInfoType.NETWORK_ID -> screens.network()
          DevInfoType.SOFTWARE_ID -> screens.software()
          DevInfoType.CAMERA_ID -> screens.camera()
          DevInfoType.CONNECTIVITY_ID -> screens.connectivity()
          DevInfoType.SENSOR_ID -> screens.sensor()
          else -> {}
        }
      }
    }
  }
}

@Composable
private fun MainTopBar(
  navigateUp: () -> Unit,
) {
  AppTopBar(
    title = stringResource(R.string.di_title),
    onBackPressed = navigateUp,
  )
}
