# audio-asset-studio Specification

## Purpose

定义内部音频素材运营工具的边界：用于本地生成、试听和记录第一方程序化音频候选素材，但不得绕过发布音频准入流程。

## Requirements

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

- **WHEN** a generated candidate fails RMS, peak, WAV format, hash, or loop boundary QA
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

### Requirement: External audio source intake

The project SHALL maintain an external audio intake process for seed recordings when internal procedural generation does not meet product quality.

#### Scenario: External seed candidate is selected

- **WHEN** an external audio file is considered as a seed candidate
- **THEN** the intake record MUST include source URL, source channel, author or provider, license, commercial-use status, download status, original filename, and intended app category
- **AND** the candidate MUST remain outside Android release resources until a future release intake change promotes it

#### Scenario: Source license is not release-safe

- **WHEN** a source is non-commercial, unclear, or requires a paid commercial license
- **THEN** the candidate MUST be marked blocked for release until license evidence is supplied
