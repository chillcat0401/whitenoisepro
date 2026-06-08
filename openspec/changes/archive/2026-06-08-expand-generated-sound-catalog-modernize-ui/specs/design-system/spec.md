# design-system Spec Delta

## MODIFIED Requirements

### Requirement: Night-friendly dark theme

The system SHALL use a low-brightness dark theme based on the Chinese Stitch/Figma visual baseline.

#### Scenario: App launches at night

- **WHEN** the app launches
- **THEN** the default visual theme uses dark background, muted surfaces, restrained teal accent, secondary cool accent, warm tertiary accent, and no large white content areas

### Requirement: Semantic design tokens

The system SHALL define semantic tokens for color, typography, spacing, radius, icon, and touch target sizing in code.

#### Scenario: Component requests a surface color

- **WHEN** a UI component needs a surface color, icon tint, border, or active control color
- **THEN** it references a semantic app token rather than a raw color literal inside the component implementation

## ADDED Requirements

### Requirement: Local icon system

The system SHALL provide local Compose-rendered icons for primary app actions, navigation, and sound categories.

#### Scenario: User scans controls while drowsy

- **WHEN** the user views Home, Mixer, Library, Saved Mixes, or the Mini Player
- **THEN** play, pause, settings, timer, edit, delete, favorite, mute, add, navigation, and sound-type controls are represented by icons rather than single-character text placeholders

#### Scenario: Device is offline

- **WHEN** the user opens the app without network access
- **THEN** all UI icons render from local code or bundled resources

