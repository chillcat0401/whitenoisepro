# early-user-feedback Specification

## Purpose
TBD - created by archiving change fast-google-play-closed-testing-launch. Update Purpose after archive.
## Requirements
### Requirement: Closed testing tester roster

The project SHALL maintain a tester roster for Google Play closed testing.

#### Scenario: Tester group is prepared

- **WHEN** the release owner prepares closed testing
- **THEN** the roster MUST track tester identifier, Google account or group membership status, opt-in date, active status, covered device category, covered test path, main feedback, and follow-up action
- **AND** the roster SHOULD target 15-20 invited testers when a 12 tester / 14 day Play Console requirement applies

#### Scenario: Tester continuity is reviewed

- **WHEN** closed testing is in progress
- **THEN** the release owner MUST review tester opt-in continuity and activity at Day 2-3, Day 7, and Day 12-14
- **AND** inactive or missing testers MUST be recorded as production access risk

### Requirement: Structured original user feedback

The project SHALL collect structured original feedback during closed testing.

#### Scenario: Tester receives instructions

- **WHEN** a tester joins the closed test
- **THEN** the tester instructions MUST ask them to exercise default playback, Library, Mixer, 30 minute timer, background playback, lock-screen or media controls, Settings trust copy, and at least one earphone or speaker scenario when available
- **AND** the instructions MUST ask about confusion, sound comfort, loop artifacts, background stability, timer discoverability, willingness to reuse before sleep, and privacy/permission trust

#### Scenario: Feedback is triaged

- **WHEN** tester feedback is received
- **THEN** it MUST be categorized as P0, P1, P2, or P3
- **AND** P0/P1 issues MUST create either a fix task or an explicit no-fix rationale before production access application

### Requirement: Production access evidence

The project SHALL preserve production access application evidence from closed testing.

#### Scenario: Production access application is prepared

- **WHEN** the release owner prepares the Google Play production access application
- **THEN** the evidence MUST summarize tester recruitment method, tester count, tester activity, tested core flows, main feedback themes, fixes made from feedback, known unfixed risks, and why the app is ready for production
- **AND** the evidence MUST not claim feedback or fixes that are not recorded in the tester feedback tracker

