import assert from "node:assert/strict";
import { existsSync, mkdtempSync, readFileSync, rmSync, statSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";
import { spawnSync } from "node:child_process";
import test from "node:test";

const projectRoot = new URL("../", import.meta.url);

function runStudio(args) {
    return spawnSync(process.execPath, ["tools/audio_asset_studio.mjs", ...args], {
        cwd: projectRoot,
        encoding: "utf8",
    });
}

test("generates candidate WAVs, manifest, and audition page outside release resources", () => {
    const outputDirectory = mkdtempSync(join(tmpdir(), "wnp-audio-studio-"));

    try {
        const result = runStudio([
            "--profile", "rain",
            "--count", "2",
            "--seed", "9000",
            "--prefix", "rain-soft",
            "--duration-seconds", "1",
            "--out", outputDirectory,
        ]);
        assert.equal(result.status, 0, result.stderr || result.stdout);

        const manifestPath = join(outputDirectory, "manifest.json");
        const auditionPath = join(outputDirectory, "audition.html");
        assert.ok(existsSync(manifestPath));
        assert.ok(existsSync(auditionPath));

        const manifest = JSON.parse(readFileSync(manifestPath, "utf8"));
        assert.equal(manifest.schemaVersion, 1);
        assert.equal(manifest.mode, "candidate");
        assert.equal(manifest.profile, "rain");
        assert.equal(manifest.assets.length, 2);
        assert.equal(manifest.parameters.targetRms, 0.1);

        for (const asset of manifest.assets) {
            assert.equal(asset.status, "candidate");
            assert.equal(asset.publishable, false);
            assert.equal(asset.profile, "rain");
            assert.match(asset.file, /^rain_soft_\d{3}\.wav$/);
            assert.match(asset.sha256, /^[a-f0-9]{64}$/);
            assert.ok(asset.qa.seamRatio < 2.5);
            const wavPath = join(outputDirectory, asset.file);
            assert.ok(existsSync(wavPath));
            assert.ok(statSync(wavPath).size > 44);
            const header = readFileSync(wavPath).toString("ascii", 0, 12);
            assert.equal(header.slice(0, 4), "RIFF");
            assert.equal(header.slice(8, 12), "WAVE");
        }

        const audition = readFileSync(auditionPath, "utf8");
        assert.match(audition, /<audio controls/);
        assert.match(audition, /rain_soft_001\.wav/);
        assert.match(audition, /publishable: false/);
    } finally {
        rmSync(outputDirectory, { recursive: true, force: true });
    }
});

test("fails with supported profiles when profile is invalid", () => {
    const outputDirectory = mkdtempSync(join(tmpdir(), "wnp-audio-studio-"));
    try {
        const result = runStudio([
            "--profile", "invalid",
            "--out", outputDirectory,
        ]);
        assert.notEqual(result.status, 0);
        assert.match(result.stderr, /Unsupported audio profile.*invalid/i);
        assert.match(result.stderr, /Supported profiles:/i);
    } finally {
        rmSync(outputDirectory, { recursive: true, force: true });
    }
});

