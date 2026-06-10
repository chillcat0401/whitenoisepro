import { mkdir, readFile, writeFile } from "node:fs/promises";
import { dirname, join } from "node:path";
import {
  createAudioAsset,
  decodeWav,
  defaultSynthesisOptions,
  analyze,
  hash,
} from "./audio_synthesis_core.mjs";

const root = new URL("../", import.meta.url).pathname;
const rawDir = join(root, "composeApp/src/androidMain/res/raw");
const manifestPath = join(root, "docs/audio-assets/generated-audio-manifest.json");

const assets = [
  { id: "white_noise", file: "white_noise_loop.wav", seed: 0x1537a11, profile: "white" },
  { id: "pink_noise", file: "pink_noise_loop.wav", seed: 0x2498b22, profile: "pink" },
  { id: "brown_noise", file: "brown_noise_loop.wav", seed: 0x3579c33, profile: "brown" },
  { id: "fan", file: "fan_loop.wav", seed: 0x468ad44, profile: "fan" },
  { id: "rain", file: "rain_loop.wav", seed: 0x579be55, profile: "rain" },
  { id: "ocean", file: "ocean_loop.wav", seed: 0x68acf66, profile: "ocean" },
  { id: "forest", file: "forest_loop.wav", seed: 0x79bd077, profile: "forest" },
  { id: "fireplace", file: "fireplace_loop.wav", seed: 0x8ace188, profile: "fireplace" },
];

async function generate() {
  await mkdir(rawDir, { recursive: true });
  await mkdir(dirname(manifestPath), { recursive: true });
  const manifestAssets = [];

  for (const asset of assets) {
    const generated = createAudioAsset(asset, defaultSynthesisOptions);
    await writeFile(join(rawDir, asset.file), generated.wav);
    manifestAssets.push({
      ...asset,
      generator: "deterministic radix-2 inverse FFT",
      sha256: generated.sha256,
      bytes: generated.bytes,
      qa: generated.qa,
    });
  }

  const manifest = {
    schemaVersion: 1,
    generatedAt: "2026-06-08",
    ownership: "First-party generated assets; no third-party recordings or samples.",
    parameters: defaultSynthesisOptions,
    assets: manifestAssets,
  };
  await writeFile(manifestPath, `${JSON.stringify(manifest, null, 2)}\n`);
}

async function verify() {
  const manifest = JSON.parse(await readFile(manifestPath, "utf8"));
  for (const asset of manifest.assets) {
    const wav = await readFile(join(rawDir, asset.file));
    if (hash(wav) !== asset.sha256) throw new Error(`Hash mismatch: ${asset.file}`);
    const qa = analyze(decodeWav(wav, manifest.parameters), manifest.parameters);
    if (Math.abs(qa.rmsDbfs - asset.qa.rmsDbfs) > 1e-9) {
      throw new Error(`QA mismatch: ${asset.file}`);
    }
  }
  process.stdout.write(`Verified ${manifest.assets.length} generated audio assets.\n`);
}

if (process.argv.includes("--verify")) {
  await verify();
} else {
  await generate();
  await verify();
}

