# Technical Decisions — Bearings concept prototype (as-built)

**Status**: Accepted (prototype scope, complete)
**Date**: 2026-06-30

This is the authoritative record of what was actually built, in what final form, and why
— reconciled against the original plan. Earlier per-decision ADRs exist in the planning
repo (`bearings/docs/decisions/0001`–`0005`) and describe the *initial* reasoning; several
of those decisions were revised during implementation once real Developer Preview
constraints surfaced. This document is the single source of truth for the code as it
actually ships in this repo.

## Context

The goal for the day: a five-card glanceable stack demonstrating the Bearings product
concept, running in the AI Glasses emulator, with a recorded demo as the primary
deliverable. The original plan assumed the DP4-era `androidx.xr.core`/`androidx.xr.compose.glimmer`
artifact coordinates and a `setGlimmerContent { }` API pattern. Neither survived contact
with the actual SDK.

## What was built

- **Kotlin + Jetpack Compose**, on **Android Gradle Plugin 9.1.1** with **Kotlin 2.2.10**
  (AGP 9's built-in Kotlin support — `kotlin-android` is *not* applied as a separate
  plugin, applying both throws `Cannot add extension with name 'kotlin'`).
- **Jetpack XR SDK**: `androidx.xr.runtime:runtime`, `androidx.xr.compose:compose` —
  the DP3-era `androidx.xr.core:core` group ID does not exist; it was renamed to
  `androidx.xr.runtime:runtime` post-DP3. Pin exact versions; these are Developer
  Preview artifacts with no stability guarantee between releases.
- **Jetpack Compose Glimmer** (`androidx.xr.glimmer:glimmer`): native `Card` (with
  `header`/`leadingIcon`/`title`/`subtitle` slots — not a manually laid-out `Column`),
  `Text` with the official type scale (`GlimmerTheme.typography.titleMedium` /
  `.bodySmall` / `.caption`), and `GlimmerTheme.colors.primary` for the low-power green
  accent. `androidx.compose.material.icons.extended` supplies the per-card icons
  (`Schedule`, `Train`, `LocalCafe`, `Translate`, `Mic`).
- **Jetpack Projected** (`androidx.xr.projected:projected`): routes `GlassesActivity` to
  the real projected glasses display via `android:requiredDisplayCategory=
  "android.hardware.display.category.XR_PROJECTED"` and the `XR_PROJECTED_LAUNCHER`
  intent category in the manifest (replacing the standard `LAUNCHER`/`DEFAULT`
  category, which lands on the phone host instead).
- **`Crossfade`** between cards (220ms) instead of an instant swap — an abrupt
  unmount/remount read as a blank flash; a short cross-fade reads as calm rather than
  broken, consistent with the product's "calm technology" principle.
- **No backend**: `CardRepository`, `CalendarRepository`, `RoutesRepository` are
  designed as direct clients of public Google APIs — no intermediary server.
  `MockCardRepository` supplies the five prototype cards; real Calendar/Routes
  integration is scaffolded under `data/stretch/` behind the same interfaces, not wired
  up (stretch scope, not attempted — see "What was not built" below).

  ![Bearings architecture — the app talks directly to public Google APIs, no backend](assets/architecture-diagram.png)
- **Single Android `app` module** — no premature `core`/`data`/`domain` split.
- **Content**: the five cards use a real, verifiable Quito, Ecuador scenario (La
  Mariscal, the Metro's San Francisco station near Centro Histórico, Café La Ronda on
  the real La Ronda street) in English, except the sign-translation card, which
  intentionally keeps the Spanish word being translated ("'Salida' means Exit").

## Key deviations from the original plan (and why)

### 1. Glasses-direct entry point, not phone-first

`GlassesActivity` targets the glasses display directly — there is no separate phone
activity or Jetpack Projected *layer* in this feature (Projected is used here only for
*display routing*, not for a phone-app-with-projected-augmentation architecture). This
is an intentional, prototype-scoped deviation from the target phone-first MVP
architecture. Do not copy this pattern into the MVP codebase — M1 (phone-app MVP) and M2
(glasses projection layer on top) supersede it.

### 2. Native Glimmer components, not Material3

The initial implementation used Material3 (`CardComposable` with a manual `Column`,
`darkColorScheme`). It was rewritten to use Glimmer's own `Card` slots and type scale
once the theming/accent color regressed during the Material3 pass — using the design
system's own components is both more correct and less code than hand-rolling an
approximation of it.

### 3. Single-card navigation, not a scrollable list

A `LazyColumn` of focusable Glimmer `Card`s (with `Modifier.focusGroup()`) was tried and
abandoned: with `userScrollEnabled = false`, only the ~2 cards that fit in the 450px
viewport ever received focus, since there was no scroll mechanism left to compose cards
3–5 into the tree. The product spec calls for one card at a time regardless ("never a
list/dashboard"), so the final implementation renders a single `CardComposable` and
advances an index, which sidesteps the issue entirely.

### 4. Input handling: pointer gestures, not D-pad key events

Glimmer's own documentation describes touchpad swipes as translating into D-pad-style
focus-navigation key events. Raw `getevent` capture on this emulator build showed the
opposite: the touchpad delivers **absolute touch/drag coordinates**
(`ABS_MT_POSITION_X/Y`, `BTN_TOUCH`) to the projected display, i.e. ordinary pointer
input. `CardStackComposable` handles both — `detectTapGestures` /
`detectHorizontalDragGestures` (confirmed working) and `onKeyEvent` for D-pad codes
(also confirmed working, via direct `adb shell input keyevent` injection) — since real
hardware may differ from this emulator's behavior.

### 5. Emulator touchpad simulation is unreliable — known limitation, not a code bug

Interacting with the emulator's on-screen touchpad strip intermittently hands focus to
the glasses system UI (`com.google.android.glasses.core/.../OverlayStandardActivity` or
`HomeActivity`) instead of reaching the app, confirmed via `dumpsys activity activities`
across repeated identical attempts. Both gesture paths (tap/drag and key-event) are
independently verified correct via direct `adb shell input` injection, which exercises
the exact same `advance()`/`goBack()` code the touchpad would trigger. The demo recording
was driven this way — visually and behaviorally identical to a real swipe.

### 6. The virtual display ID is not stable across sessions

The `ProjectionDisplayRestricted` display's numeric ID changes across emulator restarts
(observed 5, then 15, then 17, then 19...). Any launch script must re-detect it each
session:

```bash
adb shell dumpsys display | grep -oE "displayId=[0-9]+, uniqueId='virtual:[^']*ProjectionDisplayRestricted[^']*'" | grep -oE "displayId=[0-9]+" | grep -oE "[0-9]+"
```

## What was not built (explicitly out of scope today)

- Real Google Calendar / Routes API integration (`GoogleCalendarRepository`,
  `GoogleRoutesRepository` — interfaces exist under `data/stretch/`, unimplemented).
- Credential Manager / Sign-in with Google.
- Phone-first architecture / Jetpack Projected as an actual phone→glasses augmentation
  layer (current use of Projected is display-routing only).
- Error handling, offline behavior, production key management for API credentials.

## Verification

- `./gradlew assembleDebug` — clean build.
- Launched on the real `ProjectionDisplayRestricted` display (not the phone fallback,
  not a simulation) — confirmed via `dumpsys activity activities | grep mCurrentFocus`.
- All 5 cards navigate via tap and drag; position indicator updates; no crashes.
- Visual rules pass: no `Color.White` fills, dark/semi-transparent surface, near-white
  text, green accent (`GlimmerTheme.colors.primary`) on the highlighted span, native
  Glimmer rounded card shape.
- Screen recording captured of the full 5-card navigation sequence.
