# Playful, Animated UI/UX Pass — Design

Date: 2026-08-04
Modules: `common`, `app`, `features/fillstorage`, `features/fillmemory`

## Goal

Elevate the already-branded app from "clean & consistent" to "beautiful, vibrant,
delightful, easy to use" for QA engineers / testers. A vibrancy + motion layer on
top of the existing branded Material3 system — not a re-brand. UI-layer only; no
logic/data/service changes.

## Decisions (locked with the user)

- **Style:** playful & vibrant.
- **Motion:** rich but tasteful (animated meters, count-up, transitions, press
  feedback, animated download progress).
- **Delights (all):** gradient hero headers, animated meters + count-up, Android 12+
  splash screen, iconographic empty/loading states.
- **Accents:** per-feature vibrant hues (Fill Storage and Fill Memory each get their
  own bright accent).
- **Empty states:** iconographic (large tinted icon + friendly copy) — no custom art
  assets.

## Baseline (already shipped, on `develop`)

Branded Material3 (indigo/teal/slate) light+dark + dynamic-color toggle;
`SectionCard`, `usageStatusColor`/`StatusColors`, `MonoValueTextStyle`, `AppTopBar`;
launcher with dynamic-feature Download/Open/Remove cards; fillstorage/fillmemory
restyled with status-graded meters/charts. This pass adds vibrancy + motion; the
dynamic-color toggle keeps working (accents are an additive brand layer, independent
of the dynamic scheme).

## Vibrancy layer (`common`)

### Per-feature accents

- New `common` `FeatureAccent` — a small set of vibrant accent definitions, each with
  a base color + a lighter/gradient companion, defined for light and dark:
  - `Storage` — a vivid cyan/blue.
  - `Memory` — a vivid magenta/coral.
  (Exact hex chosen in the plan; bright + accessible on the branded surfaces.)
- `FeatureDescriptor` (base) gains an `accent: FeatureAccent` (or `Color`), so the
  launcher card and the feature's own hero share the hue. Feature screens reference
  the same `FeatureAccent` from `common` directly.

### Shapes

- Lean into larger corner radii for a friendly, rounded feel — either customize the
  `MaterialTheme.shapes` (medium/large/extraLarge bumped) in `common` theme, or apply
  `extraLarge` at key surfaces (heroes, cards). Prefer customizing `Shapes` centrally.

## Reusable components (`common`)

- **`GradientHero`** — a rounded header with an accent gradient background, slots for a
  title, subtitle, and an optional prominent stat/trailing content. Used by the
  launcher and each feature home (in that feature's accent). Replaces the flat
  `AppTopBar` at the top of those screens (a scrolled-away collapsing effect is
  optional, not required).
- **`AnimatedCountText`** — animates a numeric value (Int/Float/formatted) counting up
  to its target when it changes (`animateFloatAsState` + a formatter lambda).
- **`AnimatedMeter` helpers** — a small API (or documented pattern) wrapping
  `animateFloatAsState` for a 0..1 fraction and `animateColorAsState` for the
  status color, so meters (ring/bar/graph) animate to value and smoothly shift
  green→amber→red. Existing `usageStatusColor` feeds the target color.
- **`EmptyState`** — large tinted icon in a soft rounded container + a friendly title +
  message (+ optional action button). Used for empty dummy-files, zero-usage, and
  loading placeholders.

## Motion (rich, tasteful)

- **Press feedback:** cards/buttons get a subtle scale-down + ripple on press
  (`Modifier.clickable` with an interaction source + `animateFloatAsState` scale, or
  a shared `pressable` modifier in `common`).
- **Entrance:** launcher feature cards fade/slide in with a short stagger.
- **Meters/numbers:** animate on first composition and on value change (above).
- **Download progress:** the dynamic-feature Download bar animates smoothly between
  progress values (`animateFloatAsState`) rather than jumping.
- **Expand/nav:** keep/enhance the existing `AnimatedVisibility` expand and nav
  crossfade; no heavy custom transitions.
- Keep motion durations short (150–400ms), standard easing; nothing that blocks
  interaction.

## Splash screen (`app`)

- Add `androidx.core:core-splashscreen`; call `installSplashScreen()` in the launcher
  activity (`MainActivity.onCreate`, before `setContent`). Define a splash theme
  (`Theme.App.Starting`) with `windowSplashScreenBackground` = brand color and
  `windowSplashScreenAnimatedIcon` = the app icon; set it as the activity's theme in
  the manifest. Keep it simple (static icon on brand background); optional gentle
  exit fade.

## Per-module application

### app
- Launcher (`Tools`): `GradientHero` header; feature cards colored/tinted by their
  accent, with press feedback + staggered entrance + animated download progress; the
  Download/Open/Remove states keep working.
- Settings: `GradientHero` (neutral/brand accent) + the existing cards, light polish.
- `MainActivity`: install splash; nav transition polish.

### fillstorage
- Home: `GradientHero` in the Storage accent with the headline used/total stat
  (animated count-up + animated bar); storage cards/charts animate to value;
  empty/loading via `EmptyState`.
- Manage: accent-consistent meters/progress animate; option controls keep branded
  styling with press feedback.

### fillmemory
- Home: `GradientHero` in the Memory accent; the memory ring/graph animates to value
  with animated count-up and smooth status-color transition; empty/loading via
  `EmptyState`.
- Options + clear: press feedback; branded.

## Out of scope

- No changes to ViewModels/usecases/services/`core` models or feature logic.
- No custom-drawn illustration assets (iconographic only).
- No re-theming of the base palette (branded scheme + dynamic toggle stay); accents
  are additive.
- No collapsing-toolbar framework unless trivial; a static gradient hero is enough.

## Error handling / edge cases

- Reduced-motion / accessibility: keep animations short and non-essential; the UI is
  fully usable if animations are skipped. (Optional: respect
  `Settings.Global.ANIMATOR_DURATION_SCALE` = 0 by snapping to end values.)
- Count-up / meter animations must land exactly on the true value (no rounding drift).
- Dynamic-color ON: heroes use the fixed per-feature accents (intended); the rest of
  the UI follows the dynamic scheme — verify the accents still read acceptably on a
  dynamic surface (they're strong hues, should be fine).
- Splash on API < 31: `core-splashscreen` backports a basic splash; verify no crash on
  API 24.
- Empty states render cleanly in light + dark.

## Testing / verification

No unit-test infra (established) — do NOT add test deps. Build gate:
`./gradlew :app:assembleDebug` AND `./gradlew :app:bundleDebug` BUILD SUCCESSFUL under
`-Werror`. Manual smoke: splash on launch; launcher hero + colored animated cards;
each feature's accent hero + animated meters/count-up; empty states; light + dark;
dynamic-color toggle still flips the base scheme; nothing functional regressed.

## Build sequencing (for the plan)

1. **common foundation**: `FeatureAccent` palette + shapes; `GradientHero`;
   `AnimatedCountText` + meter/color animation helpers; `EmptyState`; a `pressable`
   modifier.
2. **app launcher + splash**: hero, accent-colored animated cards, staggered entrance,
   animated download progress, splash screen; settings hero/polish; `MainActivity`.
3. **fillstorage**: accent hero + animated storage meters/charts + empty states.
4. **fillmemory**: accent hero + animated memory ring/graph + count-up + empty states.
5. **Build verification** (`:app:assembleDebug` + `:app:bundleDebug`) + manual smoke.
