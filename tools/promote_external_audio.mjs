import { mkdir, readFile, stat, writeFile } from "node:fs/promises";
import { createHash } from "node:crypto";
import { createReadStream } from "node:fs";
import { dirname, isAbsolute, join, resolve } from "node:path";
import { execFile } from "node:child_process";
import { promisify } from "node:util";

const execFileP = promisify(execFile);

const releaseMappings = [
    { sourceId: "640655", soundId: "rain_soft", file: "rain_soft_loop.ogg", maxDurationSeconds: 61, defaultVolume: 0.42 },
    { sourceId: "669484", soundId: "rain_light_roof", file: "rain_light_roof_loop.ogg", maxDurationSeconds: 61, defaultVolume: 0.34 },
    { sourceId: "669486", soundId: "rain_window", file: "rain_window_loop.ogg", maxDurationSeconds: 58, defaultVolume: 0.36 },
    { sourceId: "650428", soundId: "rain_roof", file: "rain_roof_loop.ogg", maxDurationSeconds: 90, defaultVolume: 0.34 },
    { sourceId: "417797", soundId: "ocean_gentle", file: "ocean_gentle_loop.ogg", maxDurationSeconds: 90, defaultVolume: 0.38 },
    { sourceId: "431853", soundId: "ocean_waves", file: "ocean_waves_loop.ogg", maxDurationSeconds: 90, defaultVolume: 0.36 },
    { sourceId: "278982", soundId: "ocean_shore", file: "ocean_shore_loop.ogg", maxDurationSeconds: 78, defaultVolume: 0.34 },
    { sourceId: "813328", soundId: "fire_crackle", file: "fire_crackle_loop.ogg", maxDurationSeconds: 35, defaultVolume: 0.28 },
    { sourceId: "836535", soundId: "fire_hearth", file: "fire_hearth_loop.ogg", maxDurationSeconds: 80, defaultVolume: 0.26 },
    { sourceId: "843484", soundId: "fan_floor", file: "fan_floor_loop.ogg", maxDurationSeconds: 90, defaultVolume: 0.42 },
    { sourceId: "530908", soundId: "wind_forest", file: "wind_forest_loop.ogg", maxDurationSeconds: 41, defaultVolume: 0.30 },
];

function parseArgs(argv) {
    const options = {
        intake: "work/audio-intake/intake-manifest.json",
        outRaw: "composeApp/src/androidMain/res/raw",
        manifest: "docs/audio-assets/external-release-audio-manifest.json",
        targetLufs: -23,
        truePeak: -2,
        lra: 7,
        bitrate: "96k",
        maxDurationSeconds: null,
        allowSubset: false,
    };

    for (let index = 0; index < argv.length; index += 1) {
        const arg = argv[index];
        const next = () => {
            index += 1;
            if (index >= argv.length) throw new Error(`Missing value for ${arg}`);
            return argv[index];
        };
        if (arg === "--intake") options.intake = next();
        else if (arg === "--out-raw") options.outRaw = next();
        else if (arg === "--manifest") options.manifest = next();
        else if (arg === "--target-lufs") options.targetLufs = Number(next());
        else if (arg === "--true-peak") options.truePeak = Number(next());
        else if (arg === "--lra") options.lra = Number(next());
        else if (arg === "--bitrate") options.bitrate = next();
        else if (arg === "--max-duration") options.maxDurationSeconds = Number(next());
        else if (arg === "--allow-subset") options.allowSubset = true;
        else throw new Error(`Unknown option: ${arg}`);
    }
    return options;
}

function projectPath(value) {
    return isAbsolute(value) ? value : resolve(value);
}

async function run(command, args, options = {}) {
    try {
        return await execFileP(command, args, {
            maxBuffer: 50 * 1024 * 1024,
            ...options,
        });
    } catch (error) {
        const stderr = error.stderr || error.message;
        throw new Error(`${command} ${args.join(" ")} failed\n${stderr}`);
    }
}

async function sha256(file) {
    return await new Promise((resolveHash, reject) => {
        const hash = createHash("sha256");
        createReadStream(file)
            .on("data", (chunk) => hash.update(chunk))
            .on("error", reject)
            .on("end", () => resolveHash(hash.digest("hex")));
    });
}

async function ffprobe(file) {
    const { stdout } = await run("ffprobe", [
        "-v", "error",
        "-show_entries", "format=duration,size,bit_rate:stream=codec_name,sample_rate,channels",
        "-of", "json",
        file,
    ]);
    return JSON.parse(stdout);
}

async function ebur128(file) {
    const { stderr } = await run("ffmpeg", [
        "-hide_banner",
        "-nostats",
        "-i", file,
        "-filter_complex", "ebur128=peak=true",
        "-f", "null",
        "-",
    ]);
    const last = (regexp) => [...stderr.matchAll(regexp)].map((match) => Number(match[1])).at(-1) ?? null;
    return {
        integratedLufs: last(/I:\s*(-?[0-9.]+) LUFS/g),
        loudnessRangeLu: last(/LRA:\s*(-?[0-9.]+) LU/g),
        truePeakDbfs: last(/Peak:\s*(-?[0-9.]+) dBFS/g),
    };
}

async function seamMetrics(file, durationSeconds) {
    const segment = Math.min(5, Math.max(1, durationSeconds / 4));
    const outputArgs = [
        "-v", "error",
        "-i", file,
        "-filter_complex",
        `[0:a]atrim=start=${Math.max(0, durationSeconds - segment)}:end=${durationSeconds},asetpts=PTS-STARTPTS[a0];` +
        `[0:a]atrim=start=0:end=${segment},asetpts=PTS-STARTPTS[a1];` +
        "[a0][a1]concat=n=2:v=0:a=1,volumedetect",
        "-f", "null",
        "-",
    ];
    const { stderr } = await run("ffmpeg", outputArgs);
    const maxVolume = Number(stderr.match(/max_volume:\s*(-?[0-9.]+) dB/)?.[1] ?? "0");
    return {
        check: "tail-head concatenation volumedetect",
        segmentSeconds: segment,
        maxVolumeDb: maxVolume,
        status: Number.isFinite(maxVolume) ? "machine-pass" : "needs-review",
    };
}

function escapeFilterPath(value) {
    return value.replaceAll("\\", "\\\\").replaceAll(":", "\\:").replaceAll("'", "\\'");
}

async function promoteAsset(item, mapping, options) {
    if (!item.file?.path) throw new Error(`Missing source file path for ${mapping.sourceId}`);
    if (item.licenseName !== "Creative Commons 0") {
        throw new Error(`External audio ${mapping.sourceId} is not CC0: ${item.licenseName}`);
    }
    if (item.qa?.listening !== "human-pass") {
        throw new Error(`External audio ${mapping.sourceId} does not have human-pass listening QA`);
    }

    const inputPath = projectPath(item.file.path);
    const outputPath = join(projectPath(options.outRaw), mapping.file);
    await mkdir(dirname(outputPath), { recursive: true });

    const inputProbe = await ffprobe(inputPath);
    const inputDuration = Number(inputProbe.format?.duration ?? 0);
    const duration = Math.min(
        inputDuration,
        options.maxDurationSeconds ?? mapping.maxDurationSeconds,
    );
    const fadeDuration = Math.min(2, Math.max(0.25, duration / 12));
    const fadeOutStart = Math.max(0, duration - fadeDuration);
    const filter = [
        `atrim=start=0:duration=${duration}`,
        "asetpts=PTS-STARTPTS",
        `afade=t=in:st=0:d=${fadeDuration}`,
        `afade=t=out:st=${fadeOutStart}:d=${fadeDuration}`,
        `loudnorm=I=${options.targetLufs}:TP=${options.truePeak}:LRA=${options.lra}`,
    ].join(",");

    await run("ffmpeg", [
        "-y",
        "-hide_banner",
        "-nostats",
        "-i", inputPath,
        "-vn",
        "-ac", "2",
        "-ar", "44100",
        "-af", filter,
        "-c:a", "libvorbis",
        "-b:a", options.bitrate,
        outputPath,
    ]);

    const outputProbe = await ffprobe(outputPath);
    const loudness = await ebur128(outputPath);
    const outputDuration = Number(outputProbe.format?.duration ?? duration);
    const loop = await seamMetrics(outputPath, outputDuration);
    const outputStat = await stat(outputPath);

    return {
        sourceId: item.id,
        soundId: mapping.soundId,
        outputFile: mapping.file,
        sourceUrl: item.sourceUrl,
        sourceTitle: item.title,
        author: item.author,
        licenseName: item.licenseName,
        licenseUrl: item.licenseUrl,
        originalFile: item.file.path,
        originalSha256: item.file.sha256 ?? await sha256(inputPath),
        processedSha256: await sha256(outputPath),
        processedBytes: outputStat.size,
        defaultVolume: mapping.defaultVolume,
        processing: {
            tool: "ffmpeg",
            sourceStartSeconds: 0,
            durationSeconds: duration,
            fadeDurationSeconds: fadeDuration,
            targetLufs: options.targetLufs,
            truePeakDbfs: options.truePeak,
            lra: options.lra,
            codec: "libvorbis",
            bitrate: options.bitrate,
            commandSummary: `ffmpeg -i <source> -af '${filter}' -c:a libvorbis -b:a ${options.bitrate} ${mapping.file}`,
        },
        qa: {
            durationSeconds: outputDuration,
            sampleRate: Number(outputProbe.streams?.[0]?.sample_rate ?? 0),
            channels: outputProbe.streams?.[0]?.channels ?? 0,
            integratedLufs: loudness.integratedLufs,
            loudnessRangeLu: loudness.loudnessRangeLu,
            truePeakDbfs: loudness.truePeakDbfs,
            loop,
        },
        humanListeningQa: item.qa?.listening ?? "unknown",
        releaseDecision: "bundled-for-closed-testing",
    };
}

export async function promoteExternalAudio(argv = process.argv.slice(2)) {
    const options = parseArgs(argv);
    const intake = JSON.parse(await readFile(projectPath(options.intake), "utf8"));
    const itemsById = new Map((intake.items ?? []).map((item) => [item.id, item]));
    const mappings = options.allowSubset
        ? releaseMappings.filter((mapping) => itemsById.has(mapping.sourceId))
        : releaseMappings;

    for (const mapping of releaseMappings) {
        if (!options.allowSubset && !itemsById.has(mapping.sourceId)) {
            throw new Error(`Missing external audio intake item: ${mapping.sourceId}`);
        }
    }
    if (mappings.length === 0) throw new Error("No external audio items selected for promotion");

    const assets = [];
    for (const mapping of mappings) {
        assets.push(await promoteAsset(itemsById.get(mapping.sourceId), mapping, options));
    }

    const totalBytes = assets.reduce((sum, asset) => sum + asset.processedBytes, 0);
    const manifest = {
        schemaVersion: 1,
        generatedAt: new Date().toISOString(),
        ownership: "Freesound Creative Commons 0 external recordings processed for WhiteNoisePro closed testing.",
        sourceManifest: options.intake,
        outputRawDirectory: options.outRaw,
        target: {
            targetLufs: options.targetLufs,
            truePeakDbfs: options.truePeak,
            lra: options.lra,
            codec: "libvorbis",
            bitrate: options.bitrate,
        },
        totalProcessedBytes: totalBytes,
        assets,
    };

    await mkdir(dirname(projectPath(options.manifest)), { recursive: true });
    await writeFile(projectPath(options.manifest), `${JSON.stringify(manifest, null, 2)}\n`);
    process.stdout.write(`Promoted ${assets.length} external audio assets (${totalBytes} bytes).\n`);
}

try {
    if (import.meta.url === `file://${process.argv[1]}`) {
        await promoteExternalAudio();
    }
} catch (error) {
    process.stderr.write(`${error.message}\n`);
    process.exitCode = 1;
}
