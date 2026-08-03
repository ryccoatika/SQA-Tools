package com.ryccoatika.sqatoolkit.devinfo.ui.info.camera

import com.ryccoatika.sqatoolkit.devinfo.R
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import me.tatarka.inject.annotations.Inject

@Inject
internal class CameraType : DevInfoType {
  override val id: String
    get() = DevInfoType.CAMERA_ID

  override val order: Int
    get() = 5

  override val featureTitle: Int
    get() = R.string.di_text_camera
}
