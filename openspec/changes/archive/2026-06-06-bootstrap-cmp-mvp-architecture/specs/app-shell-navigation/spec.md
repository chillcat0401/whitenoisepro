## ADDED Requirements

### Requirement: MVP screen shell
The system SHALL provide a mobile app shell containing Home, Mixer, Library, Timer, Saved Mixes, and Settings screens.

#### Scenario: User switches primary screens
- **WHEN** the user taps a bottom navigation item
- **THEN** the system displays the corresponding screen without losing current playback, timer, or mix state

#### Scenario: User opens settings
- **WHEN** the user taps the settings icon from a primary screen
- **THEN** the system displays Settings while preserving the current playback and Mini Player state

### Requirement: Persistent Mini Player
The system SHALL display a persistent Mini Player above the bottom navigation whenever a mix is active or recently paused.

#### Scenario: Active playback
- **WHEN** playback is active
- **THEN** the Mini Player displays the current mix name, timer state if present, and a pause control

#### Scenario: Paused playback
- **WHEN** playback is paused but a current mix exists
- **THEN** the Mini Player displays the current mix name and a play control

### Requirement: Safe mobile layout
The system SHALL keep fixed navigation and Mini Player controls from overlapping scrollable content on 360x800, 390x844, and 430x932 viewports.

#### Scenario: Scrollable content reaches bottom
- **WHEN** the user scrolls to the end of a long screen
- **THEN** the last content item remains visible above the Mini Player and bottom navigation

#### Scenario: Narrow device rendering
- **WHEN** the app renders at 360px width
- **THEN** no navigation label, Mini Player label, or primary button text overflows its container
