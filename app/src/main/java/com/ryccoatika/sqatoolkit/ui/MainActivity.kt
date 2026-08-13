package com.ryccoatika.sqatoolkit.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import com.ryccoatika.sqatoolkit.AppScreens
import com.ryccoatika.sqatoolkit.R
import com.ryccoatika.sqatoolkit.RootScreen
import com.ryccoatika.sqatoolkit.common.data.ThemePreferences
import com.ryccoatika.sqatoolkit.common.inject.ActivityScope
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.common.ui.theme.ThemeMode
import com.ryccoatika.sqatoolkit.inject.ApplicationComponent
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

class MainActivity : ComponentActivity() {

  private lateinit var component: MainActivityComponent

  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)

    component = MainActivityComponent::class.create(this)

    enableEdgeToEdge()
    setContent {
      val themePreferences = remember { ThemePreferences(this) }
      val themeMode by themePreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
      val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
      }

      SQAToolsTheme(darkTheme = darkTheme) {
        AppContent()
      }
    }
  }

  @Composable
  private fun AppContent(modifier: Modifier = Modifier) {
    val rootScreens = remember { listOf(RootScreen.Tools, RootScreen.ChatBot, RootScreen.Settings) }
    val pagerState = rememberPagerState(pageCount = { rootScreens.size })
    val scope = rememberCoroutineScope()

    Scaffold(
      bottomBar = {
        AppNavigationBar(
          selectedNavigation = rootScreens[pagerState.currentPage],
          onNavigationSelected = { selected ->
            scope.launch { pagerState.animateScrollToPage(rootScreens.indexOf(selected)) }
          },
          modifier = Modifier.fillMaxWidth(),
        )
      },
      contentWindowInsets = WindowInsets(0),
      modifier = modifier,
    ) { paddingValues ->
      HorizontalPager(
        state = pagerState,
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues),
      ) { page ->
        when (rootScreens[page]) {
          RootScreen.Tools -> component.screens.tools()
          RootScreen.ChatBot -> Unit
          RootScreen.Settings -> component.screens.settings()
        }
      }
    }
  }
}

@Composable
private fun AppNavigationBar(
  selectedNavigation: RootScreen,
  onNavigationSelected: (RootScreen) -> Unit,
  modifier: Modifier = Modifier,
) {
  NavigationBar(
    modifier = modifier,
  ) {
    for (item in AppNavigationItems) {
      NavigationBarItem(
        icon = {
          AppNavigationItemIcon(item = item)
        },
        alwaysShowLabel = true,
        label = { Text(text = stringResource(item.labelResource)) },
        selected = selectedNavigation == item.screen,
        onClick = { onNavigationSelected(item.screen) },
      )
    }
  }
}

@Composable
private fun AppNavigationItemIcon(item: AppNavigationItem) {
  Icon(
    painter = rememberVectorPainter(item.iconImageVector),
    contentDescription = stringResource(item.contentDescriptionResource),
  )
}

private data class AppNavigationItem(
  val screen: RootScreen,
  @field:StringRes
  val labelResource: Int,
  @field:StringRes
  val contentDescriptionResource: Int,
  val iconImageVector: ImageVector,
)

private val AppNavigationItems = listOf(
  AppNavigationItem(
    screen = RootScreen.Tools,
    labelResource = R.string.nav_tools,
    contentDescriptionResource = R.string.cd_tools,
    iconImageVector = Icons.Outlined.Handyman,
  ),
  AppNavigationItem(
    screen = RootScreen.ChatBot,
    labelResource = R.string.nav_chat,
    contentDescriptionResource = R.string.cd_chat,
    iconImageVector = Icons.Outlined.Sms,
  ),
  AppNavigationItem(
    screen = RootScreen.Settings,
    labelResource = R.string.nav_settings,
    contentDescriptionResource = R.string.cd_settings,
    iconImageVector = Icons.Outlined.Settings,
  ),
)

@ActivityScope
@Component
abstract class MainActivityComponent(
  @get:Provides val activity: Activity,
  @Component val applicationComponent: ApplicationComponent = ApplicationComponent.from(activity),
) {
  abstract val screens: AppScreens
}
