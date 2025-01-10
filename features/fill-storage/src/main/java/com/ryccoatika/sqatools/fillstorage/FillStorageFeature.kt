package com.ryccoatika.sqatools.fillstorage

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.ui.graphics.vector.ImageVector
import com.ryccoatika.sqatools.common.SQAFeature
import me.tatarka.inject.annotations.Inject

@Inject
class FillStorageFeature : SQAFeature {
  override val featureId: String
    get() = "FILL_STORAGE"
  override val icon: ImageVector
    get() = Icons.Outlined.SdStorage
  override val featureTitle: Int
    get() = R.string.fs_title
  override val featureDescription: Int
    get() = R.string.fs_desc

  override fun open(context: Context) {
    Intent(
      context,
      Class.forName("com.ryccoatika.sqatools.fillstorage.FillStorageActivity"),
    ).run(context::startActivity)
  }
}
