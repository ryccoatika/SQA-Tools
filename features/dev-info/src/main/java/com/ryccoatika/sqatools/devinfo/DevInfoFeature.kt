package com.ryccoatika.sqatools.devinfo

import android.content.Context
import android.content.Intent
import androidx.annotation.RestrictTo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PermDeviceInformation
import androidx.compose.ui.graphics.vector.ImageVector
import com.ryccoatika.sqatools.common.SQAFeature
import me.tatarka.inject.annotations.Inject

@Inject
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class DevInfoFeature : SQAFeature {
  override val featureId: String
    get() = "DEV_INFO"
  override val icon: ImageVector
    get() = Icons.Outlined.PermDeviceInformation
  override val featureTitle: Int
    get() = R.string.di_title
  override val featureDescription: Int
    get() = R.string.di_desc

  override fun open(context: Context) {
    Intent(
      context,
      Class.forName("com.ryccoatika.sqatools.devinfo.DevInfoActivity"),
    ).run(context::startActivity)
  }
}
