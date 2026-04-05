package com.ryccoatika.sqatoolkit.devinfo.ui.info.software

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import me.tatarka.inject.annotations.Inject

internal typealias Software = @Composable () -> Unit

@Inject
@Composable
internal fun Software() {
  Scaffold { padding ->
    Text(text = "Software", modifier = Modifier.padding(padding))
  }
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
