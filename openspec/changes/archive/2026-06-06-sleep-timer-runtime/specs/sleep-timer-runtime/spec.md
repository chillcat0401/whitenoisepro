# sleep-timer-runtime Spec Delta

## ADDED Requirements

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

