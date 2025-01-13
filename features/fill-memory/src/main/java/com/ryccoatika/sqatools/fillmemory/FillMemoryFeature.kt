package com.ryccoatika.sqatools.fillmemory

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.ui.graphics.vector.ImageVector
import com.ryccoatika.sqatools.common.SQAFeature
import me.tatarka.inject.annotations.Inject

@Inject
class FillMemoryFeature : SQAFeature {
  override val featureId: String
    get() = "FILL_MEMORY"
  override val icon: ImageVector
    get() = Icons.Outlined.Memory
  override val featureTitle: Int
    get() = R.string.fm_title
  override val featureDescription: Int
    get() = R.string.fm_desc

  override fun open(context: Context) {
    Intent(
      context,
      Class.forName("com.ryccoatika.sqatools.fillmemory.FillMemoryActivity"),
    ).run(context::startActivity)
  }
}
