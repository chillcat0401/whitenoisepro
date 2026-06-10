## ADDED Requirements

### Requirement: Rain candidates avoid narrow shower-like texture

The internal audio asset studio SHALL tune generated rain candidates toward broad, natural rain texture rather than narrow, high-pressure water-stream texture.

#### Scenario: Rain candidate is generated

- **WHEN** the operator generates candidates with profile `rain`
- **THEN** each candidate MUST continue to pass RMS, peak, WAV format, hash, and loop boundary QA
- **AND** the generated rain waveform MUST satisfy a repeatable roughness proxy that limits excessive high-frequency adjacent-sample jumps
- **AND** the generated files MUST remain `status: candidate` and `publishable: false`

#### Scenario: Tuned rain candidates are reviewed

- **WHEN** a tuned rain candidate directory is created
- **THEN** the output MUST include `manifest.json` and `audition.html`
- **AND** the candidate directory MUST remain outside Android release resources until a future release intake change promotes it
