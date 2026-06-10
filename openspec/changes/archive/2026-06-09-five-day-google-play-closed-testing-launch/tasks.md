## 1. Planning and Intake Boundaries

- [x] 1.1 Archive `external-audio-source-intake` and verify archive acceptance.
- [x] 1.2 Validate this change with OpenSpec strict mode before implementation.

## 2. External Audio Promotion

- [x] 2.1 Add tests for external release audio manifest validation and sound id coverage.
- [x] 2.2 Implement a repeatable audio promotion script that processes 11 Freesound originals into Android release resources.
- [x] 2.3 Generate processed resources and `docs/audio-assets/external-release-audio-manifest.json`.
- [x] 2.4 Update release audio QA docs with source, hash, processing, loudness, loop, and package-size evidence.

## 3. App Catalog and Playback Resources

- [x] 3.1 Add catalog tests for 19 published sound ids, categories, names, and default volumes.
- [x] 3.2 Add Android resolver tests for dedicated resources for every published sound id.
- [x] 3.3 Update `SoundCatalog`, sample mixes, icon mapping, and Android resource resolver.

## 4. Privacy Policy and Store Readiness

- [x] 4.1 Add a GitHub Pages-ready privacy policy HTML page with current data behavior and explicit placeholders.
- [x] 4.2 Update store listing, Play Console worksheet, Data safety/release docs, and closed testing checklist for 19 bundled sounds.

## 5. Lightweight UI Polish

- [x] 5.1 Add or update design tests for relaxed visual tokens and sound icon coverage.
- [x] 5.2 Apply lightweight UI token/icon/card/spacing polish without changing navigation or core workflows.

## 6. Release Verification

- [x] 6.1 Run Node audio/tool tests and Gradle unit tests.
- [x] 6.2 Run lint, signed release bundle, release verifier, and release APK assemble.
- [x] 6.3 Run emulator release smoke and document results.
- [x] 6.4 Produce final readiness summary with remaining external Play Console blockers.
