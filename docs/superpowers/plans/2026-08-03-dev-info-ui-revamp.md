# Dev-Info UI Revamp Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Restyle the dev-info feature for QA engineers — branded theme (app-wide) with a dynamic-color toggle, compact monospace copy-rows with status color-coding, restyled cards/hero/tabs with icons, and global search + full-report share.

**Architecture:** Branded Material3 theme + `StatusColors` in the shared `common` module. A single aggregator `DevInfoViewModel` runs all 7 `Get{X}Info` usecases and feeds the pager, global search, and export — replacing the 7 per-tab ViewModels. UI-only status classifier + report formatter derive from existing `label`+`value`; the data layer is untouched.

**Tech Stack:** Kotlin, Jetpack Compose (Material3, material-icons-extended), kotlin-inject (tatarka) DI, coroutines, DataStore Preferences, Android share intent, `LocalClipboardManager`.

## Global Constraints

- minSdk 24, targetSdk 36, compileSdk 37. Guard any API > 24 with `Build.VERSION.SDK_INT`.
- **Module compiles with `-Werror` (`allWarningsAsErrors`)** — no unused imports/symbols, deprecated APIs need scoped `@Suppress("DEPRECATION")` (never inline inside a string template — hoist to an annotated `val`).
- **No unit-test infra; do NOT add test deps.** Verification = `./gradlew :features:dev-info:compileDebugKotlin` then `:app:assembleDebug`, plus manual smoke.
- 2-space indentation. kotlin-inject: `@Inject` on constructors; ViewModels use the `com.ryccoatika.sqatoolkit.common.extensions.viewModel(factory)` extension.
- Data layer (`Get{X}Info`, `*InfoUtils`, `core/model/Item.kt`) is OUT OF SCOPE — do not modify item models or gathering. Copy/search/status/export all derive from existing `label`+`value`.
- `material-icons-extended` is available transitively (`common` has `api(libs.androidx.materialIcons)`); use `androidx.compose.material.icons.*` freely.
- Theme changes in `common` restyle the whole app — that is intended.

---

### Task 1: Branded theme + StatusColors + typography (common module)

**Files:**
- Create: `common/src/main/java/com/ryccoatika/sqatoolkit/common/ui/theme/BrandColor.kt`
- Create: `common/src/main/java/com/ryccoatika/sqatoolkit/common/ui/theme/StatusColors.kt`
- Modify: `common/src/main/java/com/ryccoatika/sqatoolkit/common/ui/theme/Theme.kt`
- Modify: `common/src/main/java/com/ryccoatika/sqatoolkit/common/ui/theme/Type.kt`

**Interfaces:**
- Produces: branded `LightColorScheme`/`DarkColorScheme`; `SQAToolsTheme(darkTheme, dynamicColor: Boolean = false, content)`; `data class StatusColors(success, warning, error, neutral)` + `LocalStatusColors` + `statusColors(darkTheme): StatusColors`; refined `Typography` incl. a `monoValue` style helper.

- [ ] **Step 1: BrandColor.kt — fixed light+dark schemes**

```kotlin
package com.ryccoatika.sqatoolkit.common.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Brand: indigo primary, teal secondary, slate neutrals — a "developer tool" identity.
internal val BrandLightColorScheme = lightColorScheme(
  primary = Color(0xFF3B5BDB),
  onPrimary = Color(0xFFFFFFFF),
  primaryContainer = Color(0xFFDCE3FF),
  onPrimaryContainer = Color(0xFF00164F),
  secondary = Color(0xFF0CA678),
  onSecondary = Color(0xFFFFFFFF),
  secondaryContainer = Color(0xFFB8F5E4),
  onSecondaryContainer = Color(0xFF00201A),
  tertiary = Color(0xFF7048E8),
  onTertiary = Color(0xFFFFFFFF),
  background = Color(0xFFFAFAFC),
  onBackground = Color(0xFF1A1B1F),
  surface = Color(0xFFFAFAFC),
  onSurface = Color(0xFF1A1B1F),
  surfaceVariant = Color(0xFFE2E3EA),
  onSurfaceVariant = Color(0xFF45464E),
  surfaceContainer = Color(0xFFF0F1F5),
  surfaceContainerHigh = Color(0xFFEAEBF0),
  outline = Color(0xFF767781),
  outlineVariant = Color(0xFFC6C7D0),
  error = Color(0xFFC0392B),
  onError = Color(0xFFFFFFFF),
)

internal val BrandDarkColorScheme = darkColorScheme(
  primary = Color(0xFFB3C5FF),
  onPrimary = Color(0xFF002585),
  primaryContainer = Color(0xFF1F3B9E),
  onPrimaryContainer = Color(0xFFDCE3FF),
  secondary = Color(0xFF5BE0BE),
  onSecondary = Color(0xFF003731),
  secondaryContainer = Color(0xFF005046),
  onSecondaryContainer = Color(0xFFB8F5E4),
  tertiary = Color(0xFFCDBEFF),
  onTertiary = Color(0xFF3A1D8A),
  background = Color(0xFF121317),
  onBackground = Color(0xFFE3E2E9),
  surface = Color(0xFF121317),
  onSurface = Color(0xFFE3E2E9),
  surfaceVariant = Color(0xFF45464E),
  onSurfaceVariant = Color(0xFFC6C7D0),
  surfaceContainer = Color(0xFF1E1F25),
  surfaceContainerHigh = Color(0xFF282A31),
  outline = Color(0xFF90919B),
  outlineVariant = Color(0xFF45464E),
  error = Color(0xFFFF897D),
  onError = Color(0xFF690000),
)
```

- [ ] **Step 2: StatusColors.kt**

```kotlin
package com.ryccoatika.sqatoolkit.common.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class StatusColors(
  val success: Color,
  val warning: Color,
  val error: Color,
  val neutral: Color,
)

internal val LightStatusColors = StatusColors(
  success = Color(0xFF2B8A3E),
  warning = Color(0xFFE67700),
  error = Color(0xFFC0392B),
  neutral = Color(0xFF767781),
)

internal val DarkStatusColors = StatusColors(
  success = Color(0xFF69DB7C),
  warning = Color(0xFFFFC078),
  error = Color(0xFFFF897D),
  neutral = Color(0xFF90919B),
)

val LocalStatusColors = staticCompositionLocalOf { LightStatusColors }
```

- [ ] **Step 3: Theme.kt — brand default + status provider**

Rewrite `SQAToolsTheme` to default to branded, provide `LocalStatusColors`, and keep dynamic as an opt-in:

```kotlin
@Composable
fun SQAToolsTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> BrandDarkColorScheme
    else -> BrandLightColorScheme
  }
  val statusColors = if (darkTheme) DarkStatusColors else LightStatusColors

  CompositionLocalProvider(LocalStatusColors provides statusColors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content,
    )
  }
}
```

Remove the now-unused old `DarkColorScheme`/`LightColorScheme` (Purple*) definitions and their `Color.kt` values IF nothing else references them (grep `Purple80|Purple40|PurpleGrey|Pink80|Pink40` across the repo first; keep any still referenced). Add the `CompositionLocalProvider` import.

- [ ] **Step 4: Type.kt — hierarchy + mono value style**

Extend `Typography` with `titleLarge`, `titleMedium`, `labelLarge`, `labelMedium`, `bodyMedium` (reasonable Material sizes), keep `bodyLarge`. Add a public reusable style:

```kotlin
val MonoValueTextStyle = TextStyle(
  fontFamily = FontFamily.Monospace,
  fontWeight = FontWeight.Normal,
  fontSize = 14.sp,
  lineHeight = 20.sp,
  letterSpacing = 0.sp,
)
```

- [ ] **Step 5: Compile & commit**

Run: `./gradlew :app:compileDebugKotlin` → BUILD SUCCESSFUL (theme is app-wide; compile the app, not just dev-info).

```bash
git add common/src/main/java/com/ryccoatika/sqatoolkit/common/ui/theme/
git commit -m "feat(theme): branded palette, status colors, typography"
```

---

### Task 2: Dynamic-color preference (DataStore) + activity wiring

**Files:**
- Modify: `gradle/libs.versions.toml` (add datastore-preferences)
- Modify: `common/build.gradle.kts` (add dependency)
- Create: `common/src/main/java/com/ryccoatika/sqatoolkit/common/data/ThemePreferences.kt`
- Modify: `features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/DevInfoActivity.kt`

**Interfaces:**
- Produces: `ThemePreferences(context)` with `val useDynamicColor: Flow<Boolean>` and `suspend fun setDynamicColor(enabled: Boolean)`; activity reads it and passes `dynamicColor` to `SQAToolsTheme`, and exposes a toggle callback to `Main`.

- [ ] **Step 1: Add DataStore dependency**

In `gradle/libs.versions.toml`, under `[versions]` add `datastore = "1.1.1"`, and under `[libraries]`:
```toml
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }
```
In `common/build.gradle.kts` dependencies add `api(libs.androidx.datastore.preferences)` (api so features can use it).

- [ ] **Step 2: ThemePreferences**

```kotlin
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
```

- [ ] **Step 3: Wire into DevInfoActivity**

In `DevInfoActivity.onCreate`, create `val themePreferences = ThemePreferences(this)`, collect it as state, and pass to the theme; provide a toggle lambda to `Main`. Concretely:

```kotlin
setContent {
  val navController = rememberNavController()
  val scope = rememberCoroutineScope()
  val themePreferences = remember { ThemePreferences(this) }
  val dynamicColor by themePreferences.useDynamicColor.collectAsState(initial = false)

  CompositionLocalProvider(LocalTextCreator provides DevInfoTextCreator(this)) {
    SQAToolsTheme(dynamicColor = dynamicColor) {
      NavHost(navController = navController, startDestination = Route.Home.route) {
        composable(route = Route.Home.route) {
          component.screens.main(
            navigateUp = { finish() },
            dynamicColor = dynamicColor,
            onToggleDynamicColor = { scope.launch { themePreferences.setDynamicColor(!dynamicColor) } },
          )
        }
      }
    }
  }
}
```

Add imports: `androidx.compose.runtime.collectAsState`, `getValue`, `remember`, `rememberCoroutineScope`, `kotlinx.coroutines.launch`, `com.ryccoatika.sqatoolkit.common.data.ThemePreferences`. NOTE: the `Main` typealias signature changes in Task 5 — this step's call site (with `dynamicColor`/`onToggleDynamicColor`) must match the `Main` signature defined in Task 5. If implementing Task 2 before Task 5, temporarily call `component.screens.main(navigateUp = { finish() })` and add the two args when Task 5 lands. (Executor note: Tasks 2 and 5 both touch this call site — the controller may merge them; otherwise keep the temporary call and update in Task 5.)

- [ ] **Step 4: Compile & commit**

Run: `./gradlew :app:assembleDebug` → BUILD SUCCESSFUL.

```bash
git add gradle/libs.versions.toml common/build.gradle.kts common/src/main/java/com/ryccoatika/sqatoolkit/common/data/ThemePreferences.kt features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/DevInfoActivity.kt
git commit -m "feat(theme): persist dynamic-color choice via DataStore"
```

---

### Task 3: Aggregator DevInfoViewModel (replaces 7 per-tab VMs)

**Files:**
- Create: `.../ui/main/DevInfoViewModel.kt`
- Create: `.../ui/main/DevInfoViewState.kt` (with `TabData`)
- Delete: 7 `.../ui/info/*/{X}ViewModel.kt`, `.../ui/info/common/InfoViewModel.kt`, `.../ui/info/common/InfoViewState.kt`
- Modify: 7 tab files `.../ui/info/*/{X}.kt` — remove the `@Composable` typealias functions + VM-backed/stateless overloads, KEEP only the `{X}Type : DevInfoType` class
- Delete: `.../ui/FeatureScreens.kt`'s `DevInfoScreens` usage of tab composables — see Task 5 (Main no longer needs `DevInfoScreens`). In THIS task, delete `.../ui/info/DevInfoScreens.kt`'s `DevInfoScreens` class but KEEP the `DevInfoTypes` interface (the `@Provides @IntoSet` type registrations). If they are in the same file, keep the file with only `DevInfoTypes`.

**Interfaces:**
- Produces:
  - `data class TabData(val type: DevInfoType, val items: List<Item>, val isLoading: Boolean)`
  - `data class DevInfoViewState(val tabs: List<TabData>, val query: String)` with `Empty`
  - `DevInfoViewModel(...)` : `ViewModel` — injects all 7 usecases + `Set<DevInfoType>`; `val state: StateFlow<DevInfoViewState>`; `fun refresh(type: DevInfoType)`; `fun onQueryChange(q: String)`.
- Consumes: `Get{Device,Software,Hardware,Camera,Network,Connectivity,Sensor}Info`, `DevInfoType`, `Item`.

- [ ] **Step 1: DevInfoViewState + TabData**

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.ui.main

import androidx.compose.runtime.Immutable
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType

@Immutable
internal data class TabData(
  val type: DevInfoType,
  val items: List<Item>,
  val isLoading: Boolean,
)

@Immutable
internal data class DevInfoViewState(
  val tabs: List<TabData>,
  val query: String,
) {
  companion object {
    val Empty = DevInfoViewState(tabs = emptyList(), query = "")
  }
}
```

- [ ] **Step 2: DevInfoViewModel**

Maps each `DevInfoType` (by `id`) to its usecase, orders by `DevInfoType.order`, loads all on init, supports per-type refresh:

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatoolkit.common.ResultInteractor
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetCameraInfo
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetConnectivityInfo
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetDeviceInfo
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetHardwareInfo
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetNetworkInfo
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetSensorInfo
import com.ryccoatika.sqatoolkit.devinfo.core.usecase.GetSoftwareInfo
import com.ryccoatika.sqatoolkit.devinfo.ui.info.DevInfoType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
internal class DevInfoViewModel(
  types: Set<DevInfoType>,
  getDeviceInfo: GetDeviceInfo,
  getSoftwareInfo: GetSoftwareInfo,
  getHardwareInfo: GetHardwareInfo,
  getCameraInfo: GetCameraInfo,
  getNetworkInfo: GetNetworkInfo,
  getConnectivityInfo: GetConnectivityInfo,
  getSensorInfo: GetSensorInfo,
) : ViewModel() {
  private val useCases: Map<String, ResultInteractor<Unit, List<Item>>> = mapOf(
    DevInfoType.DEVICE_ID to getDeviceInfo,
    DevInfoType.SOFTWARE_ID to getSoftwareInfo,
    DevInfoType.HARDWARE_ID to getHardwareInfo,
    DevInfoType.CAMERA_ID to getCameraInfo,
    DevInfoType.NETWORK_ID to getNetworkInfo,
    DevInfoType.CONNECTIVITY_ID to getConnectivityInfo,
    DevInfoType.SENSOR_ID to getSensorInfo,
  )

  private val orderedTypes = types.sortedBy { it.order }
  private val itemsByType = MutableStateFlow<Map<String, List<Item>>>(emptyMap())
  private val loadingIds = MutableStateFlow<Set<String>>(orderedTypes.map { it.id }.toSet())
  private val query = MutableStateFlow("")

  val state: StateFlow<DevInfoViewState> = combine(
    itemsByType,
    loadingIds,
    query,
  ) { items, loading, q ->
    DevInfoViewState(
      tabs = orderedTypes.map { type ->
        TabData(
          type = type,
          items = items[type.id].orEmpty(),
          isLoading = type.id in loading,
        )
      },
      query = q,
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = DevInfoViewState.Empty,
  )

  init {
    orderedTypes.forEach { load(it.id) }
  }

  fun refresh(type: DevInfoType) = load(type.id)

  fun onQueryChange(q: String) {
    query.value = q
  }

  private fun load(id: String) {
    val useCase = useCases[id] ?: return
    viewModelScope.launch {
      loadingIds.update { it + id }
      val result = useCase.executeSync(Unit)
      itemsByType.update { it + (id to result) }
      loadingIds.update { it - id }
    }
  }
}
```

- [ ] **Step 3: Strip tab files to their `{X}Type` only**

For each of `device/Device.kt`, `software/Software.kt`, `hardware/Hardware.kt`, `camera/Camera.kt`, `network/Network.kt`, `connectivity/Connectivity.kt`, `sensor/Sensor.kt`: delete the `typealias {X}` line, all `@Composable fun {X}(...)` overloads, and now-unused imports; KEEP only the `@Inject internal class {X}Type : DevInfoType { ... }` (and its imports: `DevInfoType`, `R`, `me.tatarka.inject.annotations.Inject`). Then delete the sibling `{X}ViewModel.kt` files.

- [ ] **Step 4: Delete shared VM base + DevInfoScreens class**

Delete `ui/info/common/InfoViewModel.kt` and `ui/info/common/InfoViewState.kt`. In `ui/info/DevInfoScreens.kt`, delete the `DevInfoScreens` class (it referenced the removed tab composables); KEEP the `DevInfoTypes` interface with all `@Provides @IntoSet` type bindings. Remove now-unused imports of the tab composables.

- [ ] **Step 5: Compile note**

`FeatureScreens.kt` and `Main.kt` still reference the old wiring; they are rewritten in Task 5. This task will NOT compile standalone. Do the compile + commit at the END of Task 5 together with the Main rewrite. (Executor note: Tasks 3, 4, 5 form one compilable unit — implement them together, commit once. This plan keeps them as separate tasks for review clarity, but the build gate is at the end of Task 5.)

Do NOT commit yet — proceed to Task 4 and 5, single commit at end of Task 5.

---

### Task 4: Status classifier + report formatter + search filter

**Files:**
- Create: `.../ui/common/utils/StatusClassifier.kt`
- Create: `.../ui/main/DevInfoReportFormatter.kt`  (UI layer — it references `TabData`/`DevInfoType`, so it must NOT live in `core/`, to avoid a core→ui dependency inversion)
- Create: `.../ui/main/SearchFilter.kt`

**Interfaces:**
- Produces:
  - `fun statusKindOf(value: String): StatusKind` where `enum class StatusKind { SUCCESS, WARNING, ERROR, NEUTRAL }`, and `@Composable fun statusColorFor(value: String): Color?` (null = no status tint) reading `LocalStatusColors`.
  - `DevInfoReportFormatter(context, textCreator)` : `fun format(tabs: List<TabData>): String`.
  - `fun filterItems(items: List<Item>, query: String): List<Item>`.

- [ ] **Step 1: StatusClassifier**

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.ui.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ryccoatika.sqatoolkit.common.ui.theme.LocalStatusColors

internal enum class StatusKind { SUCCESS, WARNING, ERROR, NEUTRAL }

internal fun statusKindOf(value: String): StatusKind = when (value.trim().lowercase()) {
  "supported", "yes", "enforcing", "detected", "ready", "true", "enabled" -> StatusKind.SUCCESS
  "permissive", "unknown", "roaming", "not charging" -> StatusKind.WARNING
  "not supported", "no", "not detected", "absent", "not installed", "false", "disabled", "not available" -> StatusKind.ERROR
  else -> StatusKind.NEUTRAL
}

@Composable
internal fun statusColorFor(value: String): Color? {
  val colors = LocalStatusColors.current
  return when (statusKindOf(value)) {
    StatusKind.SUCCESS -> colors.success
    StatusKind.WARNING -> colors.warning
    StatusKind.ERROR -> colors.error
    StatusKind.NEUTRAL -> null
  }
}
```

- [ ] **Step 2: DevInfoReportFormatter**

Formats all tabs into a shareable text report. Handles the item types by reading their `label`/`value` (uses `DevInfoTextCreator` where a `Label` enum needs resolving):

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.ui.main

import android.content.Context
import com.ryccoatika.sqatoolkit.devinfo.core.model.DateItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.DeviceCardItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.ElapsedTimeItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.ExpandableGroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.GroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.model.PermissionItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.StatusItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.TextItem
import me.tatarka.inject.annotations.Inject

// Lives in ui.main (same package as TabData) — do not move to core/.
@Inject
internal class DevInfoReportFormatter(
  private val context: Context,
  private val textCreator: DevInfoTextCreator,
) {
  fun format(tabs: List<TabData>): String = buildString {
    appendLine("# Device Info Report")
    appendLine()
    tabs.forEach { tab ->
      appendLine("## ${context.getString(tab.type.featureTitle)}")
      tab.items.forEach { appendItem(it, indent = 0) }
      appendLine()
    }
  }

  private fun StringBuilder.appendItem(item: Item, indent: Int) {
    val pad = "  ".repeat(indent)
    when (item) {
      is RawTextItem -> appendLine("$pad${item.label}: ${item.value}")
      is TextItem -> appendLine("$pad${textCreator.itemLabel(item.label)}: ${item.value}")
      is StatusItem -> appendLine("$pad${textCreator.itemLabel(item.label)}: ${item.value}")
      is DateItem -> appendLine("$pad${textCreator.itemLabel(item.label)}: ${textCreator.longDateFormat(item.value)}")
      is ElapsedTimeItem -> appendLine("$pad${textCreator.itemLabel(item.label)}: ${textCreator.dateElapsedFormat(item.value)}")
      is PermissionItem -> appendLine("$pad${item.label}: ${item.value}")
      is DeviceCardItem -> appendLine("$pad${item.androidName} (${item.internalCodename}) — API ${item.sdkVersion}")
      is GroupItem -> {
        item.rawTitle?.let { appendLine("$pad[$it]") }
          ?: item.label?.let { appendLine("$pad[${textCreator.itemLabel(it)}]") }
        item.items.forEach { appendItem(it, indent + 1) }
      }
      is ExpandableGroupItem -> {
        appendLine("$pad[${item.title}] ${item.summary}")
        item.items.forEach { appendItem(it, indent + 1) }
      }
    }
  }
}
```

Note: confirm `DevInfoTextCreator` exposes `itemLabel(Label)`, `longDateFormat(Instant?)`, `dateElapsedFormat(Instant?)` (it does — used by existing composers). If a signature differs, adapt.

- [ ] **Step 3: SearchFilter**

```kotlin
package com.ryccoatika.sqatoolkit.devinfo.ui.main

import com.ryccoatika.sqatoolkit.devinfo.core.model.ExpandableGroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.GroupItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.Item
import com.ryccoatika.sqatoolkit.devinfo.core.model.PermissionItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.RawTextItem
import com.ryccoatika.sqatoolkit.devinfo.core.model.TextItem

internal fun filterItems(items: List<Item>, query: String): List<Item> {
  if (query.isBlank()) return items
  val q = query.trim()
  return items.mapNotNull { filterItem(it, q) }
}

private fun filterItem(item: Item, q: String): Item? = when (item) {
  is RawTextItem -> item.takeIf { it.label.contains(q, true) || it.value.contains(q, true) }
  is PermissionItem -> item.takeIf { it.label.contains(q, true) || it.value.contains(q, true) }
  is TextItem -> item.takeIf { it.value.contains(q, true) }
  is GroupItem -> {
    val titleMatch = item.rawTitle?.contains(q, true) == true
    if (titleMatch) item else {
      val kids = item.items.mapNotNull { filterItem(it, q) }
      if (kids.isEmpty()) null else item.copy(items = kids)
    }
  }
  is ExpandableGroupItem -> {
    val headMatch = item.title.contains(q, true) || item.summary.contains(q, true)
    if (headMatch) item else {
      val kids = item.items.mapNotNull { filterItem(it, q) }
      if (kids.isEmpty()) null else item.copy(items = kids)
    }
  }
  else -> null // DeviceCardItem/StatusItem/DateItem/ElapsedTime: dropped from search results (no free-text label here); acceptable per spec
}
```

Note: `StatusItem`/`DateItem`/`ElapsedTimeItem` use `Label` enums (no free-text) and are label-resolved only in the UI layer; they are excluded from search matching to keep `filterItems` free of Compose/Context. This is acceptable — those appear on the Device/Software tabs which are also browsable. If broader matching is wanted later, resolve labels upstream.

- [ ] **Step 4: (compile deferred to Task 5)** — these are referenced by Task 5. No standalone commit.

---

### Task 5: Restyle composers + Main rewrite (rows, cards, hero, tabs, search, share)

**Files:**
- Modify: `.../ui/common/ListItemText.kt` (compact mono copy-row)
- Modify: `.../ui/common/ItemGroupComposer.kt` (icon header + copy-group)
- Modify: `.../ui/common/ItemDeviceCardComposer.kt` (hero)
- Modify: `.../ui/common/ItemExpandableGroupComposer.kt` (restyle)
- Modify: `.../ui/common/ItemStatusComposer.kt`, `ItemTextComposer.kt`, `ItemDateComposer.kt` (adopt new row) — verify they route through `ListItemText`; if so no change needed beyond status color
- Create: `.../ui/common/GroupIcons.kt` (title→icon), `.../ui/main/TabIcons.kt` (type→icon)
- Modify: `.../ui/main/Main.kt` (full rewrite: aggregator VM, icon chips, search, share, snackbar, permission launcher)
- Modify: `.../ui/FeatureScreens.kt` (Main typealias signature)

**Interfaces:**
- Consumes: `DevInfoViewModel`, `DevInfoViewState`, `TabData`, `filterItems`, `DevInfoReportFormatter`, `statusColorFor`, `LocalClipboardManager`, `LocalStatusColors`, `MonoValueTextStyle`.
- Produces: new `Main` typealias `@Composable (navigateUp: () -> Unit, dynamicColor: Boolean, onToggleDynamicColor: () -> Unit) -> Unit`.

- [ ] **Step 1: Copy infra — clipboard local + snackbar**

Add a `LocalSnackbarHostState` and a copy helper. In `ui/common/utils/DevInfoCompositionLocal.kt` add:
```kotlin
internal val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> { error("LocalSnackbarHostState not provided") }
```
(import `androidx.compose.material3.SnackbarHostState`). `Main` provides it (Step 8).

- [ ] **Step 2: ListItemText → compact mono copy-row**

Rewrite so the row shows label + monospace value, tinted by status, copy-on-click:
```kotlin
@Composable
internal fun ListItemText(
  label: String,
  value: String,
  modifier: Modifier = Modifier,
) {
  val clipboard = LocalClipboardManager.current
  val snackbar = LocalSnackbarHostState.current
  val scope = rememberCoroutineScope()
  val statusColor = statusColorFor(value)

  Row(
    modifier = modifier
      .fillMaxWidth()
      .clickable {
        clipboard.setText(AnnotatedString(value))
        scope.launch { snackbar.showSnackbar("Copied $label") }
      }
      .padding(horizontal = 16.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Text(
        text = value,
        style = MonoValueTextStyle,
        color = statusColor ?: MaterialTheme.colorScheme.onSurface,
      )
    }
    Icon(
      imageVector = Icons.Rounded.ContentCopy,
      contentDescription = "Copy",
      tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
      modifier = Modifier.size(18.dp),
    )
  }
}
```
Imports: `androidx.compose.foundation.clickable`, layout `Row/Column/Arrangement/fillMaxWidth/padding/size`, `androidx.compose.material3.MaterialTheme/Text/Icon`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.rounded.ContentCopy`, `androidx.compose.ui.platform.LocalClipboardManager`, `androidx.compose.ui.text.AnnotatedString`, `androidx.compose.runtime.rememberCoroutineScope`, `kotlinx.coroutines.launch`, `com.ryccoatika.sqatoolkit.common.ui.theme.MonoValueTextStyle`, `...ui.common.utils.LocalSnackbarHostState`, `...ui.common.utils.statusColorFor`, `Alignment`. Update the `@Preview` to wrap in a `CompositionLocalProvider(LocalSnackbarHostState provides SnackbarHostState())`.

- [ ] **Step 3: ItemGroupComposer → icon header + copy-group**

Header: leading `groupIcon(title)`, uppercase title (`labelMedium`, `primary`), trailing copy-group `IconButton` that copies all `label: value` lines (build via a small local walk of `item.items`, reuse the report formatter's row logic OR a simple `itemsToText` helper — define `internal fun groupItemsToText(items): String` in `GroupIcons.kt` mapping `RawTextItem`/`PermissionItem` to `label: value`, others skipped). Card uses `surfaceContainer`, rounded, thin dividers between rows. Keep `rawTitle ?: label` title resolution.

- [ ] **Step 4: GroupIcons.kt + TabIcons.kt**

`groupIcon(title: String): ImageVector` mapping the group titles (Processor, GPU, Memory, Storage, Display, Battery, Android, System, DRM, "SIM & Network", "Wi-Fi", Bluetooth, Other) to `Icons.Rounded.*` (Memory, Image, Storage, PhoneAndroid, BatteryFull, Android, Security, SignalCellularAlt, Wifi, Bluetooth, Devices, DeveloperBoard...). `tabIcon(id: String): ImageVector` mapping the 7 `DevInfoType` ids to icons. Use a `when` with a sensible default (`Icons.Rounded.Info`).

- [ ] **Step 5: Hero device card**

Rewrite `ItemDeviceCardComposer`: `Card` with `primaryContainer` container color, rounded; `Row` of Android art + a `Column` with device model + android name (`titleLarge`, `onPrimaryContainer`), then labeled sub-lines (codename/API/release date) as small label+value pairs. Keep `AndroidVersionImage`.

- [ ] **Step 6: Expandable restyle**

Update `ItemExpandableGroupComposer` header to match: title (`bodyLarge`), summary (`labelMedium`, muted), animated chevron; keep per-instance `remember { mutableStateOf(false) }`; expanded children render via `ItemComposer` (unchanged dispatch).

- [ ] **Step 7: Verify status composers route through the new row**

`ItemStatusComposer`/`ItemTextComposer`/`ItemDateComposer` already delegate to `ListItemText` (per the earlier refactor). Confirm; they inherit the new row automatically. `StatusItem`'s boolean already renders as "Supported"/"Not Supported" text → picked up by `statusColorFor`. No change needed unless they don't route through `ListItemText`.

- [ ] **Step 8: Main.kt full rewrite**

New signature + behavior:
```kotlin
internal typealias Main = @Composable (
  navigateUp: () -> Unit,
  dynamicColor: Boolean,
  onToggleDynamicColor: () -> Unit,
) -> Unit
```
The `@Inject @Composable fun Main` injects `viewModelFactory: () -> DevInfoViewModel` and `reportFormatter: DevInfoReportFormatter`, plus `@Assisted navigateUp/dynamicColor/onToggleDynamicColor`. Behavior:
- `val vm = viewModel(factory = viewModelFactory)`; `val state by vm.state.collectAsState()`.
- `Scaffold` with `snackbarHost = { SnackbarHost(snackbarHostState) }`, provide `LocalSnackbarHostState`.
- Top bar (`AppTopBar`) actions: a Search `IconButton` toggling `searchActive`; a Share `IconButton` (builds `reportFormatter.format(state.tabs)` and launches an `ACTION_SEND` chooser via `context`); an overflow `DropdownMenu` with a "Dynamic color" toggle (`onToggleDynamicColor`, checkmark from `dynamicColor`). When `searchActive`, show a `TextField` (bound to `state.query` via `vm.onQueryChange`) in/under the bar.
- Body: if `state.query.isBlank()` → the existing chips + `HorizontalPager`, but each page renders a shared `TabContent(items = filterItems(tab.items, ""))` (= tab.items). Chips get `leadingIcon = { Icon(tabIcon(type.id), null) }`. Provide `LocalPermissionRequester` around the pager wired to a launcher that `vm.refresh(currentType)` on grant (track current page's type).
- If `state.query.isNotBlank()` → a single `LazyColumn`/scroll `SearchResults(state.tabs, query)`: for each tab with non-empty `filterItems(tab.items, query)`, a tab header (`tabIcon` + title + match count) then those filtered items via `ItemComposer`; empty overall → "No matches" state.
- `TabContent(items: List<Item>)` = the old per-tab body: `ItemComposer(items, Modifier.padding(...).verticalScroll(...).fillMaxSize())`.

Provide the complete `Main.kt` in the implementation; the executor writes it following this structure and the existing `Main.kt` (chips/pager) as the base. Keep `material3` experimental opt-ins as needed (`@OptIn(ExperimentalMaterial3Api::class)`).

- [ ] **Step 9: FeatureScreens + fix call site**

`FeatureScreens` still holds `val main: Main` — unchanged (the typealias type changed, DI still resolves). Ensure `DevInfoActivity` calls `component.screens.main(navigateUp = { finish() }, dynamicColor = dynamicColor, onToggleDynamicColor = { ... })` (from Task 2).

- [ ] **Step 10: Compile & commit (Tasks 3–5 together)**

Run: `./gradlew :features:dev-info:compileDebugKotlin` then `./gradlew :app:assembleDebug` → BUILD SUCCESSFUL (under `-Werror`).

```bash
git add -A features/dev-info/src/main/java/com/ryccoatika/sqatoolkit/devinfo/
git commit -m "feat(dev-info): aggregator VM, mono copy-rows, status colors, icon cards/tabs, global search + share"
```

---

### Task 6: Full verification

**Files:** none.

- [ ] **Step 1:** `./gradlew :app:assembleDebug` → BUILD SUCCESSFUL.
- [ ] **Step 2: Manual smoke** (device/emulator): branded palette in light + dark; overflow → toggle Dynamic color flips palette and persists across restart; tap any row → "Copied <label>" snackbar + clipboard has the value; group copy button copies the group; Search filters across all tabs (results grouped by tab, empty → "No matches"); Share opens the chooser with a formatted full report; status colors show on Supported/Not-Supported and root/SELinux rows; all 7 tabs still load, expandables expand, Network/Connectivity Grant buttons still work (refresh repopulates).
- [ ] **Step 3:** Commit any smoke fixes.

## Self-Review Notes

- Spec coverage: branded theme+toggle (T1,T2), status colors (T1,T4,T5), aggregator single-source (T3), copy rows (T5), group/hero/expandable/tab icons (T5), global search (T4 filter + T5 UI), full-report share (T4 formatter + T5 action). All covered.
- Removed the 7 per-tab VMs + `InfoViewModel`/`InfoViewState` + `DevInfoScreens` class per the single-source decision; `DevInfoTypes` provider + `{X}Type` classes kept for DI/tab metadata.
- Tasks 3–5 (and the T2 call site) are one compile unit — the build gate is at the end of Task 5; the controller should treat T3+T4+T5 as a single reviewable deliverable with one commit, or commit T3/T4 as non-compiling WIP folded into T5. Recommended: implement T3+T4+T5 in one implementer dispatch, single commit.
- DataStore added to `common` (new dep). If the version `1.1.1` is unavailable, use the latest stable resolved by the catalog.
- `-Werror`: watch unused imports after stripping tab files (Task 3) and after theme cleanup (Task 1).
