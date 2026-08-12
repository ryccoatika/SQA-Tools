# On-Demand Dynamic Feature Modules — Design

Date: 2026-08-04
Modules: `app` (base), `common`, `features/fill-storage`, `features/fill-memory`

## Goal

Convert the two feature modules (`fill-storage`, `fill-memory`) from statically
linked `com.android.library` modules into **on-demand dynamic feature modules**
(Play Feature Delivery). The launcher shows a Download button per feature; tapping
it downloads/installs the module at runtime, after which the feature opens. Users
can also remove an installed feature.

## Decisions (locked with the user)

- **Delivery:** on-demand (installed when the user taps Download; uninstallable).
- **Scope:** both `fill-storage` and `fill-memory` in one effort.
- **Dead code:** the base owns feature metadata; remove each feature's `SQAFeature`
  impl + `inject.*Feature` registration interface + the `ApplicationComponent`
  supertypes, and delete `common.SQAFeature` if nothing else uses it.
- **Uninstall:** include a Remove action per installed feature (deferred uninstall).
- **Verification:** build-only (`:app:bundleDebug`) + documented `bundletool`
  local-testing steps for the on-device install test (a plain APK cannot exercise
  on-demand install).

## Architecture

### Dependency direction reverses

Today `app` depends on the feature libraries and collects their `SQAFeature`
bindings via kotlin-inject (`ApplicationComponent : FillStorageFeature, …`). Dynamic
features **reverse** this: the feature module depends on `:app` (the base), so the
base can no longer reference feature classes or DI at compile time. The base must
discover and launch features WITHOUT compile-time knowledge of them.

### Base (`app`) module

- Gradle: keep `com.android.application`; add
  `android { dynamicFeatures = setOf(":features:fill-storage", ":features:fill-memory") }`.
- Dependencies: add Play `feature-delivery` + `feature-delivery-ktx`
  (`com.google.android.play:feature-delivery(-ktx):2.1.0`, added to the version
  catalog).
- `SQAToolsApplication` extends
  `com.google.android.play.core.splitcompat.SplitCompatApplication` (so code from a
  freshly-installed on-demand module is loadable without an app restart). If the app
  already extends a different base, instead override `attachBaseContext` and call
  `SplitCompat.install(this)`.

### Feature modules (`fill-storage`, `fill-memory`)

- Gradle plugin `com.android.library` → **`com.android.dynamic-feature`** (added to
  the catalog). Add `implementation(project(":app"))`. Keep their other deps (common,
  compose, ksp, etc.). Remove nothing functional except the dead `SQAFeature`/inject
  wiring (below).
- `AndroidManifest.xml`: add the dist namespace and a `<dist:module>`:
  ```xml
  <manifest xmlns:android="..." xmlns:dist="http://schemas.android.com/apk/distribution">
    <dist:module
      dist:instant="false"
      dist:title="@string/feature_title_fill_storage">
      <dist:delivery><dist:on-demand/></dist:delivery>
      <dist:fusing dist:include="true"/>
    </dist:module>
    ... existing <application>/<activity> ...
  </manifest>
  ```
  `dist:title` references a string in the **base** (`app`) resources.
- The feature's `Activity` stays and remains declared in the feature manifest.

### Split module name (KNOWN RISK — resolve at build)

`SplitInstallRequest.addModule(name)` uses the dynamic feature's **split name**.
Split names allow only letters/digits/underscores — hyphens may be rejected or
sanitized. The Gradle modules are `:features:fill-storage` / `:features:fill-memory`
(hyphenated). During implementation, determine the actual split name (from the AGP
build error, or the generated feature manifest's `split="…"` attribute) and:
- if the hyphenated name is accepted, use it as the descriptor's `moduleName`;
- if rejected, rename the modules to `fillstorage` / `fillmemory` (directory +
  `settings.gradle.kts` + the `projects.features.*` accessors + `dynamicFeatures`
  set) and use those.
The plan must handle this contingency explicitly; do not assume.

### Base feature registry (replaces DI discovery)

- New `FeatureDescriptor` (base): `featureId: String`, `moduleName: String` (split
  name), `@StringRes title: Int`, `@StringRes description: Int`, `icon: ImageVector`,
  `activityFqn: String` (e.g. `"com.ryccoatika.sqatoolkit.fillstorage.FillStorageActivity"`).
- New `FeatureRegistry` (base): a static `List<FeatureDescriptor>` for the two
  features.
- The launcher title/description strings and icons **move into the base** (`app`
  resources), since the base renders the card before the feature is installed. Add
  `feature_title_*` / `feature_desc_*` strings to `app`; icons come from
  material-icons (available in the base via `common`'s `api(materialIcons)`).

### FeatureInstallManager (base)

A wrapper around `SplitInstallManager` (via `feature-delivery-ktx`):
- Exposes install state per module as a Compose-observable state, modeled as a sealed
  type: `NotInstalled`, `Downloading(progress: Float)`, `Installing`, `Installed`,
  `Failed(message)`. Seeds `Installed` for modules already in
  `splitInstallManager.installedModules`.
- `install(moduleName)`: builds `SplitInstallRequest.newBuilder().addModule(name)`,
  calls `startInstall`, and maps `SplitInstallStateUpdatedListener` states
  (PENDING/DOWNLOADING/INSTALLING/INSTALLED/FAILED/CANCELED, and REQUIRES_USER_
  CONFIRMATION → launch the confirmation dialog) to the sealed state.
- `uninstall(moduleName)`: `deferredUninstall(listOf(name))` (deferred — reclaimed by
  Play later; UI reflects "removal requested").
- `launch(descriptor, context)`: after `Installed`, start the activity via
  `Class.forName(descriptor.activityFqn)` + `Intent` (SplitCompat makes the class
  resolvable). If launched immediately post-install in the same process,
  `SplitCompat.install(context)` / `SplitInstallHelper` may be needed — handle so the
  first open works without a manual restart.
- Register/unregister the state listener with the Activity/Compose lifecycle to avoid
  leaks; use `RequiresUserConfirmation` via `ActivityResultContracts.StartIntentSenderForResult`.

### Launcher UI (`app` `Tools` + `FeatureCard`)

- Drive the list from `FeatureRegistry` (not the old `Set<SQAFeature>`).
- Each card reflects the module's `FeatureInstallManager` state:
  - **NotInstalled** → a **Download** button (+ size hint if available).
  - **Downloading/Installing** → a progress indicator (bar + %).
  - **Installed** → an **Open** button (launches via the descriptor) + an overflow
    **Remove**.
  - **Failed** → error + Retry.
- Keep the branded card styling from the earlier revamp (icon tile, title, desc).

### Removals

- Feature modules: delete `FillStorageFeature`/`FillMemoryFeature` (`SQAFeature`
  impls) and `inject/FillStorageFeature.kt`/`FillMemoryFeature.kt` (registration
  interfaces).
- `app`: remove `FillStorageFeature`/`FillMemoryFeature` supertypes + imports from
  `ApplicationComponent`; rework/replace the `SQAFeature` preview parameter providers
  to `FeatureDescriptor`.
- `common`: delete `SQAFeature` if no remaining references (grep first; keep if used).

## Out of scope

- No behavior change inside the features (screens/logic stay as-is post-revamp).
- No conversion of `common` to dynamic (it's a shared library — stays static).
- No instant-app support (`dist:instant="false"`).
- No Play Console / signing setup — local bundletool testing only.

## Error handling / edge cases

- No network / download failure → `Failed` state + Retry; feature not opened.
- `REQUIRES_USER_CONFIRMATION` (large modules / mobile data) → surface Play's
  confirmation dialog via `StartIntentSenderForResult`; resume on result.
- Feature already installed at launch → card shows Open immediately.
- Reflective launch: `Class.forName` must resolve post-install; if it throws, show a
  "restart to finish install" fallback rather than crashing.
- Deferred uninstall doesn't free space immediately — the card should not claim
  instant removal (label it "Removal requested" / revert to Download on next launch).
- Split-name mismatch (hyphens) — see the KNOWN RISK; resolve at build.

## Testing / verification

- No unit-test infra (established) — do NOT add test deps.
- Build gate: `./gradlew :app:bundleDebug` BUILD SUCCESSFUL (produces the `.aab`
  with feature splits). Also `./gradlew :app:assembleDebug` should still build the
  base APK.
- Documented on-device path (for the user to run): `:app:bundleDebug` →
  `bundletool build-apks --bundle=app.aab --output=app.apks --local-testing` →
  `bundletool install-apks --apks=app.apks`, then tap Download on a feature and
  confirm it installs + opens; test Remove.

## Build sequencing (for the plan)

1. **Catalog + base gradle**: add `com.android.dynamic-feature` plugin +
   `feature-delivery(-ktx)` libs; set `app.dynamicFeatures`; `SplitCompatApplication`.
2. **Convert fill-storage**: plugin swap, `:app` dep, dist manifest, base title/desc
   strings; delete its `SQAFeature`/inject wiring. Resolve the split-name risk here.
3. **Convert fill-memory**: same conversion + removals.
4. **Base registry + FeatureInstallManager**: `FeatureDescriptor`, `FeatureRegistry`,
   the SplitInstall wrapper (install/uninstall/launch/state).
5. **Launcher rework**: `Tools`/`FeatureCard` driven by the registry + install state
   (Download/progress/Open/Remove); rework preview providers; remove
   `ApplicationComponent` supertypes; delete `common.SQAFeature` if unused.
6. **Build verification** (`:app:bundleDebug` + `:app:assembleDebug`) + document the
   bundletool steps.
