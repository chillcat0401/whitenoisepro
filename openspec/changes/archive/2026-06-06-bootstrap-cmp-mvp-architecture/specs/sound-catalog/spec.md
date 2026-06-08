## ADDED Requirements

### Requirement: Local sound catalog
The system SHALL provide a local catalog of sound metadata grouped by sleep-relevant categories.

#### Scenario: User opens Library
- **WHEN** the user opens Library
- **THEN** the system displays sounds grouped or filterable by categories such as Sleep, Nature, Noise, Home, Focus, and Baby

### Requirement: Sound metadata
Each sound SHALL include stable id, localized display name, category, icon intent, loop asset reference, and default volume.

#### Scenario: UI renders a sound item
- **WHEN** a sound appears in Library or Mixer
- **THEN** the UI can read its display name, icon intent, category, active state, and volume defaults from catalog data

### Requirement: Offline-first audio assets
The MVP SHALL assume bundled or locally available loopable sound assets for core sounds.

#### Scenario: Device is offline
- **WHEN** the user opens the MVP without network access
- **THEN** the core catalog and bundled sounds remain usable

### Requirement: Catalog search and filtering
The system SHALL support text search and category filtering over local sound metadata.

#### Scenario: User searches white noise
- **WHEN** the user enters a search term matching sound name or category
- **THEN** Library displays matching sounds and preserves active mix indicators
