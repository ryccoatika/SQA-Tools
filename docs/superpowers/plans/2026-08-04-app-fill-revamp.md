# App + Fill-* UI Revamp Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Apply the branded design language (from dev-info) to the app launcher, a new Settings screen, and full restyles of fill-memory + fill-storage — UI only, no logic changes.

**Architecture:** A tiny shared kit in `common` (`SectionCard`, `usageStatusColor`) that all modules use; then per-module restyles reusing the existing branded theme/`StatusColors`/`AppTopBar`. ViewModels/usecases/services are untouched; charts/meters are recolored with theme + status tokens.

**Tech Stack:** Kotlin, Jetpack Compose (Material3, material-icons-extended), kotlin-inject (tatarka), DataStore (`ThemePreferences`, already exists).

## Global Constraints

- minSdk 24, targetSdk 36, compileSdk 37. Guard any API > 24 with `Build.VERSION.SDK_INT`.
- **All modules compile with `-Werror` (`allWarningsAsErrors`)** — no unused imports/symbols; deprecated APIs need scoped `@Suppress("DEPRECATION")` (hoist to an annotated `val`, never inline in a string template).
- **No unit-test infra; do NOT add test deps.** Verification = `./gradlew :app:compileDebugKotlin` (or the specific module's `compileDebugKotlin`) then `./gradlew :app:assembleDebug`, plus manual smoke.
- 2-space indentation. kotlin-inject: `@Inject` on constructors; ViewModels via `com.ryccoatika.sqatoolkit.common.extensions.viewModel(factory)`.
- **UI-layer only:** do NOT modify ViewModels, ViewStates, usecases, `core/` models, or service behavior. Restyle composables only.
- Reuse existing: `common.ui.AppTopBar`, `common.ui.VerticalSpace`/`HorizontalSpace` (in `ComposeUtils.kt`), `common.ui.theme.SQAToolsTheme`/`StatusColors`/`LocalStatusColors`/`MonoValueTextStyle`, `common.data.ThemePreferences` (`useDynamicColor: Flow<Boolean>`, `suspend setDynamicColor(Boolean)`).
- `material-icons-extended` is available (common `api(libs.androidx.materialIcons)`); use `androidx.compose.material.icons.*` — verify each icon name resolves.
- Every restyled composable's `@Preview`/`@PreviewLightDark` must still compile.

---

### Task 1: Shared kit — SectionCard + usageStatusColor (common)

**Files:**
- Create: `common/src/main/java/com/ryccoatika/sqatoolkit/common/ui/widget/SectionCard.kt`
- Create: `common/src/main/java/com/ryccoatika/sqatoolkit/common/ui/theme/UsageColor.kt`

**Interfaces:**
- Produces:
  - `@Composable fun SectionCard(modifier: Modifier = Modifier, title: String? = null, icon: ImageVector? = null, content: @Composable ColumnScope.() -> Unit)`
  - `@Composable fun usageStatusColor(fraction: Float): Color` (reads `LocalStatusColors`).

- [ ] **Step 1: usageStatusColor**

```kotlin
package com.ryccoatika.sqatoolkit.common.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun usageStatusColor(fraction: Float): Color {
  val colors = LocalStatusColors.current
  val f = fraction.coerceIn(0f, 1f)
  return when {
    f < 0.6f -> colors.success
    f < 0.85f -> colors.warning
    else -> colors.error
  }
}
```

- [ ] **Step 2: SectionCard**

```kotlin
package com.ryccoatika.sqatoolkit.common.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SectionCard(
  modifier: Modifier = Modifier,
  title: String? = null,
  icon: ImageVector? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceContainer,
    ),
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(16.dp),
    ) {
      if (title != null) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          if (icon != null) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp),
            )
          }
          Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
          )
        }
      }
      content()
    }
  }
}
```

- [ ] **Step 3: Compile & commit**

Run: `./gradlew :app:compileDebugKotlin` → BUILD SUCCESSFUL (compile the app so all consumers see the new common symbols).

```bash
git add common/src/main/java/com/ryccoatika/sqatoolkit/common/ui/widget/SectionCard.kt common/src/main/java/com/ryccoatika/sqatoolkit/common/ui/theme/UsageColor.kt
git commit -m "feat(common): SectionCard + usageStatusColor shared UI kit"
```

---

### Task 2: App launcher — header + richer FeatureCard

**Files:**
- Modify: `app/src/main/java/com/ryccoatika/sqatoolkit/ui/tools/Tools.kt`
- Modify: `app/src/main/java/com/ryccoatika/sqatoolkit/ui/tools/widget/FeatureCard.kt`
- Maybe add string: `app/src/main/res/values/strings.xml` (`app_name`/subtitle if needed)

**Interfaces:**
- Consumes: `SQAFeature` (`icon: ImageVector`, `featureTitle`, `featureDescription`, `open(context)`), `AppTopBar`.

- [ ] **Step 1: Tools — add header + spacing**

Rewrite `Tools` to use a `Scaffold` with an `AppTopBar` (title "SQA Tools", no back button) and a `LazyColumn` with `contentPadding`/`verticalArrangement` spacing:

```kotlin
@Inject
@Composable
internal fun Tools(
  features: Set<SQAFeature>,
) {
  Scaffold(
    topBar = {
      AppTopBar(title = stringResource(R.string.app_name))
    },
  ) { paddingValues ->
    LazyColumn(
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.padding(paddingValues),
    ) {
      items(features.toList()) { feature ->
        FeatureCard(feature = feature)
      }
    }
  }
}
```
Imports: `androidx.compose.foundation.layout.PaddingValues`, `Arrangement`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.unit.dp`, `com.ryccoatika.sqatoolkit.common.ui.AppTopBar`, `com.ryccoatika.sqatoolkit.R`. Confirm `R.string.app_name` exists in the app module (it does for the launcher label); if not, add a string. Keep the `@PreviewLightDark` (wrap `AppTopBar` — it's `@OptIn(ExperimentalMaterial3Api::class)`; add the opt-in to the file if the compiler asks).

- [ ] **Step 2: FeatureCard — icon tile + chevron**

Rewrite the card body: icon inside a tinted rounded tile, title `titleMedium`, description `bodySmall`/`onSurfaceVariant`, trailing chevron:

```kotlin
@Composable
internal fun FeatureCard(feature: SQAFeature) {
  val context = LocalContext.current
  Card(
    onClick = { feature.open(context) },
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    modifier = Modifier.fillMaxWidth(),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(16.dp),
    ) {
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(44.dp)
          .clip(MaterialTheme.shapes.medium)
          .background(MaterialTheme.colorScheme.primaryContainer),
      ) {
        Icon(
          imageVector = feature.icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
      }
      16.HorizontalSpace()
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = stringResource(id = feature.featureTitle),
          style = MaterialTheme.typography.titleMedium,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = stringResource(id = feature.featureDescription),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 3,
          overflow = TextOverflow.Ellipsis,
        )
      }
      Icon(
        imageVector = Icons.Rounded.ChevronRight,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
```
Imports to add: `androidx.compose.foundation.background`, `androidx.compose.foundation.layout.Box`/`fillMaxWidth`/`size`, `androidx.compose.ui.draw.clip`, `androidx.compose.material3.CardDefaults`, `androidx.compose.material.icons.Icons`, `androidx.compose.material.icons.rounded.ChevronRight`. Remove the old `fontSize`/`sp` usages + unused imports (`sp`). Keep the `@PreviewLightDark`.

- [ ] **Step 3: Compile & commit**

Run: `./gradlew :app:compileDebugKotlin` → BUILD SUCCESSFUL.

```bash
git add app/src/main/java/com/ryccoatika/sqatoolkit/ui/tools/
git commit -m "feat(app): launcher header + richer feature cards"
```

---

### Task 3: App Settings — theme toggle + about

**Files:**
- Modify: `app/src/main/java/com/ryccoatika/sqatoolkit/ui/settings/Settings.kt`
- Create: `app/src/main/java/com/ryccoatika/sqatoolkit/ui/settings/SettingsViewModel.kt`
- Verify: `app/src/main/java/com/ryccoatika/sqatoolkit/AppNavigation.kt` renders `screens.settings()`

**Interfaces:**
- Consumes: `ThemePreferences`, `SectionCard`, `AppTopBar`, `viewModel(factory)`.
- Produces: a real `Settings` screen.

- [ ] **Step 1: SettingsViewModel**

```kotlin
package com.ryccoatika.sqatoolkit.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ryccoatika.sqatoolkit.common.data.ThemePreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
internal class SettingsViewModel(
  private val themePreferences: ThemePreferences,
) : ViewModel() {
  val useDynamicColor: StateFlow<Boolean> = themePreferences.useDynamicColor.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = false,
  )

  fun setDynamicColor(enabled: Boolean) {
    viewModelScope.launch { themePreferences.setDynamicColor(enabled) }
  }
}
```
NOTE: `ThemePreferences` currently has no `@Inject` constructor (it's `class ThemePreferences(context)`). For kotlin-inject to provide it, either (a) add `@Inject` to `ThemePreferences`'s constructor in `common` (it takes `Context`, which the graph provides), or (b) add a `@Provides fun provideThemePreferences(context: Context) = ThemePreferences(context)` in the app's DI component (mirroring how `DevInfoComponent` provides `DevInfoTextCreator`). Prefer (a) — add `@Inject` to `ThemePreferences` — it's clean and lets any module inject it. Verify `Context` is in the app graph (it is; `MainActivityComponent`/`ApplicationComponent` provide it — check and add a `@Provides` fallback if the graph doesn't resolve).

- [ ] **Step 2: Settings screen**

```kotlin
@Inject
@Composable
internal fun Settings(
  viewModelFactory: () -> SettingsViewModel,
) {
  Settings(viewModel = viewModel(factory = viewModelFactory))
}

@Composable
private fun Settings(viewModel: SettingsViewModel) {
  val dynamicColor by viewModel.useDynamicColor.collectAsState()
  Settings(
    dynamicColor = dynamicColor,
    onDynamicColorChange = viewModel::setDynamicColor,
  )
}

@Composable
private fun Settings(
  dynamicColor: Boolean,
  onDynamicColorChange: (Boolean) -> Unit,
) {
  val context = LocalContext.current
  val version = remember {
    runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
      .getOrNull().orEmpty()
  }
  val dynamicSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

  Scaffold(
    topBar = { AppTopBar(title = stringResource(R.string.settings_title)) },
  ) { paddingValues ->
    Column(
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier
        .padding(paddingValues)
        .padding(16.dp),
    ) {
      SectionCard(title = stringResource(R.string.settings_appearance), icon = Icons.Rounded.Palette) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.settings_dynamic_color), style = MaterialTheme.typography.bodyLarge)
            Text(
              text = stringResource(
                if (dynamicSupported) R.string.settings_dynamic_color_desc
                else R.string.settings_dynamic_color_unsupported,
              ),
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
          Switch(
            checked = dynamicColor && dynamicSupported,
            enabled = dynamicSupported,
            onCheckedChange = onDynamicColorChange,
          )
        }
      }
      SectionCard(title = stringResource(R.string.settings_about), icon = Icons.Rounded.Info) {
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.bodyLarge)
        Text(
          text = stringResource(R.string.settings_version, version),
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}
```
Imports: `androidx.compose.foundation.layout.*` (Column/Row/Arrangement/padding), `androidx.compose.material3.*` (Scaffold/Switch/Text/MaterialTheme), `androidx.compose.runtime.*` (Composable/collectAsState/getValue/remember), `androidx.compose.ui.Alignment/Modifier`, `androidx.compose.ui.platform.LocalContext`, `androidx.compose.ui.res.stringResource`, `androidx.compose.ui.unit.dp`, `android.os.Build`, `androidx.compose.material.icons.Icons` + `rounded.Palette`/`rounded.Info`, `com.ryccoatika.sqatoolkit.common.extensions.viewModel`, `com.ryccoatika.sqatoolkit.common.ui.AppTopBar`, `com.ryccoatika.sqatoolkit.common.ui.widget.SectionCard`, `com.ryccoatika.sqatoolkit.R`. Keep a `@Preview`.

- [ ] **Step 3: Strings**

Add to `app/src/main/res/values/strings.xml`:
```xml
<string name="settings_title">Settings</string>
<string name="settings_appearance">Appearance</string>
<string name="settings_dynamic_color">Dynamic color (Material You)</string>
<string name="settings_dynamic_color_desc">Use colors from your wallpaper</string>
<string name="settings_dynamic_color_unsupported">Requires Android 12 or newer</string>
<string name="settings_about">About</string>
<string name="settings_version">Version %1$s</string>
```

- [ ] **Step 4: Verify nav renders Settings**

Read `AppNavigation.kt`; confirm the Settings destination calls `appScreens.settings()`. If `Settings` newly takes an injected `viewModelFactory` (assisted-free), the `screens.settings()` call site is unchanged (DI supplies the factory). Confirm it compiles.

- [ ] **Step 5: Compile & commit**

Run: `./gradlew :app:assembleDebug` → BUILD SUCCESSFUL.

```bash
git add app/src/main/java/com/ryccoatika/sqatoolkit/ui/settings/ app/src/main/res/values/strings.xml common/src/main/java/com/ryccoatika/sqatoolkit/common/data/ThemePreferences.kt
git commit -m "feat(app): build Settings screen with dynamic-color toggle + about"
```

---

### Task 4: fill-memory full restyle

**Files (all `features/fill-memory/.../ui/`):**
- Modify: `home/Home.kt`, `home/widget/FillMemoryProgress.kt`, `home/widget/FillMemoryOptions.kt`, `home/widget/MemoryGraph.kt`
- Modify (theme colors only): `home/floater/service/FloaterButton.kt`, `home/floater/service/FloaterClosePlaceholder.kt`

**Interfaces:**
- Consumes: `SectionCard`, `usageStatusColor`, existing `LocalTextCreator`, `HomeViewState`, `MemoryUsage` (`usedMemoryPercent: Float` 0..1, `usedMemory`/`totalMemory`), `FillMemory`.
- Do NOT change any ViewModel/ViewState/textCreator.

READ each file before editing; apply these specific changes:

- [ ] **Step 1: MemoryGraph — status-graded fill color**

In `MemoryGraph.kt`, replace `val graphColor = MaterialTheme.colorScheme.primary` with a status-graded color based on the latest usage fraction:
```kotlin
val graphColor = usageStatusColor(lastMemoryUsage.usedMemoryPercent)
```
(import `com.ryccoatika.sqatoolkit.common.ui.theme.usageStatusColor`.) Change the border color from `onSurface` to `MaterialTheme.colorScheme.outlineVariant`. Keep the Canvas/path logic. The stat texts (total/used/free/dummy) stay; optionally wrap the graph in `SectionCard(title = "Memory")` at the Home level (Step 4), not here.

- [ ] **Step 2: FillMemoryProgress — status-graded ring**

In `FillMemoryProgress.kt`, color the `CircularProgressIndicator` by fill fraction:
```kotlin
CircularProgressIndicator(
  progress = { progress.progress },
  color = usageStatusColor(progress.progress),
  trackColor = MaterialTheme.colorScheme.surfaceVariant,
  modifier = Modifier.size(96.dp),
)
```
(import `usageStatusColor`.) Keep the dialog/surface/button. Optionally tint the percent `Text` with the same color.

- [ ] **Step 3: FillMemoryOptions — branded grid in a card**

In `FillMemoryOptions.kt`, keep the `LazyVerticalGrid` of option buttons but add vertical spacing (`verticalArrangement = Arrangement.spacedBy(7.dp)`) and use `FilledTonalButton` (softer, on-brand) instead of `Button` for the value chips. Keep `textCreator.fillMemoryButtonText`.

- [ ] **Step 4: Home — SectionCards + spacing**

In `home/Home.kt`, restructure the body `Column` (inside the existing `Scaffold`) to use padding + spacing and wrap the graph and options in `SectionCard`s:
```kotlin
Column(
  verticalArrangement = Arrangement.spacedBy(12.dp),
  modifier = Modifier
    .padding(paddingValues)
    .verticalScroll(rememberScrollState())
    .padding(16.dp),
) {
  SectionCard(title = stringResource(R.string.fm_title), icon = Icons.Rounded.Memory) {
    MemoryGraph(memoryUsages = state.history, modifier = Modifier.fillMaxWidth())
  }
  SectionCard {
    FillMemoryOptions(onFill = fillMemory, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth())
    Button(
      onClick = clearMemory,
      enabled = !state.isLoading,
      shape = MaterialTheme.shapes.medium,
      modifier = Modifier.fillMaxWidth(),
    ) { Text(stringResource(R.string.fm_button_clear_memory)) }
  }
}
```
Add imports: `SectionCard`, `Arrangement`, `verticalScroll`/`rememberScrollState`, `Icons`/`rounded.Memory`. Keep the `HomeTopBar` (uses `AppTopBar`) and the `FillMemoryProgress` dialog call. Keep the floater `IconButton` action in the top bar.

- [ ] **Step 5: Floater colors**

In `FloaterButton.kt` / `FloaterClosePlaceholder.kt`, replace any hardcoded colors with theme/brand colors (e.g. `MaterialTheme.colorScheme.primary`/`onPrimary` for the button, `error` for the close placeholder) IF they render inside a Compose theme scope. If the floater is drawn outside a `SQAToolsTheme` (a raw window/overlay service), leave color literals but switch them to the brand hex values (indigo `0xFF3B5BDB` primary, red for close) for consistency; do NOT change service logic. Read the files first and adapt minimally; if unclear, keep as-is and note it — this step is cosmetic and non-blocking.

- [ ] **Step 6: Compile & commit**

Run: `./gradlew :features:fill-memory:compileDebugKotlin` then `./gradlew :app:assembleDebug` → BUILD SUCCESSFUL.

```bash
git add features/fill-memory/
git commit -m "feat(fill-memory): branded restyle — status-graded graph/progress, section cards"
```

---

### Task 5: fill-storage full restyle

**Files (all `features/fill-storage/.../ui/`):**
- Modify: `home/Home.kt`, `home/widget/StorageCard.kt`, `manage/Manage.kt`, `manage/widget/FillStorageField.kt`, `manage/widget/FillStorageOptions.kt`, `manage/widget/FillStorageProgress.kt`, `manage/widget/StorageChart.kt`, `dummyfiles/DummyFiles.kt`

**Interfaces:**
- Consumes: `SectionCard`, `usageStatusColor`, `LocalTextCreator`, `Storage` (`capacity.usedSpace`/`totalSpace`), existing view states.
- Do NOT change ViewModels/usecases.

READ each file before editing; apply these changes:

- [ ] **Step 1: StorageCard — themed usage bar (fix hardcoded colors)**

In `StorageCard.kt`'s `StorageBarChart`, replace the hardcoded colors:
- Track `Color.Gray` → `MaterialTheme.colorScheme.surfaceVariant`.
- Used fill `Color.Green` → `usageStatusColor(fraction)` where `fraction = usedSpace / totalSpace` (compute as Float, coerce 0..1).
- Free-space label `Color.White` → `MaterialTheme.colorScheme.onSurfaceVariant`.
Wrap the card as `SectionCard`-style or keep `Card` with `surfaceContainer` container color. Keep the `Button` (use `FilledTonalButton` for "Manage"). Keep the text-measure logic. (import `usageStatusColor`.)

- [ ] **Step 2: StorageChart — themed segments**

In `manage/widget/StorageChart.kt` (and `ChartBar` referenced by `Manage.kt`), recolor segments with theme tokens: used = `usageStatusColor(fraction)`, free = `surfaceVariant`, dummy (if any) = `MaterialTheme.colorScheme.tertiary`. Replace any `Color.X` literals. Keep the drawing logic. Read the file to see its exact segments and map them sensibly.

- [ ] **Step 3: FillStorageField — branded input**

In `FillStorageField.kt`, ensure it uses Material3 `OutlinedTextField`/`TextField` with default (themed) colors; remove any hardcoded color literals. Keep its state hoisting.

- [ ] **Step 4: FillStorageProgress + FillStorageOptions — status-graded + branded**

`FillStorageProgress.kt`: color its progress by fill fraction via `usageStatusColor`, `trackColor = surfaceVariant` (mirror fill-memory's progress). `FillStorageOptions.kt`: use `FilledTonalButton`/branded controls; group in a `SectionCard` at the `Manage` level.

- [ ] **Step 5: Home + Manage + DummyFiles — scaffold/spacing/cards**

`home/Home.kt`: already uses `AppTopBar`; add `verticalArrangement`/consistent padding; `StorageCard`s spaced. `manage/Manage.kt`: wrap the field/options/progress/chart in `SectionCard`s with spacing (mirror fill-memory Home Step 4). `dummyfiles/DummyFiles.kt`: restyle the file list rows as branded `ListItem`s or `SectionCard` list (read the file; match the row style to the rest — label + size, muted secondary text). Keep all VM wiring and `@Preview`s.

- [ ] **Step 6: Compile & commit**

Run: `./gradlew :features:fill-storage:compileDebugKotlin` then `./gradlew :app:assembleDebug` → BUILD SUCCESSFUL.

```bash
git add features/fill-storage/
git commit -m "feat(fill-storage): branded restyle — themed charts/meters, section cards"
```

---

### Task 6: Full verification

**Files:** none.

- [ ] **Step 1:** `./gradlew :app:assembleDebug` → BUILD SUCCESSFUL under `-Werror`.
- [ ] **Step 2: Manual smoke** (device/emulator): launcher shows header + richer cards + chevrons; tapping a card opens the feature; Settings screen renders, the Dynamic-color switch flips the palette app-wide and persists across restart (and is disabled on API<31); fill-memory home shows section cards + a status-graded memory graph + progress ring; fill-storage home/manage show themed usage bars/charts (green→amber→red) + branded fields; dummy-files list restyled; nothing functional regressed (fill/clear still work). Light + dark both look right.
- [ ] **Step 3:** Commit any smoke fixes.

## Self-Review Notes

- Spec coverage: shared kit (T1), launcher header+cards (T2), Settings+toggle+about (T3), fill-memory full restyle incl. status-graded meters (T4), fill-storage full restyle (T5), verification (T6). All covered.
- UI-only: no task edits a ViewModel/usecase/service logic; T4/T5 restyle composables + recolor charts only.
- `ThemePreferences` needs an `@Inject` constructor (or an app `@Provides`) to be injectable by `SettingsViewModel` — called out in T3 Step 1; this is the one `common` non-UI change (adding `@Inject`), acceptable and low-risk.
- `-Werror`: watch unused imports after each restyle (remove old `sp`/`Color`/`fontSize` imports).
- Restyle tasks (T4/T5) instruct the implementer to READ each widget then apply named changes — the color/structure changes are concrete; exact final code per widget depends on each file's current body, which the implementer has in front of them.
