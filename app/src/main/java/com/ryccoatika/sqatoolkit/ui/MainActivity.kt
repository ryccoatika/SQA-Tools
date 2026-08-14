package com.ryccoatika.sqatoolkit.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Handyman
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.AppScreens
import com.ryccoatika.sqatoolkit.R
import com.ryccoatika.sqatoolkit.RootScreen
import com.ryccoatika.sqatoolkit.common.data.ThemePreferences
import com.ryccoatika.sqatoolkit.common.inject.ActivityScope
import com.ryccoatika.sqatoolkit.common.ui.pressable
import com.ryccoatika.sqatoolkit.common.ui.theme.SQAToolsTheme
import com.ryccoatika.sqatoolkit.common.ui.theme.ThemeMode
import com.ryccoatika.sqatoolkit.inject.ApplicationComponent
import com.ryccoatika.sqatoolkit.ui.chatbot.ChatBot
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

class MainActivity : ComponentActivity() {

  private lateinit var component: MainActivityComponent

  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)

    component = MainActivityComponent::class.create(this)

    // The gradient hero bleeds behind the status bar on every top-level screen, so keep the
    // status-bar icons light (white) regardless of the light/dark theme for contrast.
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
    )
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
          RootScreen.ChatBot -> ChatBot()
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
  Box(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 20.dp, vertical = 12.dp),
    contentAlignment = Alignment.Center,
  ) {
    Surface(
      shape = RoundedCornerShape(28.dp),
      color = MaterialTheme.colorScheme.surfaceContainerHigh,
      tonalElevation = 3.dp,
      shadowElevation = 10.dp,
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(6.dp),
      ) {
        for (item in AppNavigationItems) {
          BottomNavItem(
            item = item,
            selected = selectedNavigation == item.screen,
            onClick = { onNavigationSelected(item.screen) },
          )
        }
      }
    }
  }
}

@Composable
private fun BottomNavItem(
  item: AppNavigationItem,
  selected: Boolean,
  onClick: () -> Unit,
) {
  val spec = spring<Color>(stiffness = Spring.StiffnessMediumLow)
  val background by animateColorAsState(
    targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
    animationSpec = spec,
    label = "navItemBg",
  )
  val content by animateColorAsState(
    targetValue = if (selected) {
      MaterialTheme.colorScheme.onPrimary
    } else {
      MaterialTheme.colorScheme.onSurfaceVariant
    },
    animationSpec = spec,
    label = "navItemContent",
  )

  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier
      .clip(RoundedCornerShape(50))
      .background(background)
      .pressable(onClick)
      .padding(horizontal = 16.dp, vertical = 12.dp),
  ) {
    Icon(
      painter = rememberVectorPainter(item.iconImageVector),
      contentDescription = stringResource(item.contentDescriptionResource),
      tint = content,
    )
    AnimatedVisibility(
      visible = selected,
      enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)) +
        expandHorizontally(spring(stiffness = Spring.StiffnessMediumLow, visibilityThreshold = IntSize.VisibilityThreshold)),
      exit = fadeOut(spring(stiffness = Spring.StiffnessMedium)) +
        shrinkHorizontally(spring(stiffness = Spring.StiffnessMediumLow, visibilityThreshold = IntSize.VisibilityThreshold)),
    ) {
      Text(
        text = stringResource(item.labelResource),
        color = content,
        style = MaterialTheme.typography.labelLarge,
        maxLines = 1,
      )
    }
  }
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
