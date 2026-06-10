## 1. Tests First

- [x] 1.1 Add Node tests for reusable synthesis core behavior and WAV / QA invariants.
- [x] 1.2 Add Node tests for the audio asset studio CLI manifest and audition HTML output.

## 2. Core Refactor

- [x] 2.1 Extract reusable synthesis, WAV, hash, profile, and QA functions into `tools/audio_synthesis_core.mjs`.
- [x] 2.2 Update `tools/generate_mvp_audio.mjs` to use the shared core while preserving current published asset verification.

## 3. Internal Candidate Tool

- [x] 3.1 Implement `tools/audio_asset_studio.mjs` CLI with profile, count, seed, prefix, duration, and output directory options.
- [x] 3.2 Generate candidate WAV files, `manifest.json`, and `audition.html` without modifying app release resources.
- [x] 3.3 Add internal documentation for candidate generation, audition, approval, and promotion boundaries.

## 4. Verification

- [x] 4.1 Run Node tests for the audio toolchain.
- [x] 4.2 Run `node tools/generate_mvp_audio.mjs --verify`.
- [x] 4.3 Run OpenSpec strict validation for `internal-audio-asset-studio`.
