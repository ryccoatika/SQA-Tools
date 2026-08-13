package com.ryccoatika.sqatoolkit.common.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ryccoatika.sqatoolkit.common.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

@Inject
class ThemePreferences(
  private val context: Context,
) {
  private val themeModeKey = stringPreferencesKey("theme_mode")

  val themeMode: Flow<ThemeMode> = context.themeDataStore.data.map { preferences ->
    when (preferences[themeModeKey]) {
      ThemeMode.LIGHT.name -> ThemeMode.LIGHT
      ThemeMode.DARK.name -> ThemeMode.DARK
      else -> ThemeMode.SYSTEM
    }
  }

  suspend fun setThemeMode(mode: ThemeMode) {
    context.themeDataStore.edit { it[themeModeKey] = mode.name }
  }
}
