## 1. Tests First

- [x] 1.1 Add a rain texture regression test that fails against the current shower-like high-frequency roughness.
- [x] 1.2 Keep existing deterministic WAV, QA, invalid profile, and duration tests passing.

## 2. Rain Synthesis Tuning

- [x] 2.1 Adjust the rain spectral model to reduce narrow high-frequency water-stream texture and add broader rain-bed energy.
- [x] 2.2 Preserve existing WAV format, RMS, peak, loop boundary, hash, and deterministic generation behavior.

## 3. Candidate Output

- [x] 3.1 Generate a new rain candidate set under `work/audio-candidates/rain-natural-2026-06-09/`.
- [x] 3.2 Confirm generated candidates remain outside release resources and are ignored by git.

## 4. Verification

- [x] 4.1 Run targeted Node tests for the audio synthesis core and audio asset studio.
- [x] 4.2 Run `node tools/generate_mvp_audio.mjs --verify`.
- [x] 4.3 Run OpenSpec strict validation for `tune-rain-audio-candidates`.
