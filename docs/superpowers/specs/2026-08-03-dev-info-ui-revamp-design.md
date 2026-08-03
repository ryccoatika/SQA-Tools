# Dev-Info UI Revamp — Design

Date: 2026-08-03
Modules: `common` (theme, app-wide), `features/dev-info` (UI)

## Goal

Revamp the `features/dev-info` UI to be polished, user-friendly, and appealing
for the target user: **QA engineers / software testers**. Beyond visual polish,
add the day-to-day tester power-tools: copy-to-clipboard, global search, full
device-profile export/share, and status color-coding. No change to the data
gathering layer (usecases/utils stay).

## Decisions (locked with the user)

- **Visual identity:** a fixed branded "developer tool" palette as the default,
  with a persisted toggle back to Android dynamic ("Material You") color.
- **Theme scope:** app-wide (the theme lives in the shared `common` module, so it
  restyles every feature — accepted for consistency).
- **QA features (all):** copy-on-tap + copy-all, global search, export/share
  report, status color-coding.
- **Info row layout:** compact — muted label + monospace value.
- **Search + export scope:** global (across all 7 tabs).

## Architecture

### Single-source aggregator ViewModel

Global search and full export need every tab's data at once, so a Main-level
aggregator becomes the single source of truth, replacing the seven tiny per-tab
ViewModels.

- New `DevInfoViewModel` (`ui/main/`) injects all seven `Get{X}Info` usecases,
  runs them (each on IO via the usecase), and exposes:
  - `state: StateFlow<DevInfoViewState>` holding `tabs: List<TabData>` where
    `TabData(type: DevInfoType, items: List<Item>, isLoading: Boolean)`, plus
    a `query: String`.
  - `refresh(type: DevInfoType)` — re-runs one tab's usecase (needed when a
    permission is granted on Network/Connectivity).
  - `onQueryChange(q: String)`.
- The seven `{X}ViewModel` classes and the shared `InfoViewModel`/`InfoViewState`
  are removed; each tab composable becomes stateless, taking its
  `List<Item>` slice + an `onRequestPermission`/`refresh` callback from
  `Main`. The `Get{X}Info` usecases and `{X}Type` registrations are unchanged.
- `Main` reads `DevInfoViewModel` (via the existing `viewModel(factory)`
  extension) and drives the top bar, search, pager, and share.

Rationale: one load of each usecase (no double-running), one place to search and
export from, and per-tab refresh preserved for the permission flow.

### Export / full report

- New `DevInfoReportFormatter` (`core/utils/`, injects `Context` +
  `DevInfoTextCreator`) turns `List<TabData>` into a plain-text / lightweight
  Markdown report: a top title, then per-tab section headers and
  `label: value` lines (groups become sub-headers; expandable items become
  sub-sections). Reuses `DevInfoTextCreator` for tab titles.
- `Main` builds the report string from current state and launches an
  `ACTION_SEND` (`text/plain`) chooser. A per-tab share exports just that tab.

### Search

- `DevInfoViewModel.query` drives filtering. A pure function
  `filterItems(items, query): List<Item>` (in `ui/main/` or a small util)
  keeps a `GroupItem`/`ExpandableGroupItem` when its title or any descendant's
  label/value matches (case-insensitive substring); drops empties; row items
  match on label or value.
- UX: when `query` is non-empty, the pager is replaced by a single scrolling
  **search-results view** that renders each tab's filtered items under a tab
  header (only tabs with matches shown, with a match count). Empty → an
  "No matches" empty state. Clearing the query restores the pager.

## Visual system (common module)

### Palette

- Add a fixed light and dark `ColorScheme` to `common` theme (new
  `BrandColor.kt` / updated `Theme.kt`): indigo primary, teal secondary, slate
  neutral surfaces, plus containers. Provide sensible `onX`/`container` values.
- `SQAToolsTheme(dynamicColor: Boolean = false, ...)` — default flips to the
  branded scheme; `dynamicColor = true` uses Material You (API 31+) as today.
- **Semantic status colors:** a `StatusColors(success, warning, error, neutral)`
  holder provided via `LocalStatusColors`, with light/dark values, so status
  coding is theme-aware and not hard-coded per composable.

### Dynamic-color toggle persistence

- Persist the user's choice with **DataStore Preferences** (a single boolean
  `use_dynamic_color`). If DataStore is not already a dependency, add
  `androidx.datastore:datastore-preferences` (confirm in the plan). A minimal
  `ThemePreferences` (in `common`) exposes a `Flow<Boolean>` + a suspend setter.
- The `DevInfoActivity` collects the preference and passes it to
  `SQAToolsTheme(dynamicColor = ...)`. The top-bar toggle writes it.
- If adding DataStore proves out of scope, fall back to an in-memory
  `rememberSaveable` toggle (resets on process death) — decided in the plan,
  DataStore preferred.

### Typography

- Refine `common` `Typography`: define `titleLarge`, `titleMedium`,
  `labelLarge`, `labelMedium`, `bodyMedium` for a clear hierarchy. Values use
  `FontFamily.Monospace` via a dedicated style exposed to the info row (no font
  asset needed).

## Components (features/dev-info)

### Info row (`ListItemText` rewrite → value row)

- Compact row: label (`labelMedium`, `onSurfaceVariant`), value (`bodyMedium`
  **monospace**, `onSurface`), value wraps; trailing subtle copy glyph.
- Whole row is clickable → copies the value to the clipboard
  (`LocalClipboardManager`) and shows a snackbar "Copied <label>".
- **Status coding:** a UI-side `statusStyleFor(value): StatusColors?` maps known
  tokens — "Supported"/"Yes"/"Enforcing"/"Detected"/"Ready" → success;
  "Permissive"/"Unknown"/"Roaming" → warning; "Not Supported"/"No"/"Not
  Detected"/"Absent" → error — and tints the value + a small leading dot when
  matched. Neutral otherwise. `StatusItem` (boolean) uses the same colors.
- `RawTextItem` and `TextItem` render through this row; `PermissionItem` keeps
  its Grant button but adopts the same label/value styling.

### Group card (`ItemGroupComposer` rewrite)

- Header row: a **leading section icon** + uppercase tinted title
  (`labelMedium`, `primary`) + a trailing **copy-group** icon button (copies all
  `label: value` lines in the group). Header hidden when the group has no title.
- Card: `surfaceContainer` background, rounded corners, subtle tonal elevation;
  rows separated by thin dividers instead of 1dp gaps.
- Section icon comes from a `groupIcon(title): ImageVector` map (Processor→
  memory/CPU, GPU→ image, Memory→ memory, Storage→ storage, Display→ display,
  Battery→ battery, Android/System→ android, DRM→ shield/lock, SIM & Network→
  signal, Wi-Fi→ wifi, Bluetooth→ bluetooth, Other→ devices). Uses
  `material-icons-extended` (already available transitively).

### Hero device card (`ItemDeviceCardComposer` rewrite)

- `primaryContainer`/tonal background, rounded; Android version art on one side.
- Prominent device model + Android name (`titleLarge`), then labeled sub-lines:
  codename, API level, release date (`labelMedium` label + value). Optional
  copy-all for the header block.

### Expandable rows (`ItemExpandableGroupComposer`)

- Restyle to match the new row/card system (title + summary + animated chevron);
  expanded children use the new value rows. Keep per-instance expand state.

### Tabs + top bar (`Main` rewrite)

- Filter chips gain a **leading icon** per tab (`tabIcon(type)`), stronger
  selected state; pager unchanged structurally.
- `AppTopBar` already supports `actions` — add: **Search** (toggles an inline
  search text field in/under the bar bound to `query`), **Share** (exports —
  current tab; overflow "Share full report" for all tabs), and an **overflow**
  menu (dynamic-color toggle, expand/collapse-all placeholder if trivial).
- A `SnackbarHost` is added to the `Main` `Scaffold` for copy feedback.

## Out of scope

- No change to `Get{X}Info`/`*InfoUtils` data gathering.
- No new item data; copy/search/status all derive from existing `label`+`value`.
- No custom font files (system monospace only).
- "Expand/collapse all" is best-effort; drop if it complicates the expandable
  state model.

## Error handling / edge cases

- Empty search → "No matches" state; clearing restores pager.
- Copy of an empty/"-" value still works (copies the literal). Snackbar always
  shown.
- Share when a tab is mid-load includes whatever is loaded (loading tabs noted).
- Status classifier is additive: an unmatched value renders neutral (never
  mis-colors).
- Theme: on API < 31, dynamic color unavailable → toggle hidden or no-op,
  branded scheme used.

## Testing / verification

No unit-test infra in this repo (established). Verify by
`./gradlew :features:dev-info:compileDebugKotlin` + `:app:assembleDebug`
(under `-Werror`), plus manual: branded look in light/dark, toggle to dynamic,
tap-to-copy shows snackbar, search filters across tabs, share opens the chooser
with a formatted report, status colors on Supported/Not-Supported/root/SELinux.

## Build sequencing (for the plan)

1. `common` theme: branded palette + `StatusColors`/`LocalStatusColors` +
   typography + `SQAToolsTheme` default flip. (App still builds; other features
   restyle.)
2. Dynamic-color persistence (DataStore `ThemePreferences`) + `DevInfoActivity`
   wiring + toggle plumbing.
3. Aggregator `DevInfoViewModel` + `DevInfoViewState` + `TabData`; remove the 7
   per-tab VMs + `InfoViewModel`/`InfoViewState`; make tab composables stateless.
4. Info row + status classifier + group card + hero card + expandable restyle +
   clipboard/snackbar.
5. Tabs/top bar: icons, search field + global search-results view, share
   (current + full) via `DevInfoReportFormatter`.
6. Full app assemble + manual smoke.
