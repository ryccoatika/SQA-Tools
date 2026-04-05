package com.ryccoatika.sqatoolkit.devinfo.ui.info.camera

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import me.tatarka.inject.annotations.Inject

internal typealias Camera = @Composable () -> Unit

@Inject
@Composable
internal fun Camera() {
  Scaffold { padding ->
    Text(text = "Camera", modifier = Modifier.padding(padding))
  }
}

@Inject
internal class CameraType : DevInfoType {
  override val id: String
    get() = DevInfoType.CAMERA_ID

  override val order: Int
    get() = 5

  override val featureTitle: Int
    get() = R.string.di_text_camera
}
