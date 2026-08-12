# Playful Animated UI/UX Pass Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a playful, vibrant, animated layer over the existing branded UI — per-feature accent gradients, animated meters + count-up, gradient hero headers, a splash screen, and iconographic empty states — across app + fillstorage + fillmemory.

**Architecture:** A reusable `common` kit (accents, shapes, `GradientHero`, animation helpers, `EmptyState`, `pressable`) that the base and both feature modules consume. Per-module tasks apply it. UI-only; no logic/data changes; dynamic-color toggle stays working (accents are additive).

**Tech Stack:** Kotlin, Jetpack Compose (Material3, animation APIs `animateFloatAsState`/`animateColorAsState`/`AnimatedVisibility`), `androidx.core:core-splashscreen`, kotlin-inject.

## Global Constraints

- minSdk 24, targetSdk 36, compileSdk 37. Guard any API > 24 with `Build.VERSION.SDK_INT`.
- **All modules compile with `-Werror` (`allWarningsAsErrors`)** — no unused imports/symbols; scoped `@Suppress("DEPRECATION")` where needed (hoist to an annotated `val`).
- **No unit-test infra; do NOT add test deps.** Build gate = `./gradlew :app:assembleDebug` AND `./gradlew :app:bundleDebug` BUILD SUCCESSFUL under `-Werror`.
- 2-space indent. kotlin-inject `@Inject`; ViewModels via `com.ryccoatika.sqatoolkit.common.extensions.viewModel(factory)`.
- **UI-LAYER ONLY:** do NOT modify ViewModels/ViewStates/usecases/services/`core` models. Restyle/animate composables only.
- Feature modules (`fillstorage`, `fillmemory`) are `com.android.dynamic-feature` depending on `:app` + `common`; they can use new `common` composables. Reuse existing `common`: `SQAToolsTheme`, `StatusColors`/`usageStatusColor`, `SectionCard`, `AppTopBar`, `MonoValueTextStyle`, `Typography`.
- Animations short (150–400ms), non-blocking; UI fully usable without them.
- `material-icons-extended` available (verify each icon name resolves).

---

### Task 1: common foundation — accents, shapes, GradientHero, animation helpers, EmptyState, pressable

**Files:**
- Create: `common/.../ui/theme/FeatureAccent.kt`, `common/.../ui/theme/Shape.kt`
- Modify: `common/.../ui/theme/Theme.kt` (wire `shapes`)
- Create: `common/.../ui/widget/GradientHero.kt`, `common/.../ui/widget/EmptyState.kt`, `common/.../ui/AnimatedValue.kt` (AnimatedCountText + meter helpers), `common/.../ui/Pressable.kt`

**Interfaces:**
- Produces:
  - `enum class FeatureAccent(val color: Color, val gradientEnd: Color, val onColor: Color)` (or a data holder) with `Storage`, `Memory`, `Neutral` variants (light/dark-aware via a `@Composable fun FeatureAccent.brush()`).
  - `AppShapes: Shapes` wired into `SQAToolsTheme`.
  - `@Composable fun GradientHero(title, subtitle, accent, modifier, trailing/content slots)`.
  - `@Composable fun AnimatedCountText(value: Float, formatter: (Float) -> String, style, color, modifier)`.
  - `@Composable fun animatedFraction(target: Float): State<Float>` + reuse `usageStatusColor` with `animateColorAsState`.
  - `@Composable fun EmptyState(icon, title, message, modifier, action?)`.
  - `fun Modifier.pressable(onClick): Modifier` (scale + ripple press feedback).

- [ ] **Step 1: FeatureAccent**

```kotlin
package com.ryccoatika.sqatoolkit.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class FeatureAccent(
  val light: Color,
  val lightEnd: Color,
  val dark: Color,
  val darkEnd: Color,
) {
  Storage(Color(0xFF00B8D4), Color(0xFF2979FF), Color(0xFF4DD0E1), Color(0xFF5C9CFF)),
  Memory(Color(0xFFF50057), Color(0xFFFF6D3F), Color(0xFFFF5C8A), Color(0xFFFF8A65)),
  Neutral(Color(0xFF5B6BFF), Color(0xFF8B5CF6), Color(0xFF9AA6FF), Color(0xFFB794F6));

  @Composable
  fun base(): Color = if (isSystemInDarkTheme()) dark else light

  @Composable
  fun gradient(): Brush {
    val dark = isSystemInDarkTheme()
    return Brush.linearGradient(
      listOf(if (dark) this.dark else light, if (dark) darkEnd else lightEnd),
    )
  }
}
```
(Hex values are vibrant + accessible; adjust if any reads poorly on the branded surfaces.)

- [ ] **Step 2: Shapes**

```kotlin
package com.ryccoatika.sqatoolkit.common.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
  extraSmall = RoundedCornerShape(8.dp),
  small = RoundedCornerShape(12.dp),
  medium = RoundedCornerShape(18.dp),
  large = RoundedCornerShape(24.dp),
  extraLarge = RoundedCornerShape(32.dp),
)
```
In `Theme.kt`, pass `shapes = AppShapes` to the `MaterialTheme(...)` call.

- [ ] **Step 3: GradientHero**

```kotlin
package com.ryccoatika.sqatoolkit.common.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ryccoatika.sqatoolkit.common.ui.theme.FeatureAccent

@Composable
fun GradientHero(
  title: String,
  accent: FeatureAccent,
  modifier: Modifier = Modifier,
  subtitle: String? = null,
  trailing: @Composable (() -> Unit)? = null,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
      .background(accent.gradient())
      .padding(horizontal = 20.dp, vertical = 24.dp),
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(4.dp),
      modifier = Modifier.weight(1f),
    ) {
      Text(title, style = MaterialTheme.typography.headlineSmall, color = Color.White)
      if (subtitle != null) {
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
      }
    }
    if (trailing != null) trailing()
  }
}
```
NOTE: verify `MaterialTheme.typography.headlineSmall` exists (Typography defines a scale; if `headlineSmall` isn't defined, use `titleLarge`). White-on-gradient is intentional for the vibrant hero.

- [ ] **Step 4: AnimatedCountText + meter helpers**

```kotlin
package com.ryccoatika.sqatoolkit.common.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun animatedFraction(target: Float): State<Float> =
  animateFloatAsState(targetValue = target.coerceIn(0f, 1f), label = "fraction")

@Composable
fun AnimatedCountText(
  value: Float,
  formatter: (Float) -> String,
  modifier: Modifier = Modifier,
  style: TextStyle = LocalTextStyle.current,
  color: Color = Color.Unspecified,
) {
  val animated by animateFloatAsState(targetValue = value, label = "count")
  Text(text = formatter(animated), modifier = modifier, style = style, color = color)
}
```

- [ ] **Step 5: EmptyState**

```kotlin
package com.ryccoatika.sqatoolkit.common.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(
  icon: ImageVector,
  title: String,
  message: String,
  modifier: Modifier = Modifier,
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = modifier.padding(32.dp),
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer)
        .padding(20.dp)
        .size(48.dp),
    )
    Text(title, style = MaterialTheme.typography.titleMedium)
    Text(
      message,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
    )
  }
}
```

- [ ] **Step 6: pressable modifier**

```kotlin
package com.ryccoatika.sqatoolkit.common.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale

fun Modifier.pressable(onClick: () -> Unit): Modifier = composed {
  val interaction = remember { MutableInteractionSource() }
  val pressed by interaction.collectIsPressedAsState()
  val scale by animateFloatAsState(if (pressed) 0.97f else 1f, label = "press")
  this
    .scale(scale)
    .clickable(interactionSource = interaction, indication = null, onClick = onClick)
}
```

- [ ] **Step 7: Compile & commit**

Run: `./gradlew :app:compileDebugKotlin` → BUILD SUCCESSFUL (compile the app so consumers see the new common symbols).

```bash
git add common/src/main/java/com/ryccoatika/sqatoolkit/common/ui/
git commit -m "feat(common): playful kit — accents, shapes, gradient hero, animation helpers, empty state"
```

---

### Task 2: app — launcher hero + accent animated cards + splash + settings

**Files:**
- Modify: `app/.../feature/FeatureDescriptor.kt` (add `accent`), `app/.../feature/FeatureRegistry.kt` (set accents)
- Modify: `app/.../ui/tools/Tools.kt`, `app/.../ui/tools/widget/FeatureCard.kt`, `app/.../ui/common/utils/preview/PreviewDummyData.kt`
- Modify: `app/.../ui/settings/Settings.kt`
- Modify: `app/.../ui/MainActivity.kt`, `app/src/main/res/values/themes.xml`, `app/src/main/AndroidManifest.xml`, `gradle/libs.versions.toml`, `app/build.gradle.kts`

**Interfaces:**
- Consumes: `FeatureAccent`, `GradientHero`, `pressable`, `animatedFraction`, `AnimatedCountText`, `EmptyState`.
- Produces: `FeatureDescriptor.accent: FeatureAccent`.

- [ ] **Step 1: FeatureDescriptor + Registry accents**

Add `val accent: FeatureAccent` to `FeatureDescriptor`. In `FeatureRegistry`, set `accent = FeatureAccent.Storage` for fill-storage, `FeatureAccent.Memory` for fill-memory. Import `com.ryccoatika.sqatoolkit.common.ui.theme.FeatureAccent`.

- [ ] **Step 2: Splash screen**

- Catalog: `[versions]` `coreSplashscreen = "1.0.1"`; `[libraries]` `androidx-core-splashscreen = { group = "androidx.core", name = "core-splashscreen", version.ref = "coreSplashscreen" }`. Add `implementation(libs.androidx.core.splashscreen)` to `app/build.gradle.kts`.
- `themes.xml`: add a starting theme:
```xml
<style name="Theme.SQATools.Starting" parent="Theme.SplashScreen">
  <item name="windowSplashScreenBackground">@color/splash_background</item>
  <item name="windowSplashScreenAnimatedIcon">@mipmap/ic_launcher_foreground</item>
  <item name="postSplashScreenTheme">@style/Theme.SQATools</item>
</style>
```
Add `app/src/main/res/values/colors.xml` `<color name="splash_background">#3B5BDB</color>` (brand primary). Verify `@mipmap/ic_launcher_foreground` exists (else use `@mipmap/ic_launcher`).
- Manifest: set `MainActivity`'s `android:theme="@style/Theme.SQATools.Starting"`.
- `MainActivity.onCreate`: `installSplashScreen()` before `setContent` (follow the androidx `core-splashscreen` docs — typically first line after `super.onCreate`). Import `androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen`.

- [ ] **Step 3: Tools launcher hero + entrance**

Replace the `AppTopBar` in `Tools` with a `GradientHero(title = app name, subtitle = "device & QA utilities", accent = FeatureAccent.Neutral)` as the first item / header of the screen (put the hero above the `LazyColumn`, or as its first `item {}`). Give the feature cards a short staggered entrance (`AnimatedVisibility` with a per-index delay, or `Modifier.animateItem()` on the LazyColumn items). Keep the install/confirmation logic unchanged.

- [ ] **Step 4: FeatureCard — accent + animation + press**

Rework `FeatureCard` to use `descriptor.accent`: tint the icon tile with the accent (`accent.base()` / a soft container), apply `Modifier.pressable` for press feedback, and animate the Download progress bar with `animatedFraction(state.progress)` (smooth). Keep Download/progress/Installing/Open+Remove/Retry states + the branded layout. Update `PreviewDummyData` descriptors to include an `accent`.

- [ ] **Step 5: Settings hero**

Replace the Settings `AppTopBar` with a `GradientHero(title = "Settings", accent = FeatureAccent.Neutral)`; keep the Appearance/About `SectionCard`s. Light polish only.

- [ ] **Step 6: Compile & commit**

Run: `./gradlew :app:assembleDebug` → BUILD SUCCESSFUL.

```bash
git add -A app/ gradle/libs.versions.toml
git commit -m "feat(app): splash, launcher gradient hero, accent animated feature cards, settings hero"
```

---

### Task 3: fillstorage — accent hero + animated meters/charts + empty states

**Files (all `features/fillstorage/.../ui/`):**
- Modify: `home/Home.kt`, `home/widget/StorageCard.kt`, `manage/Manage.kt`, `manage/widget/StorageChart.kt`, `manage/widget/FillStorageProgress.kt`, `dummyfiles/DummyFiles.kt`

**Interfaces:**
- Consumes: `FeatureAccent.Storage`, `GradientHero`, `animatedFraction`, `AnimatedCountText`, `EmptyState`, `usageStatusColor`, `SectionCard`.

READ each file first, then apply:
- [ ] **Step 1: Home hero** — add a `GradientHero(title = fs_title, accent = FeatureAccent.Storage)` at the top; show the headline used/total via `AnimatedCountText` in the hero trailing slot if a total is available at Home level (else keep it in the cards).
- [ ] **Step 2: StorageCard animated bar** — drive the usage bar width with `animatedFraction(fraction)`; color via `usageStatusColor(fraction)` with `animateColorAsState`; count-up the free/used text via `AnimatedCountText` where a numeric value is shown. Use the Storage accent for non-status chrome.
- [ ] **Step 3: Manage** — `GradientHero(Storage)`; `StorageChart`/`FillStorageProgress` animate to value (`animatedFraction` + `animateColorAsState`); keep `SectionCard` grouping + press feedback on buttons (`pressable` or keep Material buttons).
- [ ] **Step 4: DummyFiles empty state** — when the file list is empty, render `EmptyState(icon = Icons.Rounded.FolderOff or similar, title, message)`; keep the `LazyColumn` for the populated list (do NOT wrap a lazy list in verticalScroll).
- [ ] **Step 5: Compile & commit**

Run: `./gradlew :features:fillstorage:compileDebugKotlin` then `./gradlew :app:assembleDebug` → BUILD SUCCESSFUL.

```bash
git add features/fillstorage/
git commit -m "feat(fillstorage): storage accent hero, animated meters/charts, empty states"
```

---

### Task 4: fillmemory — accent hero + animated ring/graph + count-up + empty states

**Files (all `features/fillmemory/.../ui/`):**
- Modify: `home/Home.kt`, `home/widget/FillMemoryProgress.kt`, `home/widget/MemoryGraph.kt`, `home/widget/FillMemoryOptions.kt`

**Interfaces:**
- Consumes: `FeatureAccent.Memory`, `GradientHero`, `animatedFraction`, `AnimatedCountText`, `EmptyState`, `usageStatusColor`, `SectionCard`.

READ each file first, then apply:
- [ ] **Step 1: Home hero** — `GradientHero(title = fm_title, accent = FeatureAccent.Memory)` at the top; keep the `SectionCard`s below.
- [ ] **Step 2: FillMemoryProgress ring** — animate the `CircularProgressIndicator` progress with `animatedFraction(progress.progress)`; smooth the status color with `animateColorAsState(usageStatusColor(progress.progress))`; count-up the percent text via `AnimatedCountText`.
- [ ] **Step 3: MemoryGraph** — animate the graph fill (drive the drawn value/last-usage fraction with `animatedFraction`), keeping the Canvas logic; color via `usageStatusColor` (already) + `animateColorAsState`. Count-up the used/total/free stat texts via `AnimatedCountText` where numeric.
- [ ] **Step 4: FillMemoryOptions** — `pressable`/press feedback on the option chips; Memory-accent tint on the chips where sensible (keep readable). If the history is empty, show an `EmptyState` in the graph card ("No samples yet — fill some memory to see the graph").
- [ ] **Step 5: Compile & commit**

Run: `./gradlew :features:fillmemory:compileDebugKotlin` then `./gradlew :app:assembleDebug` → BUILD SUCCESSFUL.

```bash
git add features/fillmemory/
git commit -m "feat(fillmemory): memory accent hero, animated ring/graph + count-up, empty state"
```

---

### Task 5: Full verification

**Files:** none.

- [ ] **Step 1:** `./gradlew :app:assembleDebug` then `./gradlew :app:bundleDebug` → BOTH BUILD SUCCESSFUL under `-Werror`.
- [ ] **Step 2: Manual smoke** (device/emulator): splash on launch; launcher gradient hero + accent-colored cards with press feedback + staggered entrance + smooth download progress; Settings hero; fillstorage Storage-accent hero + animated bars/charts + count-up + empty dummy-files state; fillmemory Memory-accent hero + animated ring/graph + count-up + empty graph state; light + dark; dynamic-color toggle still flips the base scheme; nothing functional regressed (fill/clear/download/open still work).
- [ ] **Step 3:** Commit any smoke fixes.

## Self-Review Notes

- Spec coverage: accents (T1/T2), shapes (T1), GradientHero (T1 + T2/T3/T4), AnimatedCountText/meter (T1 + T3/T4), EmptyState (T1 + T3/T4), pressable (T1 + T2/T3/T4), splash (T2), launcher hero + animated cards (T2), settings hero (T2), fillstorage (T3), fillmemory (T4), verify (T5). Covered.
- T1 is foundational — must land + compile first; T2/T3/T4 each depend on T1 and compile independently after it.
- UI-only: no task edits VM/usecase/service logic; charts/meters are animated wrappers over existing draw logic.
- `-Werror`: watch unused imports after each restyle; verify each material icon + typography token used (`headlineSmall` may not be defined — fall back to `titleLarge`).
- Dynamic-color ON: heroes intentionally keep the fixed accent gradients; the rest follows the dynamic scheme — acceptable per spec.
- Splash: verify `@mipmap/ic_launcher_foreground` exists (else `@mipmap/ic_launcher`); `installSplashScreen()` per androidx docs; test no crash on API 24.
- Nested-scroll rule still applies: do NOT wrap a `LazyColumn`/grid in a `verticalScroll` parent (fillstorage/fillmemory already resolved this — don't reintroduce).
