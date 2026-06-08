# sleep-timer-runtime Specification

## Purpose

定义睡眠定时器在应用运行期间的倒计时、淡出、延长和到点停止行为，确保 UI 状态、播放引擎和持久化边界保持一致。
## Requirements
### Requirement: Running sleep timer

The app SHALL advance an active sleep timer independently of the currently visible screen.

#### Scenario: Timer enters fade window

- **WHEN** remaining time is less than fade duration
- **THEN** playback fade factor decreases proportionally

#### Scenario: Timer completes

- **WHEN** remaining time reaches zero
- **THEN** playback stops for StopPlayback behavior
- **AND** UI playback state becomes stopped

#### Scenario: Active timer is extended

- **WHEN** the user extends an active timer
- **THEN** the full extension remains after the next tick

### Requirement: Android service-backed timer

The Android app SHALL keep the active sleep-timer deadline in a lifecycle owned by the media playback service while the app process and service remain alive.

#### Scenario: Activity is destroyed during an active timer

- **WHEN** the Activity or Compose scope is destroyed while MediaSessionService remains alive
- **THEN** the service-backed timer continues applying fade
- **AND** StopPlayback stops playback at the deadline

#### Scenario: Timer is rescheduled

- **WHEN** the user extends or restarts an active timer
- **THEN** the previous platform timer job is cancelled
- **AND** the latest absolute deadline replaces it

#### Scenario: Timer is cancelled

- **WHEN** the user cancels or replaces the timer preset
- **THEN** the platform timer job is cancelled
- **AND** the timer fade factor returns to full volume

