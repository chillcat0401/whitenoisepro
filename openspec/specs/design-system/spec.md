# design-system Specification

## Purpose

定义与中文 Stitch/Figma 视觉基准一致的夜间友好主题、设计 token 和可复用 Compose 组件。

## Requirements
### Requirement: Night-friendly dark theme
The system SHALL use a low-brightness dark theme based on the Chinese Stitch/Figma visual baseline.

#### Scenario: App launches at night
- **WHEN** the app launches
- **THEN** the default visual theme uses dark background, muted surfaces, restrained teal accent, and no large white content areas

### Requirement: Semantic design tokens
The system SHALL define semantic tokens for color, typography, spacing, radius, and touch target sizing in code.

#### Scenario: Component requests a surface color
- **WHEN** a UI component needs a surface color
- **THEN** it references a semantic app token rather than a raw color literal inside the component implementation

### Requirement: Figma/Stitch visual alignment
The system SHALL treat the Chinese Figma and Stitch export as visual guidance for MVP screens.

#### Scenario: Developer implements a screen
- **WHEN** a developer builds Home, Mixer, Library, Timer, Saved Mixes, or Settings
- **THEN** the screen preserves the layout density, dark palette, fixed Mini Player pattern, and large touch targets from the visual baseline

### Requirement: Touch-safe controls
The system SHALL provide interactive controls with a minimum 44px touch target, preferring 48px for primary controls.

#### Scenario: User interacts while drowsy
- **WHEN** the user taps play, pause, add, remove, favorite, timer, or bottom navigation controls
- **THEN** each control provides a touch target of at least 44px in both dimensions
