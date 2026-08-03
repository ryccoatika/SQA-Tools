package com.ryccoatika.sqatoolkit.common.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_prefs")

class ThemePreferences(
  private val context: Context,
) {
  private val useDynamicColorKey = booleanPreferencesKey("use_dynamic_color")

  val useDynamicColor: Flow<Boolean> =
    context.themeDataStore.data.map { it[useDynamicColorKey] ?: false }

  suspend fun setDynamicColor(enabled: Boolean) {
    context.themeDataStore.edit { it[useDynamicColorKey] = enabled }
  }
}
