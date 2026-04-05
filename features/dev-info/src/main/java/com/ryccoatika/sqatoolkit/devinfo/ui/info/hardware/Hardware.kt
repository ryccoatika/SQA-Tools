package com.ryccoatika.sqatoolkit.devinfo.ui.info.hardware

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import me.tatarka.inject.annotations.Inject

internal typealias Hardware = @Composable () -> Unit

@Inject
@Composable
internal fun Hardware() {
  Scaffold { padding ->
    Text(text = "Hardware", modifier = Modifier.padding(padding))
  }
}

@Inject
internal class HardwareType : DevInfoType {
  override val id: String
    get() = DevInfoType.HARDWARE_ID

  override val order: Int
    get() = 2

  override val featureTitle: Int
    get() = R.string.di_text_hardware
}
