import assert from "node:assert/strict";
import { mkdtempSync, mkdirSync, rmSync, writeFileSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";
import { spawnSync } from "node:child_process";
import test from "node:test";

const projectRoot = new URL("../", import.meta.url);

function runVerifier(bundlePath) {
    return spawnSync(
        process.execPath,
        ["tools/verify_release_bundle.mjs", bundlePath],
        {
            cwd: projectRoot,
            encoding: "utf8",
        },
    );
}

test("rejects a file that is not a ZIP bundle", () => {
    const directory = mkdtempSync(join(tmpdir(), "white-noise-pro-aab-"));
    const bundle = join(directory, "invalid.aab");
    try {
        writeFileSync(bundle, "not a bundle");
        const result = runVerifier(bundle);
        assert.notEqual(result.status, 0);
        assert.match(result.stderr, /not a ZIP/i);
    } finally {
        rmSync(directory, { recursive: true, force: true });
    }
});

test("rejects a structurally valid but unsigned bundle", () => {
    const directory = mkdtempSync(join(tmpdir(), "white-noise-pro-aab-"));
    const bundleRoot = join(directory, "bundle");
    const bundle = join(directory, "unsigned.aab");
    try {
        mkdirSync(join(bundleRoot, "base", "manifest"), { recursive: true });
        writeFileSync(join(bundleRoot, "base", "manifest", "AndroidManifest.xml"), "manifest");
        writeFileSync(join(bundleRoot, "BundleConfig.pb"), "config");
        const zipResult = spawnSync("zip", ["-q", "-r", bundle, "."], {
            cwd: bundleRoot,
            encoding: "utf8",
        });
        assert.equal(zipResult.status, 0, zipResult.stderr);

        const result = runVerifier(bundle);
        assert.notEqual(result.status, 0);
        assert.match(result.stderr, /signature verification failed/i);
    } finally {
        rmSync(directory, { recursive: true, force: true });
    }
});
