# Figma Chinese MVP Readiness - 2026-06-05

Figma file:

```text
https://www.figma.com/design/vhk3qAl1ZDnmk8tOHDDqR4/Untitled?node-id=0-1&t=RwOp2wiFbTkPV8HZ-1
```

Related local Stitch export:

```text
/Users/yao/Downloads/stitch.zip
```

## Decision

Use this Chinese Figma file and the Chinese Stitch export as the new visual baseline for WhiteNoisePro MVP.

This design is not a perfect component library yet, but it is good enough to enter product development if implementation treats Figma as visual guidance instead of a strict component/code-generation source.

Recommended development mode:

```text
Fast MVP implementation with visual parity, not strict Figma component extraction.
```

## Confirmed Figma Access

Figma MCP can access the file and read metadata.

Confirmed page:

```text
Page 1
nodeId: 0:1
```

Confirmed screen frames:

```text
首页 (中文版)
nodeId: 1:2
size: 390 x 1164
```

```text
定时器 (中文版)
nodeId: 1:136
size: 390 x 884
```

```text
设置 (中文版)
nodeId: 1:746
rendered size: 430 x 884
```

The metadata also shows Library-like content with:

```text
Component/SoundItem
Component/SoundItem: Active
Section: Noise
Section: Home
噪音色彩
居家环境
```

## Screenshot Results

### Home

```text
nodeId: 1:2
rendered: 390 x 1164
status: pass
```

Home includes:

- Header
- Now playing hero card
- Play button
- Favorite / timer / edit actions
- Volume slider
- Recently used sounds
- Recommended scenes
- Mini Player

### Timer

```text
nodeId: 1:136
rendered: 390 x 884
status: pass
```

Timer includes:

- Large duration display
- Presets
- Fade-out controls
- End behavior options
- Start timer button
- Mini Player
- Bottom Nav

### Settings

```text
nodeId: 1:746
rendered: 430 x 884
status: usable with width issue
```

Settings includes:

- Theme row
- Audio quality row
- Start last mix toggle
- Offline download toggle
- Privacy policy row
- Restore purchases
- Mini Player
- Bottom Nav

Issue:

The rendered screenshot is 430px wide and shows a right-side white gutter. The screen should be normalized to 390px during implementation. This does not block MVP development.

## Component Readiness

The design has useful component-like naming:

```text
Persistent Mini Player
Mini Player (Persistent)
BottomNavBar
Component/SoundItem
Component/SoundItem: Active
Button - Play
Button - Pause
Button - Favorite
Button - Timer
Button - Edit Mix
Button - Settings
Input - Volume slider
```

However, this audit does not require them to be real Figma `COMPONENT` / `INSTANCE` objects before development.

For MVP implementation, map these visually to code components:

```text
AppScreen
TopBar
BottomNav
MiniPlayer
NowPlayingCard
VolumeSlider
RecentSoundCard
RecommendedSceneCard
SoundItem
SoundLayerRow
MixCard
TimerPresetChip
SettingsRow
ToggleRow
IconButton
PrimaryButton
```

## Known Issues To Handle In Code

### 1. Screen Height

Some screens are taller than a single phone viewport.

Implementation should use:

```text
Fixed header
Scrollable content
Fixed Mini Player
Fixed Bottom Nav
Safe-area aware bottom padding
```

### 2. Settings Width

Settings appears as 430px wide in render.

Implementation should standardize all mobile screens around:

```text
base width: 390
responsive range: 360-430
```

### 3. Mixed English Copy

Some copied Stitch/Figma content may still contain English, such as:

```text
Deep Rain Cascade
Playing
Midnight Storm
45m left
```

Replace during implementation:

```text
深雨层叠
正在播放
午夜雷雨
剩余 45 分钟
```

### 4. Tokens Are Visual Guidance

Do not wait for perfect Figma Variables.

Use the Stitch design document as initial token source:

```text
background: #111317
surface-container-low: #1a1c20
surface-container: #1e2024
surface-container-high: #282a2e
surface-variant: #333539
on-surface: #e2e2e8
on-surface-variant: #bec9c7
primary: #8ad3ce
secondary: #accae3
tertiary: #ffb599
```

Implementation may define these directly in app theme tokens rather than relying on Figma variables.

## MVP Development Recommendation

Proceed to OpenSpec Explore / Propose for fast MVP.

Suggested MVP scope:

1. App shell:
   - Home
   - Mixer
   - Library
   - Timer
   - Saved Mixes
   - Settings
   - Bottom Nav
   - Mini Player

2. Local state:
   - Current mix
   - Active sounds
   - Per-sound volume
   - Master volume
   - Timer duration
   - Saved mixes
   - Favorites

3. Audio MVP:
   - Use placeholder/local bundled sounds first
   - Multi-track playback if feasible in first pass
   - Otherwise implement UI and state first, then audio engine as the next task

4. Visual target:
   - Match the Chinese Stitch/Figma direction
   - Dark low-brightness UI
   - Large touch targets
   - No marketing home page

## Go / No-Go

Go for MVP development:

```text
YES
```

Reason:

- Main visual direction is clear.
- Key MVP screens exist in either Figma or the Stitch export.
- Home, Timer, and Settings render correctly enough.
- Component-like structure is sufficient for manual implementation.

Do not use direct automated Figma-to-code generation as the primary path yet.

Recommended path:

```text
Figma/Stitch as visual baseline -> OpenSpec proposal -> Superpowers TDD implementation -> visual verification against screenshots
```
