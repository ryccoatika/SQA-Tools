package com.ryccoatika.sqatoolkit.fillmemory

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.fillmemory.core.utils.FillMemoryTextCreator
import com.ryccoatika.sqatoolkit.fillmemory.inject.FillMemoryScope
import com.ryccoatika.sqatoolkit.fillmemory.ui.FeatureScreens
import com.ryccoatika.sqatoolkit.fillmemory.ui.common.utils.LocalTextCreator
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

internal class FillMemoryActivity : ComponentActivity() {
  private lateinit var component: FillMemoryComponent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    component = FillMemoryComponent::class.create(this)

    enableEdgeToEdge()

    setContent {
      val navController = rememberNavController()

      CompositionLocalProvider(
        LocalTextCreator provides FillMemoryTextCreator(this),
      ) {
        SQAToolsTheme {
          NavHost(
            navController = navController,
            startDestination = Route.Home.route,
          ) {
            composable(
              route = Route.Home.route,
            ) {
              component.screens.home {
                // navigateUp
                finish()
              }
            }
          }
        }
      }
    }
  }

  sealed class Route(val route: String) {
    data object Home : Route("home")
  }
}

@FillMemoryScope
@Component
internal abstract class FillMemoryComponent(
  @get:Provides val context: Context,
) {
  abstract val screens: FeatureScreens
}
