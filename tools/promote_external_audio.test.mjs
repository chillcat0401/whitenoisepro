import assert from "node:assert/strict";
import { existsSync, mkdtempSync, readFileSync, rmSync, writeFileSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";
import { spawnSync } from "node:child_process";
import test from "node:test";

import {
    createAudioAsset,
    defaultSynthesisOptions,
    sampleCountForDuration,
} from "./audio_synthesis_core.mjs";

const projectRoot = new URL("../", import.meta.url);

function runPromotion(args) {
    return spawnSync(process.execPath, ["tools/promote_external_audio.mjs", ...args], {
        cwd: projectRoot,
        encoding: "utf8",
    });
}

test("promotes an external intake WAV into a release Ogg with manifest evidence", () => {
    const directory = mkdtempSync(join(tmpdir(), "wnp-external-audio-"));
    try {
        const original = createAudioAsset(
            { id: "soft_rain_fixture", file: "soft_rain_fixture.wav", profile: "rain", seed: 44 },
            {
                ...defaultSynthesisOptions,
                sampleCount: sampleCountForDuration(3),
            },
        );
        const originalPath = join(directory, "640655__fixture.wav");
        writeFileSync(originalPath, original.wav);

        const intakePath = join(directory, "intake.json");
        writeFileSync(
            intakePath,
            JSON.stringify({
                items: [
                    {
                        id: "640655",
                        category: "小雨",
                        title: "Soft rain fixture",
                        sourceUrl: "https://freesound.org/people/barkenov/sounds/640655/",
                        author: "barkenov",
                        licenseName: "Creative Commons 0",
                        licenseUrl: "http://creativecommons.org/publicdomain/zero/1.0/",
                        file: { path: originalPath, sha256: original.sha256 },
                        qa: { listening: "human-pass" },
                    },
                ],
            }),
        );

        const outRaw = join(directory, "raw");
        const outManifest = join(directory, "external-release-audio-manifest.json");
        const result = runPromotion([
            "--intake", intakePath,
            "--out-raw", outRaw,
            "--manifest", outManifest,
            "--allow-subset",
            "--max-duration", "2",
            "--bitrate", "64k",
        ]);

        assert.equal(result.status, 0, result.stderr || result.stdout);
        assert.ok(existsSync(join(outRaw, "rain_soft_loop.ogg")));

        const manifest = JSON.parse(readFileSync(outManifest, "utf8"));
        assert.equal(manifest.schemaVersion, 1);
        assert.equal(manifest.assets.length, 1);
        assert.equal(manifest.assets[0].soundId, "rain_soft");
        assert.equal(manifest.assets[0].sourceId, "640655");
        assert.match(manifest.assets[0].processedSha256, /^[a-f0-9]{64}$/);
        assert.ok(manifest.assets[0].processedBytes > 1000);
        assert.equal(manifest.assets[0].humanListeningQa, "human-pass");
        assert.ok(manifest.assets[0].qa.durationSeconds > 0);
    } finally {
        rmSync(directory, { recursive: true, force: true });
    }
});

test("requires all release mappings unless subset mode is explicit", () => {
    const directory = mkdtempSync(join(tmpdir(), "wnp-external-audio-"));
    try {
        const intakePath = join(directory, "intake.json");
        writeFileSync(intakePath, JSON.stringify({ items: [] }));

        const result = runPromotion([
            "--intake", intakePath,
            "--out-raw", join(directory, "raw"),
            "--manifest", join(directory, "manifest.json"),
        ]);

        assert.notEqual(result.status, 0);
        assert.match(result.stderr, /Missing external audio intake item/i);
    } finally {
        rmSync(directory, { recursive: true, force: true });
    }
});
