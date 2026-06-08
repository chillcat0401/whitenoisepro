# playback-engine Spec Delta

## MODIFIED Requirements

### Requirement: Multi-layer playback

The system SHALL support more than one active sound layer in a mix without adding audible controller-only media.

#### Scenario: Three published layers are active

- **WHEN** the current mix contains brown noise, pink noise, and fan layers
- **THEN** all unmuted layers contribute to output with their effective volumes
- **AND** the MediaSession controller player does not add another audible layer
