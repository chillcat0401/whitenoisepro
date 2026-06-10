## 1. Tests First

- [x] 1.1 Add tests proving `light-rain` is a supported profile and generates deterministic QA-valid WAV.
- [x] 1.2 Add a regression test that `light-rain` has lower roughness than `rain`.

## 2. Synthesis Implementation

- [x] 2.1 Add `light-rain` to supported profiles.
- [x] 2.2 Implement the `light-rain` spectral model with lighter rain-bed and softer scattered drops.

## 3. Candidate Output

- [x] 3.1 Generate `work/audio-candidates/light-rain-2026-06-09/`.
- [x] 3.2 Confirm generated candidates are ignored by git and remain outside release resources.

## 4. Verification

- [x] 4.1 Run targeted audio tool tests.
- [x] 4.2 Run `node tools/generate_mvp_audio.mjs --verify`.
- [x] 4.3 Run OpenSpec strict validation for `add-light-rain-audio-candidates`.
