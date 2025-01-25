package com.ryccoatika.sqatools.fillmemory.ui.home.floater.service

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.IBinder
import android.view.WindowManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.ryccoatika.sqatools.fillmemory.FillMemoryActivity
import com.ryccoatika.sqatools.fillmemory.inject.FillMemoryScope
import com.ryccoatika.sqatools.fillmemory.ui.home.floater.FloaterFillMemory
import com.ryccoatika.sqatools.fillmemory.ui.home.floater.FloaterFillMemoryViewModel
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

internal class FloaterService :
  Service(),
  LifecycleOwner,
  SavedStateRegistryOwner {

  private lateinit var component: FloaterFillMemoryComponent

  private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
  private val savedStateRegistryController = SavedStateRegistryController.create(this)

  override val lifecycle: Lifecycle
    get() = lifecycleRegistry
  override val savedStateRegistry: SavedStateRegistry
    get() = savedStateRegistryController.savedStateRegistry

  private lateinit var windowManager: WindowManager

  private var floaterButtonView: FloaterButton? = null
  private var closePlaceholderView: FloaterClosePlaceholder? = null
  private var contentView: FloaterFillMemory? = null

  override fun onCreate() {
    super.onCreate()

    component = FloaterFillMemoryComponent::class.create(this)

    windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    floaterButtonView = FloaterButton(
      context = this,
      lifecycleOwner = this,
      savedStateRegistryOwner = this,
      onStopServiceRequest = ::stopSelf,
      onClick = {
        contentView?.show(windowManager)
      },
    )
    closePlaceholderView = FloaterClosePlaceholder(
      context = this,
      lifecycleOwner = this,
      savedStateRegistryOwner = this,
    )
    contentView = FloaterFillMemory(
      context = this,
      viewModel = component.floaterFillMemoryViewModel,
      lifecycleOwner = this,
      savedStateRegistryOwner = this,
      openApp = ::openApp,
    )

    savedStateRegistryController.performAttach()
    savedStateRegistryController.performRestore(null)
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)

    floaterButtonView?.animateToEdges(windowManager)
  }

  override fun onDestroy() {
    super.onDestroy()

    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)

    floaterButtonView = null
    closePlaceholderView = null
    contentView = null

    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  }

  private fun openApp() {
    val packageName = applicationContext.packageName
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    val isActivityRunning = activityManager.appTasks.any { task ->
      task.taskInfo.baseIntent.component?.packageName == packageName
    }

    if (isActivityRunning) {
      val intent = Intent(applicationContext, FillMemoryActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
      }
      startActivity(intent)
    } else {
      val intent = Intent(applicationContext, FillMemoryActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      }
      startActivity(intent)
    }
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    show()
    return START_NOT_STICKY
  }

  private fun show() {
    if (lifecycleRegistry.currentState == Lifecycle.State.RESUMED) return

    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

    floaterButtonView?.show(windowManager)
    floaterButtonView?.configureDragGesture(windowManager, closePlaceholderView)
  }

  companion object {
    internal fun show(context: Context) {
      val intent = Intent(context, FloaterService::class.java)
      context.startService(intent)
    }
  }
}

@FillMemoryScope
@Component
internal abstract class FloaterFillMemoryComponent(
  @get:Provides val context: Context,
) {
  abstract val floaterFillMemoryViewModel: FloaterFillMemoryViewModel
}
