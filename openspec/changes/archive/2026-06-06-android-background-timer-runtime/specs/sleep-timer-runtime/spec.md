# sleep-timer-runtime Spec Delta

## ADDED Requirements

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
