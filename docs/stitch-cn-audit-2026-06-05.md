# Stitch Chinese Export Audit - 2026-06-05

Source zip:

```text
/Users/yao/Downloads/stitch.zip
```

Extracted locally to:

```text
/Volumes/Volumes2T/VibeCodingProjects/WhiteNoisePro/work/stitch-cn
```

## Export Structure

The zip contains 6 generated HTML screens and screenshots:

```text
stitch/_1/code.html
stitch/_1/screen.png
stitch/_2/code.html
stitch/_2/screen.png
stitch/_3/code.html
stitch/_3/screen.png
stitch/_4/code.html
stitch/_4/screen.png
stitch/_5/code.html
stitch/_5/screen.png
stitch/_6/code.html
stitch/_6/screen.png
stitch/nocturne_ambient/DESIGN.md
```

## Screen Mapping

| Folder | Title | MVP Screen | Status |
|---|---|---|---|
| `_1` | `Sleep - 正在播放` | Home / Now Playing | Good visual direction |
| `_2` | `Timer - Digital Somnolence` | Timer | Mostly good, some English copy remains |
| `_3` | `已保存的混音` | Saved Mixes | Content matches, header says `库`; white background issue |
| `_4` | `Digital Somnolence - Mixer` | Mixer | Good visual direction |
| `_5` | `Sound Library` | Sound Library | Chinese content, title not localized; white background issue |
| `_6` | `设置` | Settings | Good visual direction, some English mini-player copy remains |

## Positive Findings

- The Chinese version covers the core MVP screens:
  - Home / Now Playing
  - Mixer
  - Library
  - Timer
  - Saved Mixes
  - Settings
- The visual direction is stronger than the previous Figma-imported version.
- Home, Mixer, Timer, and Settings mostly preserve the intended night-friendly dark UI.
- Core controls are represented:
  - Play / pause
  - Favorite
  - Timer shortcut
  - Edit mix
  - Master volume
  - Per-layer volume sliders
  - Mute / remove layer
  - Add sound
  - Save mix
  - Preset timers
  - Fade-out toggle
  - Saved mix cards
  - Mini player
  - Bottom navigation
- The package includes a useful design system document with colors, typography, radius, spacing, and component guidance.

## Important Issues

### 1. Some Screens Render With White Background

Affected screenshots:

```text
stitch/_3/screen.png
stitch/_5/screen.png
```

Observed:

- Saved Mixes has a dark header but the main content area is white.
- Library has a dark header but the main content area is white.

Likely cause:

- The HTML uses token-like class names such as `Token/Color/Background`.
- Some CSS uses Tailwind-only directives such as `@apply`.
- Some CSS uses `theme('colors.background')`.
- These do not reliably work in plain browser runtime with Tailwind CDN.

Impact:

- The design violates the low-brightness dark UI product constraint.
- Figma import/capture may preserve the broken white background.
- Code generation would inherit incorrect colors.

Required fix:

- Replace token-only background usage with direct utility classes or compiled CSS.
- Ensure each `body` and `main` explicitly uses dark background classes.

Examples:

```html
<body class="bg-background text-on-background ...">
<main class="bg-background ...">
```

or compiled CSS:

```css
.screen-root {
  background: #111317;
  color: #e2e2e8;
}
```

### 2. Stitch Screens Are Long Screenshots, Not Standard App Frames

Screenshot dimensions:

```text
_1: 536 x 1600
_2: 706 x 1600
_3: 623 x 1600
_4: 582 x 1600
_5: 441 x 1600
_6: 706 x 1600
```

Expected MVP design frame:

```text
390 x 844
```

Impact:

- These are useful as references, but not yet clean mobile frames.
- Figma import may create long web captures rather than app screen frames.
- Bottom navigation and mini-player placement can become hard to validate.

Required fix:

- Normalize each screen to a 390px mobile artboard.
- Define viewport height behavior.
- Keep scrollable content inside screen frames.
- Keep Mini Player and Bottom Nav fixed.

### 3. Bottom Mini Player / Navigation Overlap

Observed in Library:

- Mini Player and Bottom Nav visually collide.
- Some labels appear shifted or duplicated.
- The active nav item and mini-player text overlap in the screenshot.

Required fix:

- Reserve bottom padding for both Mini Player and Bottom Nav.
- Use one consistent vertical stack:

```text
scroll content
fixed Mini Player
fixed Bottom Nav
safe area inset
```

Recommended spacing:

```text
Bottom Nav height: 80px
Mini Player height: 64px
Gap: 8px
Scrollable content bottom padding: 168px + safe area
```

### 4. Inconsistent Localization

Remaining English or mixed copy:

```text
Timer - Digital Somnolence
Digital Somnolence - Mixer
Sound Library
Deep Rain Cascade
Playing
Midnight Storm
Playing • 45m left
Beta 波
```

Suggested replacements:

```text
Timer - Digital Somnolence -> 定时器
Digital Somnolence - Mixer -> 混音
Sound Library -> 声音库
Deep Rain Cascade -> 深雨层叠
Playing -> 正在播放
Midnight Storm -> 午夜雷雨
Playing • 45m left -> 正在播放 • 剩余 45 分钟
Beta 波 -> 贝塔波
```

### 5. Saved Mixes Header Is Wrong

`_3` title is `已保存的混音`, but visible header is `库`.

Required fix:

```text
库 -> 已保存
```

or:

```text
库 -> 我的混音
```

### 6. Tokens Are Present as Naming Intent, Not Figma Variables

The export includes token-like class names:

```text
Token/Color/Background
Token/Color/Surface
Token/Text/Title
Token/Text/Body
Component/MiniPlayer
Component/BottomNav
Component/SoundItem
Component/MixCard
Component/SoundLayerRow
```

These names are useful for design handoff, but they are not the same as actual Figma variables or components.

If imported directly, Figma MCP may still see HTML-derived layers or rasterized captures, not reusable Figma components.

## Figma MCP Readiness

Current status:

```text
Not directly ready for Figma MCP implementation.
Good as visual/product reference.
Needs normalization before import or code generation.
```

Reason:

- It is a Stitch HTML/PNG export, not an editable Figma file.
- Some pages have broken dark backgrounds.
- Screen dimensions are inconsistent.
- Some localization remains incomplete.
- Token and component names are embedded in HTML class names, not actual Figma design tokens/components.

## Recommended Next Step

Before importing this version into Figma:

1. Fix `_3` and `_5` dark backgrounds.
2. Normalize all screens to 390px mobile app frames.
3. Fix Mini Player and Bottom Nav overlap.
4. Complete Chinese localization.
5. Rename screen concepts:

```text
_1 -> Screen/Home
_2 -> Screen/Timer
_3 -> Screen/Saved Mixes
_4 -> Screen/Mixer
_5 -> Screen/Library
_6 -> Screen/Settings
```

6. After import into Figma, convert repeated structures into real components:

```text
Component/Bottom Nav
Component/Mini Player
Component/Sound Item
Component/Sound Layer Row
Component/Mix Card
Component/Slider
Component/Button/Icon
Component/Button/Primary
Component/Toggle Row
Component/Settings Row
```

7. Create actual Figma variables:

```text
Color/Background
Color/Surface
Color/Surface Elevated
Color/Text Primary
Color/Text Muted
Color/Accent
Color/Border
Text/Screen Title
Text/Section Title
Text/Body
Text/Caption
```

## Best Development Use

Use this Chinese Stitch export as:

- A visual reference for OpenSpec `design.md`
- A source for initial copywriting
- A source for token values
- A UI direction for Home, Mixer, Timer, Saved Mixes, Library, and Settings

Do not use it as:

- Final Figma source of truth
- Direct React Native / Flutter code source
- Final token implementation
- Final responsive layout without cleanup

## Decision

This Chinese version is a better product reference than the previous Figma import, but it still needs cleanup before Figma MCP automation.

Recommended flow:

```text
Clean local Stitch HTML/CSS -> import/capture into Figma -> create real Figma components/tokens -> rerun Figma MCP audit -> start OpenSpec explore/propose
```
