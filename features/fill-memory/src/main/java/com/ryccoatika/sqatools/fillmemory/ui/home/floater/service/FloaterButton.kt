package com.ryccoatika.sqatools.fillmemory.ui.home.floater.service

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Service.VIBRATOR_MANAGER_SERVICE
import android.app.Service.VIBRATOR_SERVICE
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.ryccoatika.sqatools.common.ui.theme.SQAToolsTheme
import kotlin.math.absoluteValue

internal class FloaterButton(
  private val context: Context,
  private val lifecycleOwner: LifecycleOwner?,
  private val savedStateRegistryOwner: SavedStateRegistryOwner?,
  private val onStopServiceRequest: () -> Unit,
  private val onClick: () -> Unit = {},
) {
  val view by lazy {
    ComposeView(context).apply {
      setViewTreeLifecycleOwner(lifecycleOwner)
      setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
      setContent {
        FloaterButtonContent()
      }
    }
  }

  val layoutParams by lazy {
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
    windowManager.addView(view, layoutParams)
    animateToEdges(windowManager)
  }

  fun configureDragGesture(
    windowManager: WindowManager,
    closePlaceholder: FloaterClosePlaceholder?,
  ) {
    (view as View).setOnTouchListener(object : View.OnTouchListener {
      private var lastX = 0
      private var lastY = 0
      private var initialTouchX = 0
      private var initialTouchY = 0

      private var isOnClosePlaceHolder = false
      private var isVibrated = false

      private val isNotMoving: Boolean
        get() {
          val xDifference = (lastX - layoutParams.x).absoluteValue
          val yDifference = (lastY - layoutParams.y).absoluteValue

          return xDifference < 10 && yDifference < 10
        }

      @SuppressLint("ClickableViewAccessibility")
      override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
          MotionEvent.ACTION_DOWN -> {
            lastX = layoutParams.x
            lastY = layoutParams.y
            initialTouchX = event.rawX.toInt()
            initialTouchY = event.rawY.toInt()
            return true
          }

          MotionEvent.ACTION_MOVE -> {
            layoutParams.x = lastX + (event.rawX.toInt() - initialTouchX)
            layoutParams.y = lastY + (event.rawY.toInt() - initialTouchY)

            if (isNotMoving) return true

            closePlaceholder?.show(windowManager)

            if (closePlaceholder?.checkViewIsClose(view, layoutParams) == true) {
              isOnClosePlaceHolder = true
              layoutParams.x = closePlaceholder.layoutParams.x
              layoutParams.y = closePlaceholder.layoutParams.y
            } else {
              isOnClosePlaceHolder = false
              isVibrated = false
            }

            if (isOnClosePlaceHolder && !isVibrated) {
              vibrate()
              isVibrated = true
            }

            windowManager.updateViewLayout(view, layoutParams)
            return true
          }

          MotionEvent.ACTION_UP -> {
            if (isNotMoving) {
              onClick()
            }

            if (isOnClosePlaceHolder) {
              onStopServiceRequest()
              return true
            }

            animateToEdges(windowManager)
            closePlaceholder?.hide(windowManager)
            return true
          }
        }
        return false
      }
    })
  }

  fun animateToEdges(windowManager: WindowManager) {
    val screenWidth = context.resources.displayMetrics.widthPixels
    val screenHeight = context.resources.displayMetrics.heightPixels

    val horizontalDistance = screenWidth / 2 - layoutParams.x.absoluteValue
    val verticalDistance = screenHeight / 2 - layoutParams.y.absoluteValue

    if (horizontalDistance < verticalDistance) {
      val newX = if (layoutParams.x < 0) {
        -(screenWidth / 2)
      } else {
        screenWidth / 2
      }

      val valueAnimator = ValueAnimator.ofInt(layoutParams.x, newX)
      valueAnimator.duration = 300

      valueAnimator.addUpdateListener {
        val value = it.animatedValue as Int
        layoutParams.x = value
        windowManager.updateViewLayout(view, layoutParams)
      }

      valueAnimator.start()
    } else {
      val newY = if (layoutParams.y < 0) {
        -(screenHeight / 2)
      } else {
        screenHeight / 2
      }

      val valueAnimator = ValueAnimator.ofInt(layoutParams.y, newY)
      valueAnimator.duration = 300

      valueAnimator.addUpdateListener {
        val value = it.animatedValue as Int
        layoutParams.y = value
        windowManager.updateViewLayout(view, layoutParams)
      }

      valueAnimator.start()
    }
  }

  @Suppress("DEPRECATION")
  private fun vibrate(millis: Long = 100) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager = context.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
      vibratorManager.defaultVibrator.vibrate(VibrationEffect.createOneShot(millis, 50))
    } else {
      val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
      vibrator.vibrate(millis)
    }
  }

  @Composable
  internal fun FloaterButtonContent() {
    SQAToolsTheme {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Outlined.Memory,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onPrimary,
        )
      }
    }
  }
}
