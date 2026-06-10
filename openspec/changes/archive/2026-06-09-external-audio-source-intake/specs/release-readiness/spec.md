## ADDED Requirements

### Requirement: External audio release evidence

The project SHALL keep release evidence for any external audio before it is bundled in an app build.

#### Scenario: External audio is promoted

- **WHEN** an external audio candidate is promoted into Android release resources
- **THEN** release-readiness docs MUST include its source URL, license evidence, original hash, processed hash, processing steps, human listening QA, loop QA, loudness QA, and package-size impact

#### Scenario: BBC Sound Effects candidate is proposed

- **WHEN** a BBC Sound Effects recording is proposed for release
- **THEN** it MUST be rejected unless a paid commercial license from the authorized licensing path is recorded
