## ADDED Requirements

### Requirement: Candidate audio generation

The project SHALL provide an internal local tool that generates first-party procedural audio candidates without adding them to the release app package.

#### Scenario: Operator generates candidate sounds

- **WHEN** an operator runs the audio asset studio with a profile, seed, count, prefix, and output directory
- **THEN** the tool MUST write candidate WAV files to the requested output directory
- **AND** the tool MUST NOT modify Android `res/raw`, the app sound catalog, or published release manifests

#### Scenario: Unsupported profile is requested

- **WHEN** an operator requests an unsupported audio profile
- **THEN** the tool MUST fail with an actionable error listing supported profiles

### Requirement: Candidate manifest and QA evidence

The internal audio asset studio SHALL record reproducible metadata and QA evidence for every generated candidate.

#### Scenario: Candidate manifest is created

- **WHEN** candidate sounds are generated
- **THEN** the output directory MUST contain `manifest.json`
- **AND** the manifest MUST include schema version, generation date, ownership, parameters, profile, seed, file names, SHA-256 hashes, byte sizes, QA metrics, `status: candidate`, and `publishable: false`

#### Scenario: Generated candidate fails QA

- **WHEN** a generated candidate fails RMS, peak, WAV format, hash, or loop seam QA
- **THEN** the tool MUST fail the run rather than writing a publishable candidate record

### Requirement: Local audition page

The internal audio asset studio SHALL produce a local HTML audition page for generated candidates.

#### Scenario: Audition page is created

- **WHEN** candidate sounds are generated
- **THEN** the output directory MUST contain `audition.html`
- **AND** the page MUST list each candidate with profile, seed, QA summary, status, and a browser-native audio control

### Requirement: Published audio generation compatibility

The existing MVP audio generation workflow SHALL continue to generate and verify the current published audio assets.

#### Scenario: Published generator is verified

- **WHEN** `node tools/generate_mvp_audio.mjs --verify` is run
- **THEN** it MUST verify all published generated audio assets against `docs/audio-assets/generated-audio-manifest.json`

