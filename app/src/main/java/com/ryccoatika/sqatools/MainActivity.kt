package com.ryccoatika.sqatools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.ryccoatika.sqatools.ui.theme.SQAToolsTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      SQAToolsTheme {
        AppContent()
      }
    }
  }
}

@Composable
private fun AppContent(modifier: Modifier = Modifier) {
  val navController = rememberNavController()

  Scaffold(
    bottomBar = {
      val currentSelectedItem by navController.currentScreenAsState()

      AppNavigationBar(
        selectedNavigation = currentSelectedItem,
        onNavigationSelected = { selected ->
          navController.navigate(selected.route) {
            launchSingleTop = true
            restoreState = true

            popUpTo(navController.graph.findStartDestination().id) {
              saveState = true
            }
          }
        },
        modifier = Modifier.fillMaxWidth(),
      )
    },
    contentWindowInsets = ScaffoldDefaults.contentWindowInsets
      .exclude(WindowInsets.statusBars),
    modifier = modifier,
  ) { paddingValues ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(paddingValues),
    ) {
      AppNavigation(
        navController = navController,
        modifier = Modifier.weight(1f),
      )
    }
  }
}

@Composable
private fun NavController.currentScreenAsState(): State<RootScreen> {
  val selectedItem = remember { mutableStateOf<RootScreen>(RootScreen.Home) }

  DisposableEffect(this) {
    val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
      when {
        destination.hierarchy.any { it.route == RootScreen.Home.route } -> {
          selectedItem.value = RootScreen.Home
        }
        destination.hierarchy.any { it.route == RootScreen.ChatBot.route } -> {
          selectedItem.value = RootScreen.ChatBot
        }
        destination.hierarchy.any { it.route == RootScreen.Settings.route } -> {
          selectedItem.value = RootScreen.Settings
        }
      }
    }
    addOnDestinationChangedListener(listener)

    onDispose {
      removeOnDestinationChangedListener(listener)
    }
  }

  return selectedItem
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
          AppNavigationItemIcon(
            item = item,
            selected = selectedNavigation == item.screen,
          )
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
private fun AppNavigationItemIcon(item: AppNavigationItem, selected: Boolean) {
  val painter = rememberVectorPainter(item.iconImageVector)
  val selectedPainter = item.selectedImageVector?.let { rememberVectorPainter(it) }

  if (selectedPainter != null) {
    Crossfade(
      targetState = selected,
      label = item.screen.route,
    ) {
      Icon(
        painter = if (it) selectedPainter else painter,
        contentDescription = stringResource(item.contentDescriptionResource),
      )
    }
  } else {
    Icon(
      painter = painter,
      contentDescription = stringResource(item.contentDescriptionResource),
    )
  }
}

private data class AppNavigationItem(
  val screen: RootScreen,
  @StringRes
  val labelResource: Int,
  @StringRes
  val contentDescriptionResource: Int,
  val iconImageVector: ImageVector,
  val selectedImageVector: ImageVector? = null,
)

private val AppNavigationItems = listOf(
  AppNavigationItem(
    screen = RootScreen.Home,
    labelResource = R.string.nav_home,
    contentDescriptionResource = R.string.cd_home,
    iconImageVector = Icons.Outlined.Home,
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
