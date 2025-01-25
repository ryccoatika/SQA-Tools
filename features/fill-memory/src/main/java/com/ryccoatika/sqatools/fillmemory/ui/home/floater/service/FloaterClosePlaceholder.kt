package com.ryccoatika.sqatools.fillmemory.ui.home.floater.service

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme

internal class FloaterClosePlaceholder(
  private val context: Context,
  private val lifecycleOwner: LifecycleOwner?,
  private val savedStateRegistryOwner: SavedStateRegistryOwner?,
) {
  private var isShowing = false

  private val view: ComposeView by lazy {
    ComposeView(context).apply {
      setViewTreeLifecycleOwner(lifecycleOwner)
      setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)

      setContent {
        ClosePlaceholderContent()
      }
    }
  }

  val layoutParams: LayoutParams by lazy {
    LayoutParams(
      LayoutParams.WRAP_CONTENT,
      LayoutParams.WRAP_CONTENT,
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LayoutParams.TYPE_APPLICATION_OVERLAY
      } else {
        @Suppress("DEPRECATION")
        LayoutParams.TYPE_PHONE
      },
      LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_LAYOUT_IN_SCREEN,
      PixelFormat.TRANSLUCENT,
    )
  }

  fun show(windowManager: WindowManager) {
    if (isShowing) return

    isShowing = true

    windowManager.addView(view, layoutParams)

    val screenHeight = context.resources.displayMetrics.heightPixels

    val valueAnimator = ValueAnimator.ofInt(screenHeight / 2, screenHeight / 4)
    valueAnimator.addUpdateListener {
      val value = it.animatedValue as Int
      layoutParams.y = value
      windowManager.updateViewLayout(view, layoutParams)
    }
    valueAnimator.start()
  }

  fun hide(windowManager: WindowManager) {
    if (!isShowing) return

    isShowing = false

    val screenHeight = context.resources.displayMetrics.heightPixels

    val valueAnimator = ValueAnimator.ofInt(screenHeight / 4, screenHeight / 2)
    valueAnimator.addUpdateListener {
      val value = it.animatedValue as Int
      layoutParams.y = value
      windowManager.updateViewLayout(view, layoutParams)
      if (value >= screenHeight / 2) {
        windowManager.removeView(view)
      }
    }
    valueAnimator.start()
  }

  fun checkViewIsClose(view: View, params: LayoutParams): Boolean {
    val rect = Rect(
      layoutParams.x - this.view.width,
      layoutParams.y - this.view.height,
      layoutParams.x + this.view.width,
      layoutParams.y + this.view.height,
    )
    val targetRect = Rect(
      params.x - view.width / 2,
      params.y - view.height / 2,
      params.x + view.width / 2,
      params.y + view.height / 2,
    )

    return rect.intersect(targetRect)
  }

  @Composable
  private fun ClosePlaceholderContent() {
    SQAToolsTheme {
      Box(
        modifier = Modifier
          .size(64.dp)
          .background(
            brush = Brush.radialGradient(
              colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primary.copy(0.8f),
                MaterialTheme.colorScheme.primary.copy(0.5f),
                Color.Transparent,
              ),
            ),
            shape = CircleShape,
          )
          .padding(9.dp)
          .border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.onPrimary,
            shape = CircleShape,
          ),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Outlined.Close,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onPrimary,
          modifier = Modifier.size(32.dp),
        )
      }
    }
  }
}
