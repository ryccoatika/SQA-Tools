# On-Demand Dynamic Feature Modules Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Convert `fill-storage` + `fill-memory` from static `com.android.library` modules into on-demand dynamic feature modules, launched from a base-owned registry with a Download/Open/Remove UI.

**Architecture:** Dependency direction reverses (features depend on `:app`); `app` is the base (`dynamicFeatures`, `SplitCompatApplication`, Play `feature-delivery`). Feature discovery moves from kotlin-inject DI to a static `FeatureRegistry` in the base; a `FeatureInstallManager` wraps `SplitInstallManager` for install/uninstall/launch/state. Modules are renamed to drop hyphens (`fillstorage`, `fillmemory`) so their split names are valid.

**Tech Stack:** Kotlin, Jetpack Compose (Material3), kotlin-inject (tatarka), AGP 9.3.1 `com.android.dynamic-feature`, Play `com.google.android.play:feature-delivery(-ktx)`, `SplitInstallManager`/`SplitCompat`.

## Global Constraints

- minSdk 24, targetSdk 36, compileSdk 37. Guard any API > 24 with `Build.VERSION.SDK_INT`.
- **All modules compile with `-Werror` (`allWarningsAsErrors`)** — no unused imports/symbols; scoped `@Suppress("DEPRECATION")` where a Play API is deprecated (hoist to an annotated `val`, never inline in a string template).
- **No unit-test infra; do NOT add test deps.** Build gate = `./gradlew :app:bundleDebug` AND `./gradlew :app:assembleDebug` BUILD SUCCESSFUL. Real on-demand install is verified manually via bundletool (documented, not run here).
- 2-space indentation. kotlin-inject `@Inject`; ViewModels via `com.ryccoatika.sqatoolkit.common.extensions.viewModel(factory)`.
- **Feature INTERNAL behavior is unchanged** — screens/ViewModels/usecases/services stay. Only module packaging, discovery, launch, and the launcher UI change.
- The Kotlin package names are ALREADY hyphen-free (`com.ryccoatika.sqatoolkit.fillstorage` / `fillmemory`) — only the GRADLE MODULE directories/names (`fill-storage`→`fillstorage`, `fill-memory`→`fillmemory`) change. Do NOT rename Kotlin packages.

---

### Task 1: Module plumbing — rename, catalog, gradle, manifests, SplitCompat, remove dead wiring

**Files:**
- Rename: `features/fill-storage/` → `features/fillstorage/`, `features/fill-memory/` → `features/fillmemory/` (via `git mv`)
- Modify: `settings.gradle.kts`, `gradle/libs.versions.toml`, `app/build.gradle.kts`, the two feature `build.gradle.kts`, the two feature `AndroidManifest.xml`, `app/src/main/AndroidManifest.xml` (namespaces etc. if needed), `app/.../SQAToolsApplication.kt`, `app/.../inject/ApplicationComponent.kt`
- Delete: `features/fillstorage/.../FillStorageFeature.kt`, `features/fillstorage/.../inject/FillStorageFeature.kt`, `features/fillmemory/.../FillMemoryFeature.kt`, `features/fillmemory/.../inject/FillMemoryFeature.kt`, `common/.../SQAFeature.kt`
- Add: base strings `app/src/main/res/values/strings.xml` (feature titles/descs)

**Interfaces:**
- Produces: renamed modules `:features:fillstorage` / `:features:fillmemory` as `com.android.dynamic-feature`; base `dynamicFeatures` set; `SQAToolsApplication : SplitCompatApplication`; base strings `feature_title_fill_storage`, `feature_desc_fill_storage`, `feature_title_fill_memory`, `feature_desc_fill_memory`.

- [ ] **Step 1: Rename module directories**

```bash
git mv features/fill-storage features/fillstorage
git mv features/fill-memory features/fillmemory
```

- [ ] **Step 2: settings.gradle.kts**

Replace the two feature includes:
```kotlin
  ":features:fillstorage",
  ":features:fillmemory",
```

- [ ] **Step 3: Version catalog**

In `gradle/libs.versions.toml`:
- `[versions]` add `playFeatureDelivery = "2.1.0"`.
- `[plugins]` add `android-dynamic-feature = { id = "com.android.dynamic-feature", version.ref = "agp" }`.
- `[libraries]` add:
```toml
play-feature-delivery = { group = "com.google.android.play", name = "feature-delivery", version.ref = "playFeatureDelivery" }
play-feature-delivery-ktx = { group = "com.google.android.play", name = "feature-delivery-ktx", version.ref = "playFeatureDelivery" }
```

- [ ] **Step 4: app/build.gradle.kts (base)**

- Remove `implementation(projects.features.fillStorage)` and `implementation(projects.features.fillMemory)`.
- In `android { … }` add:
```kotlin
  dynamicFeatures += setOf(":features:fillstorage", ":features:fillmemory")
```
- In `dependencies { … }` add:
```kotlin
  implementation(libs.play.feature.delivery)
  implementation(libs.play.feature.delivery.ktx)
```

- [ ] **Step 5: Feature build.gradle.kts (both)**

For `features/fillstorage/build.gradle.kts` and `features/fillmemory/build.gradle.kts`:
- Change the plugin `alias(libs.plugins.android.library)` → `alias(libs.plugins.android.dynamicFeature)`.
- Add `implementation(project(":app"))` to dependencies (the reverse dep; keep the existing `implementation(projects.common)` and others).
- A `com.android.dynamic-feature` module does NOT declare `namespace`/`applicationId`/`versionCode` — but it DOES keep `namespace`. Keep the existing `namespace`. Remove nothing else. (fill-memory keeps `largeHeap`/service/perms via its manifest.)

- [ ] **Step 6: Feature manifests → dist:module (both)**

`features/fillstorage/src/main/AndroidManifest.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
  xmlns:dist="http://schemas.android.com/apk/distribution">

  <dist:module
    dist:instant="false"
    dist:title="@string/feature_title_fill_storage">
    <dist:delivery>
      <dist:on-demand />
    </dist:delivery>
    <dist:fusing dist:include="true" />
  </dist:module>

  <application>
    <activity
      android:name="com.ryccoatika.sqatoolkit.fillstorage.FillStorageActivity"
      android:exported="false" />
  </application>
</manifest>
```
`features/fillmemory/src/main/AndroidManifest.xml`: same `<dist:module>` (title `@string/feature_title_fill_memory`), KEEPING its existing `<uses-permission>` (SYSTEM_ALERT_WINDOW, VIBRATE), `<application android:largeHeap="true">`, the `.FillMemoryActivity` activity, and the `FloaterService` service. `dist:title` references the base string.

- [ ] **Step 7: Base strings**

Add to `app/src/main/res/values/strings.xml` (values used by both `dist:title` and the launcher registry):
```xml
<string name="feature_title_fill_storage">Fill Storage</string>
<string name="feature_desc_fill_storage">Fill device storage with dummy files</string>
<string name="feature_title_fill_memory">Fill Memory</string>
<string name="feature_desc_fill_memory">Stress-test device RAM</string>
```
(Use the real titles/descriptions from the features' current `fs_title`/`fs_desc`/`fm_title`/`fm_desc` string values — read them and copy the text.)

- [ ] **Step 8: SplitCompatApplication**

`SQAToolsApplication`:
```kotlin
import com.google.android.play.core.splitcompat.SplitCompatApplication
...
class SQAToolsApplication : SplitCompatApplication() {
  val component: ApplicationComponent by unsafeLazy { ApplicationComponent::class.create(this) }
}
```
(`SplitCompatApplication` extends `Application`, so `component` and everything else stay.)

- [ ] **Step 9: Remove dead SQAFeature wiring**

- `ApplicationComponent.kt`: remove the `FillStorageFeature`/`FillMemoryFeature` imports and the two supertypes, leaving `abstract class ApplicationComponent(...) { @Provides fun provideContext(...) ... }` with no feature supertypes (keep the `Context` provider added earlier).
- Delete `features/fillstorage/.../FillStorageFeature.kt`, `.../inject/FillStorageFeature.kt`, `features/fillmemory/.../FillMemoryFeature.kt`, `.../inject/FillMemoryFeature.kt`.
- Delete `common/.../SQAFeature.kt`.
- NOTE: this leaves `Tools.kt`/`FeatureCard.kt`/`PreviewDummyData.kt` referencing the removed `SQAFeature` — they are reworked in Task 2. THIS TASK WILL NOT COMPILE STANDALONE. Do the build gate at the END of Task 2 (Tasks 1 + 2 are one compile unit — implement them together, one dispatch).

- [ ] **Step 10: (compile deferred to Task 2)** — do NOT commit a broken tree; commit once at Task 2 with the launcher rework. (Executor: implement Task 1 + Task 2, then a single commit.)

---

### Task 2: Base registry + FeatureInstallManager + launcher rework

**Files:**
- Create: `app/.../feature/FeatureDescriptor.kt`, `app/.../feature/FeatureRegistry.kt`, `app/.../feature/FeatureInstallManager.kt`, `app/.../feature/FeatureInstallState.kt`
- Modify: `app/.../ui/tools/Tools.kt`, `app/.../ui/tools/widget/FeatureCard.kt`, `app/.../ui/common/utils/preview/PreviewDummyData.kt`
- Add strings: `app/src/main/res/values/strings.xml` (button labels)

**Interfaces:**
- Consumes: renamed split names `"fillstorage"`/`"fillmemory"`, base feature strings.
- Produces:
  - `data class FeatureDescriptor(val featureId: String, val moduleName: String, @StringRes val title: Int, @StringRes val description: Int, val icon: ImageVector, val activityFqn: String)`
  - `object FeatureRegistry { val features: List<FeatureDescriptor> }`
  - `sealed interface FeatureInstallState { NotInstalled; data class Downloading(val progress: Float); Installing; Installed; data class Failed(val message: String) }`
  - `class FeatureInstallManager(context)` with `fun stateFlow(moduleName): StateFlow<FeatureInstallState>` (or a combined `StateFlow<Map<String, FeatureInstallState>>`), `fun install(moduleName)`, `fun uninstall(moduleName)`, `fun launch(descriptor, context)`, plus confirmation-dialog handling.

- [ ] **Step 1: FeatureInstallState + FeatureDescriptor + FeatureRegistry**

```kotlin
// FeatureInstallState.kt
package com.ryccoatika.sqatoolkit.feature

internal sealed interface FeatureInstallState {
  data object NotInstalled : FeatureInstallState
  data class Downloading(val progress: Float) : FeatureInstallState
  data object Installing : FeatureInstallState
  data object Installed : FeatureInstallState
  data class Failed(val message: String) : FeatureInstallState
}
```

```kotlin
// FeatureDescriptor.kt
package com.ryccoatika.sqatoolkit.feature

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

internal data class FeatureDescriptor(
  val featureId: String,
  val moduleName: String,
  @StringRes val title: Int,
  @StringRes val description: Int,
  val icon: ImageVector,
  val activityFqn: String,
)
```

```kotlin
// FeatureRegistry.kt
package com.ryccoatika.sqatoolkit.feature

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.SdStorage
import com.ryccoatika.sqatoolkit.R

internal object FeatureRegistry {
  val features: List<FeatureDescriptor> = listOf(
    FeatureDescriptor(
      featureId = "FILL_STORAGE",
      moduleName = "fillstorage",
      title = R.string.feature_title_fill_storage,
      description = R.string.feature_desc_fill_storage,
      icon = Icons.Rounded.SdStorage,
      activityFqn = "com.ryccoatika.sqatoolkit.fillstorage.FillStorageActivity",
    ),
    FeatureDescriptor(
      featureId = "FILL_MEMORY",
      moduleName = "fillmemory",
      title = R.string.feature_title_fill_memory,
      description = R.string.feature_desc_fill_memory,
      icon = Icons.Rounded.Memory,
      activityFqn = "com.ryccoatika.sqatoolkit.fillmemory.FillMemoryActivity",
    ),
  )
}
```
NOTE: `moduleName` MUST equal the module's actual split name. After the rename the modules are `:features:fillstorage`/`:features:fillmemory`, whose split names are `fillstorage`/`fillmemory`. VERIFY at build: if AGP reports a different split name (check the merged feature manifest's `split="…"` or a build error), update `moduleName` to match. Verify `Icons.Rounded.Memory`/`SdStorage` resolve (material-icons-extended); swap to a valid icon if not.

- [ ] **Step 2: FeatureInstallManager**

Wrap `SplitInstallManager` with the callback listener API (version-stable) and expose per-module state. Handle `REQUIRES_USER_CONFIRMATION`, `DOWNLOADING` progress, `INSTALLED`, `FAILED`, `CANCELED`.

```kotlin
package com.ryccoatika.sqatoolkit.feature

import android.content.Context
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class FeatureInstallManager(context: Context) {
  private val manager: SplitInstallManager = SplitInstallManagerFactory.create(context.applicationContext)

  private val states = MutableStateFlow(
    FeatureRegistry.features.associate { d ->
      d.moduleName to if (d.moduleName in manager.installedModules) {
        FeatureInstallState.Installed
      } else {
        FeatureInstallState.NotInstalled
      }
    },
  )
  val statesFlow: StateFlow<Map<String, FeatureInstallState>> = states.asStateFlow()

  // pending user-confirmation state, surfaced to the UI to launch Play's dialog
  val pendingConfirmation = MutableStateFlow<SplitInstallSessionState?>(null)

  private val listener = SplitInstallStateUpdatedListener { state ->
    val name = state.moduleNames().firstOrNull() ?: return@SplitInstallStateUpdatedListener
    val next = when (state.status()) {
      SplitInstallSessionStatus.PENDING -> FeatureInstallState.Downloading(0f)
      SplitInstallSessionStatus.DOWNLOADING -> {
        val total = state.totalBytesToDownload().coerceAtLeast(1L)
        FeatureInstallState.Downloading(state.bytesDownloaded().toFloat() / total)
      }
      SplitInstallSessionStatus.INSTALLING -> FeatureInstallState.Installing
      SplitInstallSessionStatus.INSTALLED -> FeatureInstallState.Installed
      SplitInstallSessionStatus.FAILED -> FeatureInstallState.Failed("Install failed (${state.errorCode()})")
      SplitInstallSessionStatus.CANCELED -> FeatureInstallState.NotInstalled
      SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
        pendingConfirmation.value = state
        FeatureInstallState.Downloading(0f)
      }
      else -> return@SplitInstallStateUpdatedListener
    }
    states.update { it + (name to next) }
  }

  fun register() = manager.registerListener(listener)
  fun unregister() = manager.unregisterListener(listener)

  fun install(moduleName: String) {
    if (moduleName in manager.installedModules) {
      states.update { it + (moduleName to FeatureInstallState.Installed) }
      return
    }
    states.update { it + (moduleName to FeatureInstallState.Downloading(0f)) }
    val request = SplitInstallRequest.newBuilder().addModule(moduleName).build()
    manager.startInstall(request).addOnFailureListener { e ->
      states.update { it + (moduleName to FeatureInstallState.Failed(e.message ?: "Install failed")) }
    }
  }

  fun uninstall(moduleName: String) {
    manager.deferredUninstall(listOf(moduleName))
    states.update { it + (moduleName to FeatureInstallState.NotInstalled) }
  }

  fun consumeConfirmation() { pendingConfirmation.value = null }

  fun splitInstallManager(): SplitInstallManager = manager
}
```
The confirmation-dialog `IntentSender` is launched from the Compose UI (Step 3) via `manager.startConfirmationDialogForResult(state, starter, REQUEST_CODE)` or, with `ActivityResultContracts.StartIntentSenderForResult`, by building an `IntentSenderRequest` from `state.resolutionIntent()?.intentSender`. Use whichever the resolved `feature-delivery` version supports; if the exact confirmation API is uncertain, wire the happy path (small modules install without confirmation) and leave a clearly-commented TODO for the confirmation dialog rather than guessing an API that won't compile — but attempt it first.

- [ ] **Step 3: Tools launcher rework**

Rework `Tools.kt` to drive off `FeatureRegistry` + a remembered `FeatureInstallManager` (registered/unregistered with the composition lifecycle via `DisposableEffect`), instead of the injected `Set<SQAFeature>`:

```kotlin
@Inject
@Composable
internal fun Tools() {
  val context = LocalContext.current
  val installManager = remember { FeatureInstallManager(context) }
  DisposableEffect(installManager) {
    installManager.register()
    onDispose { installManager.unregister() }
  }
  val states by installManager.statesFlow.collectAsState()

  Scaffold(
    topBar = { AppTopBar(title = stringResource(R.string.app_name)) },
  ) { paddingValues ->
    LazyColumn(
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      modifier = Modifier.padding(paddingValues),
    ) {
      items(FeatureRegistry.features, key = { it.featureId }) { descriptor ->
        FeatureCard(
          descriptor = descriptor,
          state = states[descriptor.moduleName] ?: FeatureInstallState.NotInstalled,
          onDownload = { installManager.install(descriptor.moduleName) },
          onOpen = { installManager.launch(descriptor, context) },
          onRemove = { installManager.uninstall(descriptor.moduleName) },
        )
      }
    }
  }
}
```
`Tools` no longer injects `Set<SQAFeature>` — its `@Inject` composable now takes no params (kotlin-inject still provides it; `AppScreens.tools` unchanged). Add a `launch(descriptor, context)` to `FeatureInstallManager`:
```kotlin
  fun launch(descriptor: FeatureDescriptor, context: Context) {
    com.google.android.play.core.splitcompat.SplitCompat.install(context)
    val intent = android.content.Intent(context, Class.forName(descriptor.activityFqn))
    context.startActivity(intent)
  }
```

- [ ] **Step 4: FeatureCard rework**

`FeatureCard(descriptor, state, onDownload, onOpen, onRemove)`: keep the branded icon-tile + title + description layout; the trailing area reflects `state`:
- `NotInstalled` → a `Button`/`FilledTonalButton` "Download".
- `Downloading` → `LinearProgressIndicator(progress = { state.progress })` + "%".
- `Installing` → an indeterminate spinner + "Installing".
- `Installed` → an "Open" `FilledTonalButton` + an overflow `IconButton` (`Icons.Rounded.MoreVert`) with a `DropdownMenu` "Remove".
- `Failed` → error text + a "Retry" button (calls `onDownload`).
Read the current branded `FeatureCard` and preserve its styling; swap the click/trailing behavior. Add the button-label strings to `app` strings (`feature_download`, `feature_open`, `feature_remove`, `feature_installing`, `feature_retry`).

- [ ] **Step 5: Preview providers**

Rewrite `PreviewDummyData.kt`: replace the `SQAFeature`-based providers with `FeatureDescriptor`-based ones (a `PreviewParameterProvider<FeatureDescriptor>` and, if used, a state variant), using dummy descriptors (real `R.string`/icons). Remove the `SQAFeature` import. Update `FeatureCard`/`Tools` `@Preview` signatures to match.

- [ ] **Step 6: Compile + bundle (Tasks 1 + 2 gate)**

Run: `./gradlew :app:assembleDebug` then `./gradlew :app:bundleDebug` — BOTH BUILD SUCCESSFUL under `-Werror`. If the split-name assumption was wrong, fix `FeatureRegistry.moduleName` (Step 1 note) and rebuild.

```bash
git add -A
git commit -m "feat: convert fill-storage/fill-memory to on-demand dynamic feature modules"
```

---

### Task 3: Verification + bundletool docs

**Files:**
- Create: `docs/dynamic-features-testing.md`

- [ ] **Step 1: Confirm builds**

Run `./gradlew :app:assembleDebug` and `./gradlew :app:bundleDebug` → BUILD SUCCESSFUL.

- [ ] **Step 2: Write the on-device test doc**

Create `docs/dynamic-features-testing.md` documenting the local on-demand test:
```
1. ./gradlew :app:bundleDebug
   → app/build/outputs/bundle/debug/app-debug.aab
2. bundletool build-apks --bundle=app-debug.aab --output=app.apks \
     --local-testing
3. bundletool install-apks --apks=app.apks
4. Launch app → tap "Download" on Fill Storage / Fill Memory →
   confirm the module downloads (progress), then "Open" launches it.
5. Test "Remove" (deferred uninstall; space reclaimed by Play later).
Notes: on-demand install only works from an App Bundle install path
(bundletool --local-testing or Play internal testing), NOT a plain
`:app:installDebug` APK.
```

- [ ] **Step 3: Commit**

```bash
git add docs/dynamic-features-testing.md
git commit -m "docs: bundletool local-testing steps for dynamic features"
```

## Self-Review Notes

- Spec coverage: rename (T1), catalog/gradle/dynamicFeatures/feature-delivery (T1), dist manifests (T1), SplitCompatApplication (T1), dead-code removal (T1), registry + descriptor (T2), FeatureInstallManager install/uninstall/launch/state (T2), launcher Download/progress/Open/Remove (T2), preview rework (T2), build gate + bundletool docs (T3). Covered.
- Tasks 1 + 2 are ONE compile unit (T1 removes `SQAFeature`, breaking the launcher until T2 reworks it) — implement together, single commit at T2 Step 6.
- KNOWN RISKS the implementer must resolve (not assume): (a) split name = `fillstorage`/`fillmemory` after rename — verify against the built feature manifest; (b) exact `feature-delivery` confirmation-dialog API — attempt, else wire happy-path with a commented TODO; (c) `com.android.dynamic-feature` module must not declare `applicationId`/`versionCode` (it inherits the base) — keep only `namespace`.
- `-Werror`: after deleting `SQAFeature` and reworking previews, ensure no dangling imports.
