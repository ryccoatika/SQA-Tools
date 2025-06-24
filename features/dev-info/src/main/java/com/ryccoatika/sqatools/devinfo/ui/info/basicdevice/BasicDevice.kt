package com.ryccoatika.sqatools.devinfo.ui.info.basicdevice

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ryccoatika.sqatools.devinfo.R
import com.ryccoatika.sqatools.devinfo.ui.info.DevInfoType
import me.tatarka.inject.annotations.Inject

internal typealias BasicDevice = @Composable () -> Unit

@Inject
@Composable
internal fun BasicDevice() {
  Scaffold { padding ->
    Text(text = "Basic Device", modifier = Modifier.padding(padding))
  }
}

@Inject
internal class BasicDeviceType : DevInfoType {
  override val id: String
    get() = DevInfoType.BASIC_DEVICE_ID

  override val order: Int
    get() = 1

  override val featureTitle: Int
    get() = R.string.di_text_basic_device
}
