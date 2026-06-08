# app-state-interaction Spec Delta

## ADDED Requirements

### Requirement: App-level state orchestration

The app SHALL route user-facing state changes through a commonMain app-level state orchestrator instead of mutating `AppState` directly inside screen composables.

#### Scenario: UI sends an app intent

- **GIVEN** a user changes a core app control
- **WHEN** the UI handles the event
- **THEN** it sends a typed app-level intent
- **AND** the state orchestrator updates `AppState`
- **AND** the screen renders from the updated state

### Requirement: Restore persisted app snapshot

The app SHALL restore persisted mixes, timer defaults, and settings from `AppRepository` on startup when available.

#### Scenario: Stored snapshot exists

- **GIVEN** the repository contains a saved app snapshot
- **WHEN** the app state orchestrator initializes
- **THEN** saved mixes, recent mixes, timer defaults, and settings are restored
- **AND** current mix is restored only when `startLastMix` is enabled
- **AND** playback does not automatically start

#### Scenario: Stored snapshot is missing

- **GIVEN** the repository has no saved app snapshot
- **WHEN** the app state orchestrator initializes
- **THEN** the app uses default MVP sample content
- **AND** the app remains on Home
- **AND** playback is stopped

### Requirement: Persist meaningful user changes

The app SHALL persist meaningful mix, timer, and settings changes after the state update succeeds.

#### Scenario: User saves or edits a mix

- **GIVEN** the user changes current mix content, volume, favorite state, or saved mixes
- **WHEN** the state orchestrator applies the intent
- **THEN** the repository receives an updated app snapshot

#### Scenario: User changes temporary browsing state

- **GIVEN** the user changes library search text or selected category
- **WHEN** the state orchestrator applies the intent
- **THEN** the repository is not written

### Requirement: Interactive MVP pages

The MVP pages SHALL connect their primary controls to real app behavior or show them as read-only, disabled, or hidden.

#### Scenario: User operates Library

- **GIVEN** the user is on Library
- **WHEN** the user searches, selects a category, or taps a sound
- **THEN** the visible sound list or current mix updates accordingly

#### Scenario: User operates Timer

- **GIVEN** the user is on Timer
- **WHEN** the user chooses a preset, starts, extends, or cancels the timer
- **THEN** timer state updates accordingly
- **AND** the updated timer defaults are persisted

#### Scenario: User operates Settings

- **GIVEN** the user is on Settings
- **WHEN** the user toggles start-last-mix
- **THEN** user settings update
- **AND** the updated settings are persisted

