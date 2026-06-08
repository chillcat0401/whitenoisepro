# Signed Release Bundle Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Generate and verify a Google Play upload-ready signed AAB while keeping upload-key secrets out of source control.

**Architecture:** Gradle reads a complete signing tuple from environment variables or a local properties file and only requires it for release-producing tasks. A Node CLI validates AAB structure and invokes `jarsigner` for cryptographic verification.

**Tech Stack:** Kotlin Gradle DSL, Android Gradle Plugin, JDK keytool/jarsigner, Node.js test runner

---

### Task 1: Secret boundary

**Files:**
- Modify: `.gitignore`
- Create: `keystore.properties.example`

- [ ] Ignore `keystore.properties`, `*.jks`, and `*.keystore`.
- [ ] Add a template containing only placeholder property names.
- [ ] Verify ignore patterns with `git check-ignore` when the project is under Git; otherwise inspect patterns directly.

### Task 2: Bundle verifier

**Files:**
- Create: `tools/verify_release_bundle.test.mjs`
- Create: `tools/verify_release_bundle.mjs`

- [ ] Write a test that expects an invalid AAB to be rejected.
- [ ] Run `node --test tools/verify_release_bundle.test.mjs` and confirm RED because the verifier is absent.
- [ ] Implement ZIP entry parsing and strict `jarsigner` invocation.
- [ ] Re-run the test and confirm GREEN.

### Task 3: Gradle signing

**Files:**
- Modify: `composeApp/build.gradle.kts`

- [ ] Load environment variables first and local properties second.
- [ ] Reject partial signing tuples.
- [ ] Require a complete tuple for release-producing tasks.
- [ ] Attach the signing config to the release build type.
- [ ] Confirm debug `check` still runs without release inputs.

### Task 4: Local upload artifact

**Files:**
- Create locally and ignore: `keystore.properties`
- Create locally and ignore: `upload-keystore.jks`
- Generate: `composeApp/build/outputs/bundle/release/composeApp-release.aab`

- [ ] Generate strong random local passwords without printing them.
- [ ] Create a 4096-bit RSA upload key with a long validity.
- [ ] Run `:composeApp:bundleRelease`.
- [ ] Run the Node verifier and `jarsigner -verify`.

### Task 5: Release evidence

**Files:**
- Modify: `docs/release-readiness/google-play-checklist.md`
- Create: `docs/release-readiness/android-release-signing.md`
- Create: `docs/code-review-signed-release-bundle-2026-06-07.md`
- Modify: `openspec/changes/signed-release-bundle/tasks.md`

- [ ] Document key backup, rotation, Play App Signing, and build commands without secrets.
- [ ] Run full `:composeApp:check`, lint, debug APK, and release AAB verification.
- [ ] Strict-validate and archive the OpenSpec change.
