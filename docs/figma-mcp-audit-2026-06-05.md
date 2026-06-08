# Figma MCP Audit - 2026-06-05

Figma file:

```text
https://www.figma.com/design/cCCKb9ZPZ9IBoRSdUG3Iep/Untitled?node-id=1-1339&t=fwAyIBPzr8fCg7lg-0
```

## Scope

This audit verifies whether the imported Figma design is ready to support the WhiteNoisePro MVP development flow through Figma MCP and downstream React Native / Flutter implementation.

Requested validation:

1. Contains Home, Mixer, Library, Timer, Saved Mixes, and Settings screens.
2. Contains Bottom Nav, Mini Player, Sound Item, Mix Card, Slider, and related reusable components.
3. Checks for unnamed or generic layers.
4. Checks for rasterized text or buttons.
5. Checks color and typography tokens.
6. Evaluates readiness for React Native / Flutter generation.

## MCP Calls Performed

Read metadata:

```text
fileKey: cCCKb9ZPZ9IBoRSdUG3Iep
nodeId: 0:1
```

Read Library content grid:

```text
nodeId: 1:1339
name: Main Content Grid
```

Read screenshots:

```text
Screen/Mixer: nodeId 1:3
Screen/Home: nodeId 1:153
Screen/Settings: nodeId 1:1605
Library Main Content Grid: nodeId 1:1339
```

## Confirmed Nodes

### Confirmed Screens

```text
Screen/Mixer
nodeId: 1:3
size: 390 x 1112
```

```text
Screen/Home
nodeId: 1:153
size: 390 x 1164
```

```text
Screen/Settings
nodeId: 1:1605
size: 390 x 884
```

### Confirmed Library Content

```text
Main Content Grid
nodeId: 1:1339
size: 390 x 1194
```

This node contains Library-style sound browsing content:

- Popular for Sleep
- Colors of Noise
- Around the Home
- Component/SoundItem
- Component/SoundItem: Active
- Add / Remove button states

### Confirmed Component-Like Structures

Observed in metadata:

- BottomNavBar
- Fixed Mini Player
- Persistent Mini Player
- Component/SoundItem
- Component/SoundItem: Active
- Mix Card 3
- Input - Volume slider
- Input - Master Volume
- Input - Volume for Heavy Rain
- Input - Volume for Brown Noise
- Input - Volume for Campfire
- Button - Pause
- Button - Mute
- Button - Remove
- Button - Favorite
- Add Sound Button

These are component-like, but the audit did not confirm whether they are actual Figma `COMPONENT` / `INSTANCE` objects or regular frames.

## Screenshot Results

### Screen/Mixer

```text
nodeId: 1:3
original: 390 x 1112
rendered: 1 x 1
```

Result: Failed render.

This likely indicates a Figma rendering issue for this screen, possibly caused by parent frame clipping, visibility, placement, opacity, masks, or plugin export incompatibility.

### Screen/Home

```text
nodeId: 1:153
original: 390 x 1164
rendered: 1 x 1
```

Result: Failed render.

Same risk as Mixer. This screen should be fixed before using it as a code generation source.

### Screen/Settings

```text
nodeId: 1:1605
rendered: 430 x 884
```

Result: Passed render.

Note: Metadata reports `Screen/Settings` as 390 x 884, but screenshot rendered at 430 x 884. The internal Header node is also 430px wide. This should be normalized.

### Library Main Content Grid

```text
nodeId: 1:1339
original: 390 x 1194
rendered: 335 x 1024
```

Result: Passed render.

## Token / Variable Status

`get_variable_defs` on node `1:1339` returned:

```json
{}
```

`get_variable_defs` on page `0:1` failed because no layer was selected.

Current status: Insufficient tokenization.

The file has subscribed design libraries, including:

- Material 3 Design Kit
- Simple Design System

However, this audit did not confirm local app-specific color variables, text styles, or semantic design tokens applied to the white noise app screens.

## Generic Layer Naming

Generic names are heavily present:

```text
Container
Background
Text
Button
Margin
Section
Main
Nav
Header
Input
Frame 1
```

Impact:

- Figma MCP code generation will produce low-semantic component trees.
- React Native / Flutter implementation will require more manual cleanup.
- Mapping Figma nodes to code components will be harder.

## Rasterization / Asset Risk

Text appears to be real Figma text nodes in the sampled metadata.

Examples:

```text
Popular for Sleep
Deep Sleep Sanctuary
Deep Brown Noise
Ocean Sleep
PREFERENCES
PLAYBACK
ABOUT
```

Risk:

- Several icons in design context were emitted as image asset URLs.
- Some icon nodes are vectors in metadata, but Library context exported icons as `img` constants.
- This may generate code with remote image assets instead of reusable icon components.

Recommendation:

Use a consistent icon component library for implementation, such as Lucide, Material Symbols, or platform-native icons, rather than depending on exported Figma image assets.

## MVP Coverage Status

| Item | Status | Notes |
|---|---|---|
| Home | Present, render issue | `Screen/Home` exists but screenshot returns 1 x 1 |
| Mixer | Present, render issue | `Screen/Mixer` exists but screenshot returns 1 x 1 |
| Library | Partial confirmed | `Main Content Grid` exists and renders; top-level `Screen/Library` not confirmed from untruncated metadata |
| Timer | Not confirmed | Need direct node or clean top-level screen name |
| Saved Mixes | Partial evidence | Mix cards exist; top-level `Screen/Saved Mixes` not confirmed from untruncated metadata |
| Settings | Present and renders | Width inconsistency: metadata 390, header/render 430 |
| Bottom Nav | Present as frame | Actual component status not confirmed |
| Mini Player | Present as frame | Actual component status not confirmed |
| Sound Item | Present as named component-like frame | Actual component status not confirmed |
| Mix Card | Present | At least `Mix Card 3` found |
| Slider | Present | Multiple slider-like inputs found |
| Tokens | Not ready | No app-specific variable definitions confirmed |
| Code generation readiness | Medium risk | Needs cleanup before RN / Flutter generation |

## Recommended Figma Cleanup

Before using Figma MCP for implementation, fix these items:

1. Normalize top-level screen names:

```text
Screen/Home
Screen/Mixer
Screen/Library
Screen/Timer
Screen/Saved Mixes
Screen/Settings
```

2. Ensure all six screen frames render correctly through Figma export.

Critical current failures:

```text
Screen/Home -> renders 1 x 1
Screen/Mixer -> renders 1 x 1
```

3. Convert reusable structures into real Figma components:

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

4. Replace generic layer names with semantic names.

Examples:

```text
Container -> Sound Item Content
Background -> Sound Icon Background
Text -> Sound Name
Button -> Add Sound Button
Input - Volume slider -> Master Volume Slider
```

5. Create app-specific semantic tokens:

```text
Color/Background
Color/Surface
Color/Surface Elevated
Color/Text Primary
Color/Text Muted
Color/Accent
Color/Border
Color/Danger
Text/Screen Title
Text/Section Title
Text/Body
Text/Caption
Radius/Card
Radius/Button
Spacing/Screen Padding
```

6. Normalize screen widths.

Settings currently mixes 390px and 430px widths:

```text
Screen/Settings: 390 x 884
Header inside Settings: 430 x 64
Rendered screenshot: 430 x 884
```

7. Avoid exported image icons for implementation.

Implementation should map Figma icon intent to code-native icons instead of preserving remote image assets.

## Current Readiness

Current status:

```text
Not ready for direct automated implementation.
Ready for manual review and Figma cleanup.
```

Recommended next step:

```text
Clean Figma screen names, rendering behavior, componentization, and tokens. Then rerun Figma MCP audit before starting OpenSpec implementation.
```
