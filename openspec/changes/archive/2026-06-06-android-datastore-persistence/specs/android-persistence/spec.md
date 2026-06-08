# android-persistence Spec Delta

## ADDED Requirements

### Requirement: Coroutine-based Android persistence

The Android app SHALL persist app snapshots with AndroidX DataStore using coroutine and Flow APIs.

#### Scenario: App state is saved

- **GIVEN** a meaningful app state change occurs
- **WHEN** AppStore requests persistence
- **THEN** the repository saves the snapshot through a suspend function
- **AND** DataStore performs an atomic update

#### Scenario: App state is restored

- **GIVEN** a persisted snapshot exists
- **WHEN** the app starts
- **THEN** AppStore collects the repository snapshot Flow
- **AND** restores state without blocking the main thread

### Requirement: Versioned persistence schema

The persisted snapshot SHALL use an explicit schema version and tolerate additive JSON fields.

#### Scenario: Stored JSON contains a future additive field

- **WHEN** the current codec reads the JSON
- **THEN** known version-one fields are restored
- **AND** unknown fields are ignored

#### Scenario: Stored JSON is invalid or uses an unsupported version

- **WHEN** the codec reads the stored value
- **THEN** it returns no snapshot
- **AND** the app starts with default state instead of crashing

