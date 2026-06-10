## ADDED Requirements

### Requirement: Light rain candidate profile

The internal audio asset studio SHALL support a distinct `light-rain` profile for small rain or distant rain candidates.

#### Scenario: Operator generates light rain candidates

- **WHEN** an operator runs the audio asset studio with profile `light-rain`
- **THEN** the tool MUST generate candidate WAV files, `manifest.json`, and `audition.html`
- **AND** the manifest MUST record profile `light-rain`, seeds, hashes, QA metrics, `status: candidate`, and `publishable: false`
- **AND** generated files MUST remain outside Android release resources

#### Scenario: Light rain is compared with rain

- **WHEN** `light-rain` and `rain` candidates are generated with the same QA options
- **THEN** `light-rain` MUST satisfy a lower roughness proxy than the existing `rain` profile
- **AND** it MUST continue to pass RMS, peak, WAV format, and loop boundary QA
