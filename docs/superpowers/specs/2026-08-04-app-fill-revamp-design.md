# App + Fill-Memory + Fill-Storage UI Revamp — Design

Date: 2026-08-04
Modules: `common` (small shared kit), `app` (launcher, settings, nav), `features/fill-memory`, `features/fill-storage`

## Goal

Extend the branded design language established in `features/dev-info` to the rest
of the app: the launcher + settings + bottom nav (`app` module) and the two
remaining feature modules (`fill-memory`, `fill-storage`). One cohesive
consistency pass so every screen shares the same look. UI-layer only — no
ViewModel / usecase / data changes.

## Baseline (already done, on `develop`)

- Branded Material3 light+dark theme (indigo/teal/slate) is the app-wide default,
  with a DataStore-persisted dynamic-color toggle (`ThemePreferences`).
- `common` theme exposes `StatusColors` + `LocalStatusColors`, `MonoValueTextStyle`,
  refined `Typography`; `common.ui.AppTopBar` exists.
- These already restyle every module's COLORS; this revamp redesigns the
  LAYOUTS/components of the remaining modules to match dev-info's polish.

## Decisions (locked with the user)

- **Base:** dev-info revamp merged to `develop`; this work branches off `develop`.
- **Depth:** full restyle of fill-memory / fill-storage screens AND widgets
  (graphs, charts, progress, option forms).
- **Settings:** build the empty `Settings` screen; it hosts the app-wide
  dynamic-color toggle + an About section. dev-info keeps its overflow toggle
  (both read the same `ThemePreferences`, so they stay in sync).
- **Launcher:** vertical list + app header/hero + richer feature cards.
- **Meters/charts:** status-graded — green → amber → red as usage rises (reusing
  `StatusColors`); a helper maps a 0..1 fraction to the status color.
- **Structure:** one combined spec + plan; per-module implementation tasks.

## Shared kit (common module)

Add a few small, focused primitives to `common/ui/` so app + fill-* (and
dev-info, opportunistically) share them instead of each re-rolling card styling:

- `SectionCard(title: String? = null, icon: ImageVector? = null, content)` — a
  rounded `surfaceContainer` card with an optional icon+uppercase-tinted-title
  header, matching dev-info's group card. Used by settings, fill-* option/stat
  groups.
- `usageStatusColor(fraction: Float): Color` (in `common.ui.theme`, reads
  `LocalStatusColors`) — maps 0..~0.6 → success, ~0.6..0.85 → warning, >0.85 →
  error, for meters/charts.
- Spacing tokens object (e.g. `Dimens` with `screenPadding`, `cardGap`) — light,
  optional; only if it removes real duplication.

Keep this minimal (YAGNI). Do NOT retrofit dev-info in this effort beyond
trivially swapping to `SectionCard` if it's a clean drop-in; dev-info is already
shipped.

## App module

### Launcher (`ui/tools/Tools.kt`, `ui/tools/widget/FeatureCard.kt`)

- Add a header: an `AppTopBar` (or a hero header) titled "SQA Tools" with a short
  subtitle ("device & QA utilities").
- `FeatureCard`: icon inside a tinted rounded tile (`primaryContainer`), title
  `titleMedium`, description `bodySmall`/muted (2–3 lines), trailing chevron
  (`Icons.Rounded.ChevronRight`); clickable ripple; comfortable padding; cards
  spaced in the list.
- Keep the `Set<SQAFeature>`-driven list; only restyle.

### Settings (`ui/settings/Settings.kt`) — currently empty

- Build a real screen with `AppTopBar` ("Settings") and `SectionCard`s:
  - **Appearance:** a row with a `Switch` for "Dynamic color (Material You)" bound
    to `ThemePreferences.useDynamicColor` (collect + `setDynamicColor`); gray it
    out / hide with a note on API < 31 (dynamic unsupported).
  - **About:** app name + version (`BuildConfig.VERSION_NAME` or PackageManager),
    maybe a one-line description.
- Settings needs a `SettingsViewModel` OR can read `ThemePreferences` directly in
  the composable (mirroring how `DevInfoActivity` does it). Prefer a tiny
  `SettingsViewModel` (injected `ThemePreferences`) exposing `dynamicColor`
  state + `toggle()`, for testability/cleanliness — but reading the pref inline
  is acceptable if simpler and consistent with the codebase. Decide in the plan.
- `Settings` is invoked from `MainActivity`'s bottom-nav — wire the new content.

### Bottom nav (`ui/MainActivity.kt`)

- Polish the `NavigationBar`: branded colors come free from the theme; ensure
  selected/unselected icon+label states read well; keep the existing
  Crossfade/navigation logic. Minimal structural change.

## fill-memory (full restyle)

- **Home (`ui/home/Home.kt`)**: wrap in a branded scaffold + `AppTopBar`
  ("Fill Memory"); lay out the stat + options + graph in `SectionCard`s with
  consistent spacing.
- **`FillMemoryProgress`**: restyle the progress indicator; color by fill level
  via `usageStatusColor` (green when low RAM pressure → red as it fills); show
  used/total with clear typography.
- **`FillMemoryOptions`**: branded option controls (sliders/steppers/buttons) —
  Material3 components themed; group in a `SectionCard`.
- **`MemoryGraph`**: recolor the graph to the brand/status palette (used segment
  = `usageStatusColor`, free = `surfaceVariant`); keep its data/redraw logic.
- **Floater (`ui/home/floater/service/FloaterButton.kt`, `FloaterClosePlaceholder.kt`)**:
  theme the floating button + close placeholder to brand colors; keep the service
  behavior untouched.

## fill-storage (full restyle)

- **Home (`ui/home/Home.kt`, `widget/StorageCard.kt`)**: branded scaffold +
  `AppTopBar` ("Fill Storage"); `StorageCard` restyled as a stat card with a
  status-graded usage meter (used/total).
- **Manage (`ui/manage/Manage.kt` + widgets)**: `FillStorageField` (branded
  `OutlinedTextField`/`TextField`), `FillStorageOptions` (branded controls),
  `FillStorageProgress` (status-graded), `StorageChart` (recolored like
  `MemoryGraph`), grouped in `SectionCard`s.
- **DummyFiles (`ui/dummyfiles/DummyFiles.kt`)**: restyle the file list rows
  (branded list items; consistent with dev-info rows where sensible).

## Out of scope

- No changes to ViewModels, usecases, services' behavior, or data.
- No new features/functionality — visual/layout restyle only.
- No custom fonts; system + `MonoValueTextStyle` only where monospace fits
  (e.g. numeric byte values, optional).
- Not pulling in a full charting library — recolor existing custom graphs.

## Error handling / edge cases

- Dynamic-color switch on API < 31: disabled with an explanatory caption; theme
  falls back to branded (already handled by `SQAToolsTheme`).
- Meters clamp fraction to 0..1; `usageStatusColor` handles out-of-range.
- Empty states (no dummy files, zero usage) render cleanly.
- Previews (`@Preview`/`@PreviewLightDark`) for restyled composables must compile
  and reflect the branded theme.

## Testing / verification

No unit-test infra (established). Verify per module: `./gradlew :app:compileDebugKotlin`
and per-feature `compileDebugKotlin`, then `./gradlew :app:assembleDebug`, all
under `-Werror`. Manual smoke: launcher header + cards; Settings toggle flips
palette app-wide and persists; fill-memory/fill-storage screens render branded
with status-graded meters in light + dark; floater themed; nothing functional
regressed.

## Build sequencing (for the plan)

1. **Shared kit** (common): `SectionCard`, `usageStatusColor`, optional spacing
   tokens. (App-wide compile.)
2. **App launcher**: header + `FeatureCard` restyle.
3. **App Settings**: build the screen (toggle + about) + nav wiring.
4. **fill-memory**: home + progress + options + graph + floater.
5. **fill-storage**: home + StorageCard + manage widgets + dummyfiles.
6. Full app assemble + manual smoke.
