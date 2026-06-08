# playback-engine Spec Delta

## ADDED Requirements

### Requirement: Audio-focus-gated audible output

The Android implementation SHALL keep all audible layer players paused until the MediaSession
controller player is actually playing without playback suppression.

#### Scenario: Focus is pending or denied

- **WHEN** the controller has playWhenReady set but is not actually playing
- **THEN** all audible layer players remain paused
- **AND** the playback engine reports Buffering rather than Playing

#### Scenario: Focus is authorized

- **WHEN** the controller player becomes actually playing
- **THEN** the engine starts only the current unmuted layers
- **AND** the playback engine reports Playing

#### Scenario: User explicitly pauses or stops

- **WHEN** the controller no longer has playWhenReady or the engine is stopped
- **THEN** all layer players pause or stop
- **AND** delayed controller callbacks do not restart audible output

### Requirement: Authoritative playback presentation state

The common presentation layer SHALL derive visible playing state from PlaybackEngine state rather
than optimistically assuming a play command succeeded.

#### Scenario: Android rejects or delays playback authorization

- **WHEN** a play command is issued but the Android controller does not become actually playing
- **THEN** the application does not present playback as active
