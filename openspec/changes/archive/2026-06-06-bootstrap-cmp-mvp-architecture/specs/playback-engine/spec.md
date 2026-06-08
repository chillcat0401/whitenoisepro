## ADDED Requirements

### Requirement: Platform playback abstraction
The system SHALL expose playback through a common PlaybackEngine interface while keeping platform-specific media APIs outside common domain logic.

#### Scenario: Common UI toggles playback
- **WHEN** common UI sends a play, pause, stop, or volume command
- **THEN** it calls the common playback boundary rather than directly using Android APIs

### Requirement: Android Media3 playback implementation
The Android implementation SHALL use AndroidX Media3 concepts for playback and background control unless an implementation spike proves a better fit.

#### Scenario: Android playback starts
- **WHEN** the user starts playback on Android
- **THEN** the Android playback implementation prepares loopable media items and exposes playback through a media session

### Requirement: Background playback
The Android MVP SHALL support playback continuing while the app is backgrounded or the screen is locked.

#### Scenario: App goes to background during playback
- **WHEN** the user backgrounds the app while playback is active
- **THEN** playback continues and remains controllable through system media controls where supported

### Requirement: Multi-layer playback
The system SHALL support more than one active sound layer in a mix.

#### Scenario: Three layers are active
- **WHEN** the current mix contains rain, brown noise, and fireplace layers
- **THEN** all unmuted layers contribute to output with their effective volumes

### Requirement: Audio lifecycle safety
The system SHALL release or suspend platform audio resources when playback stops and no timer or resume state requires them.

#### Scenario: User stops playback
- **WHEN** playback is stopped
- **THEN** the platform engine releases or suspends resources according to the platform lifecycle policy
