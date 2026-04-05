package com.ryccoatika.sqatoolkit.fillstorage

import android.content.Context
import android.content.Intent
import androidx.annotation.RestrictTo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SdStorage
import androidx.compose.ui.graphics.vector.ImageVector
import com.ryccoatika.sqatoolkit.common.SQAFeature
import me.tatarka.inject.annotations.Inject

@Inject
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
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
      Class.forName("com.ryccoatika.sqatoolkit.fillstorage.FillStorageActivity"),
    ).run(context::startActivity)
  }
}
