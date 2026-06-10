import { mkdir, writeFile } from "node:fs/promises";
import { join, resolve } from "node:path";
import {
    createAudioAsset,
    defaultSynthesisOptions,
    sampleCountForDuration,
    supportedProfiles,
} from "./audio_synthesis_core.mjs";

function parseArgs(argv) {
    const options = {
        profile: "rain",
        count: 3,
        seed: 1,
        prefix: null,
        durationSeconds: 12,
        out: null,
        targetRms: defaultSynthesisOptions.targetRms,
        maxPeak: defaultSynthesisOptions.maxPeak,
    };

    for (let index = 0; index < argv.length; index += 1) {
        const arg = argv[index];
        const next = () => {
            index += 1;
            if (index >= argv.length) throw new Error(`Missing value for ${arg}`);
            return argv[index];
        };
        if (arg === "--profile") options.profile = next();
        else if (arg === "--count") options.count = Number(next());
        else if (arg === "--seed") options.seed = Number(next());
        else if (arg === "--prefix") options.prefix = next();
        else if (arg === "--duration-seconds") options.durationSeconds = Number(next());
        else if (arg === "--out") options.out = next();
        else if (arg === "--target-rms") options.targetRms = Number(next());
        else if (arg === "--max-peak") options.maxPeak = Number(next());
        else if (arg === "--help") options.help = true;
        else throw new Error(`Unknown option: ${arg}`);
    }

    return options;
}

function usage() {
    return `Usage: node tools/audio_asset_studio.mjs --profile rain --count 3 --seed 9000 --prefix rain-soft --out work/audio-candidates/rain-soft

Options:
  --profile <name>            Supported profiles: ${supportedProfiles.join(", ")}
  --count <number>            Number of candidates to generate
  --seed <number>             Base seed; each candidate increments by 1
  --prefix <name>             Output filename prefix
  --duration-seconds <number> Approximate loop duration; rounded to nearest FFT-safe sample count
  --target-rms <number>       Target RMS, default ${defaultSynthesisOptions.targetRms}
  --max-peak <number>         Max peak, default ${defaultSynthesisOptions.maxPeak}
  --out <directory>           Output directory
`;
}

function assertOptions(options) {
    if (!supportedProfiles.includes(options.profile)) {
        throw new Error(
            `Unsupported audio profile: ${options.profile}. Supported profiles: ${supportedProfiles.join(", ")}`,
        );
    }
    if (!Number.isInteger(options.count) || options.count < 1 || options.count > 50) {
        throw new Error(`count must be an integer between 1 and 50: ${options.count}`);
    }
    if (!Number.isFinite(options.seed)) throw new Error(`seed must be numeric: ${options.seed}`);
    if (!Number.isFinite(options.durationSeconds) || options.durationSeconds <= 0) {
        throw new Error(`duration-seconds must be positive: ${options.durationSeconds}`);
    }
}

function sanitizePrefix(value) {
    return String(value)
        .trim()
        .toLowerCase()
        .replace(/[^a-z0-9]+/g, "_")
        .replace(/^_+|_+$/g, "") || "candidate";
}

function defaultOutputDirectory(profile) {
    const stamp = new Date().toISOString().replace(/[-:]/g, "").replace(/\..+$/, "Z");
    return join("work/audio-candidates", `${profile}-${stamp}`);
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;");
}

function renderAudition(manifest) {
    const rows = manifest.assets.map((asset) => `
        <tr>
            <td>${escapeHtml(asset.id)}</td>
            <td>${escapeHtml(asset.profile)}</td>
            <td>${asset.seed}</td>
            <td>${asset.qa.rmsDbfs.toFixed(2)} dBFS</td>
            <td>${asset.qa.peakDbfs.toFixed(2)} dBFS</td>
            <td>${asset.qa.seamRatio.toFixed(3)}</td>
            <td>publishable: ${asset.publishable}</td>
            <td><audio controls src="${escapeHtml(asset.file)}"></audio></td>
        </tr>`).join("\n");

    return `<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>WhiteNoisePro Audio Candidates</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; margin: 32px; background: #f7f4ef; color: #211f1b; }
        table { border-collapse: collapse; width: 100%; background: #fff; }
        th, td { border: 1px solid #ddd6ca; padding: 10px; text-align: left; }
        th { background: #ebe2d4; }
        audio { width: 220px; }
    </style>
</head>
<body>
    <h1>WhiteNoisePro Audio Candidates</h1>
    <p>Mode: ${escapeHtml(manifest.mode)}. Profile: ${escapeHtml(manifest.profile)}. Generated: ${escapeHtml(manifest.generatedAt)}.</p>
    <table>
        <thead>
            <tr><th>ID</th><th>Profile</th><th>Seed</th><th>RMS</th><th>Peak</th><th>Seam</th><th>Status</th><th>Audition</th></tr>
        </thead>
        <tbody>${rows}
        </tbody>
    </table>
</body>
</html>
`;
}

export async function runAudioAssetStudio(argv = process.argv.slice(2)) {
    const options = parseArgs(argv);
    if (options.help) {
        process.stdout.write(usage());
        return;
    }

    assertOptions(options);

    const prefix = sanitizePrefix(options.prefix ?? `${options.profile}-candidate`);
    const outputDirectory = resolve(options.out ?? defaultOutputDirectory(options.profile));
    const synthesisOptions = {
        ...defaultSynthesisOptions,
        sampleCount: sampleCountForDuration(options.durationSeconds),
        targetRms: options.targetRms,
        maxPeak: options.maxPeak,
    };

    await mkdir(outputDirectory, { recursive: true });

    const assets = [];
    for (let index = 0; index < options.count; index += 1) {
        const ordinal = String(index + 1).padStart(3, "0");
        const file = `${prefix}_${ordinal}.wav`;
        const assetSpec = {
            id: `${prefix}_${ordinal}`,
            file,
            profile: options.profile,
            seed: (options.seed + index) >>> 0,
        };
        const generated = createAudioAsset(assetSpec, synthesisOptions);
        await writeFile(join(outputDirectory, file), generated.wav);
        assets.push({
            ...assetSpec,
            generator: "deterministic radix-2 inverse FFT",
            sha256: generated.sha256,
            bytes: generated.bytes,
            qa: generated.qa,
            status: "candidate",
            publishable: false,
        });
    }

    const manifest = {
        schemaVersion: 1,
        mode: "candidate",
        generatedAt: new Date().toISOString(),
        ownership: "First-party generated candidate assets; no third-party recordings or samples.",
        profile: options.profile,
        parameters: synthesisOptions,
        assets,
    };

    await writeFile(join(outputDirectory, "manifest.json"), `${JSON.stringify(manifest, null, 2)}\n`);
    await writeFile(join(outputDirectory, "audition.html"), renderAudition(manifest));
    process.stdout.write(`Generated ${assets.length} audio candidates in ${outputDirectory}\n`);
}

try {
    await runAudioAssetStudio();
} catch (error) {
    process.stderr.write(`${error.message}\n`);
    process.exitCode = 1;
}
