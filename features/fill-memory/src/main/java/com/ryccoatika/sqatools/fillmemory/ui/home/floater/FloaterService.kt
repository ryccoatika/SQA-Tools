package com.ryccoatika.sqatools.fillmemory.ui.home.floater

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.MotionEvent
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
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlin.math.absoluteValue

internal class FloaterService :
  Service(),
  LifecycleOwner,
  SavedStateRegistryOwner {

  private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
  private val savedStateRegistryController = SavedStateRegistryController.create(this)

  private val screenWidth: Int
    get() = resources.displayMetrics.widthPixels
  private val screenHeight: Int
    get() = resources.displayMetrics.heightPixels

  override val lifecycle: Lifecycle
    get() = lifecycleRegistry
  override val savedStateRegistry: SavedStateRegistry
    get() = savedStateRegistryController.savedStateRegistry

  private lateinit var windowManager: WindowManager

  private var contentView: View? = null
  private var closeView: View? = null

  override fun onCreate() {
    super.onCreate()

    windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

    savedStateRegistryController.performAttach()
    savedStateRegistryController.performRestore(null)
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)

    if (contentView != null) {
      animateToEdges(contentView, contentView!!.layoutParams as LayoutParams)
    }
  }

  override fun onDestroy() {
    super.onDestroy()

    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)

    windowManager.removeView(contentView)
    windowManager.removeView(closeView)

    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    show()
    return START_NOT_STICKY
  }

  private fun show() {
    if (contentView != null && closeView != null) return

    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

    contentView = ComposeView(this).apply {
      setViewTreeLifecycleOwner(this@FloaterService)
      setViewTreeSavedStateRegistryOwner(this@FloaterService)
      setContent {
        Floater()
      }
    }
    closeView = ComposeView(this).apply {
      setViewTreeLifecycleOwner(this@FloaterService)
      setViewTreeSavedStateRegistryOwner(this@FloaterService)
      setContent {
        FloaterClose()
      }
    }

    val closeParams = getLayoutParams()
    windowManager.addView(closeView, closeParams)
    closeView?.isVisible = false

    val contentParams = getLayoutParams()
    windowManager.addView(contentView, contentParams)
    configureDrag(contentView, contentParams, closeView, closeParams)
    animateToEdges(contentView, contentParams)
  }

  private fun configureDrag(
    view: View?,
    params: LayoutParams,
    closeView: View?,
    closeParams: LayoutParams,
  ) {
    if (view == null || closeView == null) return
    view.setOnTouchListener(object : View.OnTouchListener {
      private var lastX = 0
      private var lastY = 0
      private var initialTouchX = 0
      private var initialTouchY = 0

      private var isNeedToVibrate = false
      private var isVibrated = false

      private var isClosePlaceholderShowed = false

      @SuppressLint("ClickableViewAccessibility")
      override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
          MotionEvent.ACTION_DOWN -> {
            lastX = params.x
            lastY = params.y
            initialTouchX = event.rawX.toInt()
            initialTouchY = event.rawY.toInt()
            return true
          }

          MotionEvent.ACTION_MOVE -> {
            params.x = lastX + (event.rawX.toInt() - initialTouchX)
            params.y = lastY + (event.rawY.toInt() - initialTouchY)

            val xDifference = (lastX - params.x).absoluteValue
            val yDifference = (lastY - params.y).absoluteValue

            if (xDifference < 10 || yDifference < 10) return true

            if (!isClosePlaceholderShowed) {
              showClosePlaceholder(closeView, closeParams)
              isClosePlaceholderShowed = true
            }

            if (params.closeInto(closeParams, closeView.width)) {
              isNeedToVibrate = true
              params.x = closeParams.x
              params.y = closeParams.y
            } else {
              isNeedToVibrate = false
              isVibrated = false
            }

            if (isNeedToVibrate && !isVibrated) {
              vibrate()
              isVibrated = true
            }

            windowManager.updateViewLayout(view, params)
            return true
          }

          MotionEvent.ACTION_UP -> {
            if (lastX == params.x && lastY == params.y) {
              Log.d("190401", "onTouch: OnClick!!")
            }

            if (params.closeInto(closeParams, closeView.width)) {
              stopSelf()
              return true
            }

            isClosePlaceholderShowed = false
            animateToEdges(view, params)
            hideClosePlaceholder(closeView, closeParams)

            return true
          }
        }
        return false
      }
    })
  }

  private fun animateToEdges(view: View?, params: LayoutParams) {
    if (view == null) return

    val horizontalDistance = screenWidth / 2 - params.x.absoluteValue
    val verticalDistance = screenHeight / 2 - params.y.absoluteValue

    if (horizontalDistance < verticalDistance) {
      val newX = if (params.x < 0) {
        -(screenWidth / 2)
      } else {
        screenWidth / 2
      }

      val valueAnimator = ValueAnimator.ofInt(params.x, newX)
      valueAnimator.duration = 300

      valueAnimator.addUpdateListener {
        val value = it.animatedValue as Int
        params.x = value
        windowManager.updateViewLayout(view, params)
      }

      valueAnimator.start()
    } else {
      val newY = if (params.y < 0) {
        -(screenHeight / 2)
      } else {
        screenHeight / 2
      }

      val valueAnimator = ValueAnimator.ofInt(params.y, newY)
      valueAnimator.duration = 300

      valueAnimator.addUpdateListener {
        val value = it.animatedValue as Int
        params.y = value
        windowManager.updateViewLayout(view, params)
      }

      valueAnimator.start()
    }
  }

  private fun showClosePlaceholder(view: View?, params: LayoutParams) {
    if (view == null) return

    view.isVisible = true
    val valueAnimator = ValueAnimator.ofInt(screenHeight / 2, screenHeight / 4)
    valueAnimator.addUpdateListener {
      val value = it.animatedValue as Int
      params.y = value
      windowManager.updateViewLayout(view, params)
    }
    valueAnimator.start()
  }

  private fun hideClosePlaceholder(view: View?, params: LayoutParams) {
    if (view == null) return

    val valueAnimator = ValueAnimator.ofInt(screenHeight / 4, screenHeight / 2)
    valueAnimator.addUpdateListener {
      val value = it.animatedValue as Int
      params.y = value
      windowManager.updateViewLayout(view, params)
      if (value >= screenHeight / 2) {
        view.isVisible = false
      }
    }
    valueAnimator.start()
  }

  @Suppress("DEPRECATION")
  private fun getLayoutParams(): LayoutParams {
    return LayoutParams(
      LayoutParams.WRAP_CONTENT,
      LayoutParams.WRAP_CONTENT,
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LayoutParams.TYPE_APPLICATION_OVERLAY
      } else {
        LayoutParams.TYPE_PHONE
      },
      LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_LAYOUT_IN_SCREEN,
      PixelFormat.TRANSLUCENT,
    )
  }

  private fun LayoutParams.closeInto(params: LayoutParams, radius: Int): Boolean {
    val rect = Rect(
      x - width / 2,
      y - height / 2,
      x + width / 2,
      y + height / 2,
    )
    val targetRect = Rect(
      params.x - radius,
      params.y - radius,
      params.x + radius,
      params.y + radius,
    )

    return rect.intersect(targetRect)
  }

  @Suppress("DEPRECATION")
  private fun vibrate(millis: Long = 100) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
      vibratorManager.defaultVibrator.vibrate(VibrationEffect.createOneShot(millis, 50))
    } else {
      val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
      vibrator.vibrate(millis)
    }
  }

  @Composable
  private fun Floater() {
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
        tint = Color.White,
      )
    }
  }

  @Composable
  internal fun FloaterClose() {
    Box(
      modifier = Modifier
        .size(54.dp)
        .background(
          brush = Brush.radialGradient(
            colors = listOf(
              MaterialTheme.colorScheme.primary,
              Color.Transparent,
            ),
          ),
          shape = CircleShape,
        )
        .padding(4.dp)
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

  companion object {
    internal fun show(context: Context) {
      val intent = Intent(context, FloaterService::class.java)
      context.startService(intent)
    }
  }
}
