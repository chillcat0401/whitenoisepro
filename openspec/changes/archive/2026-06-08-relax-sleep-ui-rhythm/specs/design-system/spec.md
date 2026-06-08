# design-system Spec Delta

## MODIFIED Requirements

### Requirement: Semantic design tokens

The system SHALL define semantic tokens for color, typography, spacing, radius, icon, comfort rhythm, and touch target sizing in code.

#### Scenario: Component requests layout spacing

- **WHEN** a screen, card, hero area, mini player, or bottom navigation needs spacing
- **THEN** it references semantic comfort tokens rather than scattering raw padding values

### Requirement: Figma/Stitch visual alignment

The system SHALL treat the Chinese Figma and Stitch export as visual guidance for MVP screens while prioritizing a relaxed sleep-use rhythm.

#### Scenario: Developer implements a screen

- **WHEN** a developer builds Home, Mixer, Library, Timer, Saved Mixes, or Settings
- **THEN** the screen preserves dark palette, fixed Mini Player pattern, large touch targets, and enough breathing room for sleep-time scanning

## ADDED Requirements

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

