package com.ryccoatika.sqatoolkit.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatoolkit.common.data.ThemePreferences
import com.ryccoatika.sqatoolkit.common.ui.theme.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
internal class SettingsViewModel(
  private val themePreferences: ThemePreferences,
) : ViewModel() {
  val themeMode: StateFlow<ThemeMode> = themePreferences.themeMode.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = ThemeMode.SYSTEM,
  )

  fun setThemeMode(mode: ThemeMode) {
    viewModelScope.launch { themePreferences.setThemeMode(mode) }
  }
}
