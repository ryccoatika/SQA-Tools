package com.ryccoatika.sqatools.devinfo.ui.info.advancedevice

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ryccoatika.sqatools.devinfo.R
import com.ryccoatika.sqatools.devinfo.ui.info.DevInfoType
import me.tatarka.inject.annotations.Inject

internal typealias AdvanceDevice = @Composable () -> Unit

@Inject
@Composable
internal fun AdvanceDevice() {
  Scaffold { padding ->
    Text(text = "AdvanceDevice", modifier = Modifier.padding(padding))
  }
}

@Inject
internal class AdvanceDeviceType : DevInfoType {
  override val id: String
    get() = DevInfoType.ADVANCE_DEVICE_ID

  override val order: Int
    get() = 3

  override val featureTitle: Int
    get() = R.string.di_text_advance_device
}
