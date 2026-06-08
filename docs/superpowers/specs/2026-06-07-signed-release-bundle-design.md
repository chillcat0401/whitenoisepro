# Signed Release Bundle Design

## Goal

Produce a Google Play upload-ready Android App Bundle without committing signing secrets.

## Architecture

The Android module loads four signing values from environment variables or a gitignored root `keystore.properties`. Release-producing Gradle tasks require all values and attach a dedicated upload-key signing config. Debug and unit-test workflows remain usable without signing secrets.

A small Node verifier checks the AAB ZIP structure and delegates signature verification to the JDK `jarsigner` tool. It requires the signed verification result while allowing the expected trust-chain warning from a self-signed Play upload certificate. The local upload key is operational material, not source code, and must be backed up separately.

## Error Handling

- Partial signing configuration is rejected.
- Release-producing tasks without signing data fail with the names of accepted inputs.
- The verifier rejects missing files, malformed ZIPs, missing base manifest/config entries, and invalid signatures.

## Testing

- Node test drives invalid and valid bundle-verifier behavior.
- Gradle check and debug assembly protect existing behavior.
- `bundleRelease` plus `jarsigner -verify` proves that the release artifact is signed.
- OpenSpec strict validation proves requirement/task consistency.
