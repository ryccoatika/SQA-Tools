package com.ryccoatika.sqatoolkit.devinfo.ui.info.network

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import me.tatarka.inject.annotations.Inject

internal typealias Network = @Composable () -> Unit

@Inject
@Composable
internal fun Network() {
  Scaffold { padding ->
    Text(text = "Network", modifier = Modifier.padding(padding))
  }
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
