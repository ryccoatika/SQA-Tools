package com.ryccoatika.sqatoolkit.devinfo

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ryccoatika.sqatoolkit.common.data.ThemePreferences
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.devinfo.core.utils.DevInfoTextCreator
import com.ryccoatika.sqatoolkit.devinfo.inject.DevInfoScope
import com.ryccoatika.sqatoolkit.devinfo.ui.FeatureScreens
import com.ryccoatika.sqatoolkit.devinfo.ui.common.utils.LocalTextCreator
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoTypes
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

internal class DevInfoActivity : ComponentActivity() {
  private lateinit var component: DevInfoComponent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    component = DevInfoComponent::class.create(this)

    enableEdgeToEdge()

    setContent {
      val navController = rememberNavController()
      val scope = rememberCoroutineScope()
      val themePreferences = remember { ThemePreferences(this) }
      val dynamicColor by themePreferences.useDynamicColor.collectAsState(initial = false)

      CompositionLocalProvider(
        LocalTextCreator provides DevInfoTextCreator(this),
      ) {
        SQAToolsTheme(dynamicColor = dynamicColor) {
          NavHost(
            navController = navController,
            startDestination = Route.Home.route,
          ) {
            composable(
              route = Route.Home.route,
            ) {
              component.screens.main(
                { finish() },
                dynamicColor,
                { scope.launch { themePreferences.setDynamicColor(!dynamicColor) } },
              )
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

@DevInfoScope
@Component
internal abstract class DevInfoComponent(
  @get:Provides val context: Context,
) : DevInfoTypes {
  abstract val screens: FeatureScreens

  @Provides
  fun provideTextCreator(context: Context): DevInfoTextCreator = DevInfoTextCreator(context)
}
