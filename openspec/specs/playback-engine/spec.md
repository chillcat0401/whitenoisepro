# playback-engine Specification

## Purpose

定义跨平台播放抽象以及 Android 多层循环播放、后台媒体会话和音量控制的职责边界。commonMain 只依赖稳定的播放接口，平台实现负责媒体框架、系统控制、资源释放和真实输出状态。
## Requirements
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

The system SHALL support more than one active sound layer in a mix without adding audible controller-only media.

#### Scenario: Three published layers are active

- **WHEN** the current mix contains brown noise, pink noise, and fan layers
- **THEN** all unmuted layers contribute to output with their effective volumes
- **AND** the MediaSession controller player does not add another audible layer

### Requirement: Audio lifecycle safety
The system SHALL release or suspend platform audio resources when playback stops and no timer or resume state requires them.

#### Scenario: User stops playback
- **WHEN** playback is stopped
- **THEN** the platform engine releases or suspends resources according to the platform lifecycle policy

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

