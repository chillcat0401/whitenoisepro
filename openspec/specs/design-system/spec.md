# design-system Specification

## Purpose

定义与中文 Stitch/Figma 视觉基准一致的夜间友好主题、设计 token、舒适布局节奏、本地图标系统和可复用 Compose 组件。

## Requirements
### Requirement: Night-friendly dark theme
The system SHALL use a low-brightness dark theme based on the Chinese Stitch/Figma visual baseline.

#### Scenario: App launches at night
- **WHEN** the app launches
- **THEN** the default visual theme uses dark background, muted surfaces, restrained teal accent, secondary cool accent, warm tertiary accent, and no large white content areas

### Requirement: Semantic design tokens
The system SHALL define semantic tokens for color, typography, spacing, radius, icon, comfort rhythm, and touch target sizing in code.

#### Scenario: Component requests a surface color
- **WHEN** a UI component needs a surface color, icon tint, border, or active control color
- **THEN** it references a semantic app token rather than a raw color literal inside the component implementation

#### Scenario: Component requests layout spacing
- **WHEN** a screen, card, hero area, mini player, or bottom navigation needs spacing
- **THEN** it references semantic comfort tokens rather than scattering raw padding values

### Requirement: Figma/Stitch visual alignment
The system SHALL treat the Chinese Figma and Stitch export as visual guidance for MVP screens while prioritizing a relaxed sleep-use rhythm.

#### Scenario: Developer implements a screen
- **WHEN** a developer builds Home, Mixer, Library, Timer, Saved Mixes, or Settings
- **THEN** the screen preserves dark palette, fixed Mini Player pattern, large touch targets, and enough breathing room for sleep-time scanning

### Requirement: Touch-safe controls
The system SHALL provide interactive controls with a minimum 44px touch target, preferring 48px for primary controls.

#### Scenario: User interacts while drowsy
- **WHEN** the user taps play, pause, add, remove, favorite, timer, or bottom navigation controls
- **THEN** each control provides a touch target of at least 44px in both dimensions

### Requirement: Local icon system
The system SHALL provide local Compose-rendered icons for primary app actions, navigation, and sound categories.

#### Scenario: User scans controls while drowsy
- **WHEN** the user views Home, Mixer, Library, Saved Mixes, or the Mini Player
- **THEN** play, pause, settings, timer, edit, delete, favorite, mute, add, navigation, and sound-type controls are represented by icons rather than single-character text placeholders

#### Scenario: Device is offline
- **WHEN** the user opens the app without network access
- **THEN** all UI icons render from local code or bundled resources

### Requirement: Relaxed sleep layout rhythm
The system SHALL provide a less dense layout rhythm for bedtime use.

#### Scenario: User opens the app at bedtime
- **WHEN** the app renders primary screens
- **THEN** screen horizontal padding is at least 20dp
- **AND** top content padding is at least 24dp
- **AND** Mini Player height is at least 72dp
- **AND** list content has bottom padding that accounts for Mini Player, bottom navigation, and extra breathing room

#### Scenario: User scans sound library
- **WHEN** Library shows sound cards
- **THEN** each sound card uses a relaxed card ratio and padding sufficient to separate icon, title, and metadata
