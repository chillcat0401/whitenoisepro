# playback-engine Spec Delta

## ADDED Requirements

### Requirement: Observable platform playback state

The system SHALL expose platform playback state as a coroutine StateFlow so common presentation
state remains synchronized with application and system media controls.

#### Scenario: System media control pauses playback

- **WHEN** a notification, lock-screen control, audio-focus event, or becoming-noisy event pauses the Android player
- **THEN** the playback engine emits a Paused state
- **AND** the common AppStore updates its visible playing state without requiring an app restart

#### Scenario: System media control resumes playback

- **WHEN** the user explicitly resumes playback through a system media control
- **THEN** the playback engine emits a Playing state
- **AND** the common AppStore updates its visible playing state

### Requirement: Android audio interruption handling

The Android implementation SHALL designate one MediaSession controller player to manage audio
focus and audio-becoming-noisy events while layer players remain the audible mix output.

#### Scenario: Audio focus is lost

- **WHEN** another application or system event suppresses or pauses the controller player
- **THEN** all active audible layers pause
- **AND** the common AppStore reflects the controller player's actual playing state
- **AND** transient focus recovery follows Media3 audio-focus policy

#### Scenario: Headphones disconnect

- **WHEN** Android reports that audio output is becoming noisy
- **THEN** the controller player pauses
- **AND** all active audible layers pause
- **AND** playback remains paused until an explicit play intent

#### Scenario: Multiple layers play

- **WHEN** more than one sound layer is active
- **THEN** layer players use media audio attributes
- **AND** they do not independently compete for audio focus
