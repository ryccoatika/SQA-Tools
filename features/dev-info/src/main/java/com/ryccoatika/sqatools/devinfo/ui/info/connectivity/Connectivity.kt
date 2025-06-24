package com.ryccoatika.sqatools.devinfo.ui.info.connectivity

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ryccoatika.sqatools.devinfo.R
import com.ryccoatika.sqatools.devinfo.ui.info.DevInfoType
import me.tatarka.inject.annotations.Inject

internal typealias Connectivity = @Composable () -> Unit

@Inject
@Composable
internal fun Connectivity() {
  Scaffold { padding ->
    Text(text = "Connectivity", modifier = Modifier.padding(padding))
  }
}

@Inject
internal class ConnectivityType : DevInfoType {
  override val id: String
    get() = DevInfoType.CONNECTIVITY_ID

  override val order: Int
    get() = 6

  override val featureTitle: Int
    get() = R.string.di_text_connectivity
}
