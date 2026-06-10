import assert from "node:assert/strict";
import test from "node:test";

import {
    createAudioAsset,
    decodeWav,
    sampleCountForDuration,
    supportedProfiles,
} from "./audio_synthesis_core.mjs";

test("creates deterministic loop-safe WAV candidates with QA metrics", () => {
    assert.ok(supportedProfiles.includes("rain"));

    const spec = {
        id: "rain_candidate",
        file: "rain_candidate.wav",
        profile: "rain",
        seed: 12345,
    };
    const options = {
        sampleRate: 44_100,
        sampleCount: 32_768,
        targetRms: 0.1,
        maxPeak: 0.78,
    };

    const first = createAudioAsset(spec, options);
    const second = createAudioAsset(spec, options);

    assert.deepEqual(first.wav, second.wav);
    assert.equal(first.sha256, second.sha256);
    assert.equal(first.bytes, first.wav.length);
    assert.equal(first.wav.toString("ascii", 0, 4), "RIFF");
    assert.equal(first.wav.toString("ascii", 8, 12), "WAVE");
    assert.equal(first.qa.sampleRate, 44_100);
    assert.equal(first.qa.channels, 1);
    assert.equal(first.qa.bitDepth, 16);
    assert.ok(first.qa.rmsDbfs > -25 && first.qa.rmsDbfs < -15);
    assert.ok(first.qa.peakDbfs < -1);
    assert.ok(first.qa.seamRatio < 2.5);

    const decoded = decodeWav(first.wav, { sampleRate: 44_100 });
    assert.equal(decoded.length, 32_768);
});

test("rejects unsupported synthesis profiles", () => {
    assert.throws(
        () => createAudioAsset(
            {
                id: "bad",
                file: "bad.wav",
                profile: "unknown_profile",
                seed: 1,
            },
            { sampleRate: 44_100, sampleCount: 16_384, targetRms: 0.1, maxPeak: 0.78 },
        ),
        /Unsupported audio profile.*unknown_profile/i,
    );
});

test("maps requested duration to the nearest FFT-safe sample count", () => {
    assert.equal(sampleCountForDuration(3, 44_100), 131_072);
    assert.equal(sampleCountForDuration(12, 44_100), 524_288);
});

test("rain profile limits harsh adjacent-sample roughness", () => {
    const generated = createAudioAsset(
        {
            id: "rain_texture",
            file: "rain_texture.wav",
            profile: "rain",
            seed: 260609,
        },
        {
            sampleRate: 44_100,
            sampleCount: 32_768,
            targetRms: 0.1,
            maxPeak: 0.78,
        },
    );

    assert.ok(
        generated.qa.p99AdjacentDelta < 0.16,
        `rain p99 adjacent delta should avoid narrow shower texture: ${generated.qa.p99AdjacentDelta}`,
    );
});

test("light-rain profile generates softer candidates than rain", () => {
    assert.ok(supportedProfiles.includes("light-rain"));

    const options = {
        sampleRate: 44_100,
        sampleCount: 32_768,
        targetRms: 0.1,
        maxPeak: 0.78,
    };
    const rain = createAudioAsset(
        {
            id: "rain",
            file: "rain.wav",
            profile: "rain",
            seed: 260620,
        },
        options,
    );
    const lightRain = createAudioAsset(
        {
            id: "light_rain",
            file: "light_rain.wav",
            profile: "light-rain",
            seed: 260620,
        },
        options,
    );

    assert.equal(lightRain.qa.sampleRate, 44_100);
    assert.equal(lightRain.qa.channels, 1);
    assert.ok(lightRain.qa.seamRatio < 2.5);
    assert.ok(
        lightRain.qa.p99AdjacentDelta < rain.qa.p99AdjacentDelta * 0.75,
        `light-rain should be softer than rain: light=${lightRain.qa.p99AdjacentDelta}, rain=${rain.qa.p99AdjacentDelta}`,
    );
    assert.ok(
        lightRain.qa.p99AdjacentDelta < 0.07,
        `light-rain p99 adjacent delta should stay gentle: ${lightRain.qa.p99AdjacentDelta}`,
    );
});
