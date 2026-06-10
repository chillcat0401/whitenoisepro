import { createHash } from "node:crypto";

export const defaultSynthesisOptions = Object.freeze({
    sampleRate: 44_100,
    sampleCount: 1 << 19,
    targetRms: 0.1,
    maxPeak: 0.78,
});

export const supportedProfiles = Object.freeze([
    "white",
    "pink",
    "brown",
    "fan",
    "rain",
    "light-rain",
    "ocean",
    "forest",
    "fireplace",
]);

export function sampleCountForDuration(durationSeconds, sampleRate = defaultSynthesisOptions.sampleRate) {
    const requested = Math.max(1, Number(durationSeconds)) * sampleRate;
    let upper = 1;
    while (upper < requested) upper *= 2;
    const lower = Math.max(1, upper / 2);
    const nearest = requested - lower <= upper - requested ? lower : upper;
    return Math.max(16_384, nearest);
}

function normalizeOptions(options = {}) {
    return {
        sampleRate: Number(options.sampleRate ?? defaultSynthesisOptions.sampleRate),
        sampleCount: Number(options.sampleCount ?? defaultSynthesisOptions.sampleCount),
        targetRms: Number(options.targetRms ?? defaultSynthesisOptions.targetRms),
        maxPeak: Number(options.maxPeak ?? defaultSynthesisOptions.maxPeak),
    };
}

function assertPowerOfTwo(value) {
    if (!Number.isInteger(value) || value < 2 || (value & (value - 1)) !== 0) {
        throw new Error(`sampleCount must be a power of two: ${value}`);
    }
}

function assertSupportedProfile(profile) {
    if (!supportedProfiles.includes(profile)) {
        throw new Error(
            `Unsupported audio profile: ${profile}. Supported profiles: ${supportedProfiles.join(", ")}`,
        );
    }
}

function randomGenerator(seed) {
    let state = seed >>> 0;
    return () => {
        state ^= state << 13;
        state ^= state >>> 17;
        state ^= state << 5;
        return (state >>> 0) / 0x1_0000_0000;
    };
}

function reverseBits(value, width) {
    let result = 0;
    for (let bit = 0; bit < width; bit += 1) {
        result = (result << 1) | ((value >>> bit) & 1);
    }
    return result;
}

function inverseFft(real, imag) {
    const length = real.length;
    const width = Math.log2(length);
    for (let index = 0; index < length; index += 1) {
        const reversed = reverseBits(index, width);
        if (reversed > index) {
            [real[index], real[reversed]] = [real[reversed], real[index]];
            [imag[index], imag[reversed]] = [imag[reversed], imag[index]];
        }
    }

    for (let size = 2; size <= length; size *= 2) {
        const half = size / 2;
        const angleStep = (2 * Math.PI) / size;
        for (let offset = 0; offset < length; offset += size) {
            for (let index = 0; index < half; index += 1) {
                const angle = angleStep * index;
                const cosine = Math.cos(angle);
                const sine = Math.sin(angle);
                const even = offset + index;
                const odd = even + half;
                const oddReal = real[odd] * cosine - imag[odd] * sine;
                const oddImag = real[odd] * sine + imag[odd] * cosine;
                real[odd] = real[even] - oddReal;
                imag[odd] = imag[even] - oddImag;
                real[even] += oddReal;
                imag[even] += oddImag;
            }
        }
    }

    for (let index = 0; index < length; index += 1) {
        real[index] /= length;
    }
}

function spectralAmplitude(profile, frequency) {
    const lowCut = frequency / (frequency + 28);
    const highCut = Math.exp(-Math.pow(frequency / 17_000, 4));
    if (profile === "white") return lowCut * highCut;
    if (profile === "pink") return lowCut * highCut / Math.sqrt(Math.max(frequency, 35));
    if (profile === "brown") return lowCut * highCut / Math.max(frequency, 55);

    if (profile === "fan") {
        const background = 0.22 * lowCut * highCut / Math.sqrt(Math.max(frequency, 45));
        const harmonics = [52, 104, 156, 208, 260].reduce((sum, center, index) => {
            const width = 2.2 + index * 0.45;
            return sum + (1 / (index + 1)) * Math.exp(-0.5 * Math.pow((frequency - center) / width, 2));
        }, 0);
        return background + harmonics * 0.2;
    }

    if (profile === "rain") {
        const rainBed = Math.exp(-Math.pow(frequency / 5_600, 2.2)) / Math.sqrt(Math.max(frequency, 70));
        const softSpray = Math.exp(-Math.pow(frequency / 8_800, 3.2)) / Math.sqrt(Math.max(frequency, 220));
        const roofBody = Math.exp(-0.5 * Math.pow((frequency - 720) / 520, 2)) * 0.11;
        const scatteredDrops = [950, 1_650, 2_500, 3_700, 5_200].reduce((sum, center, index) => {
            const width = 360 + index * 150;
            return sum + 0.055 * Math.exp(-0.5 * Math.pow((frequency - center) / width, 2));
        }, 0);
        const airRollOff = Math.exp(-Math.pow(frequency / 11_500, 4));
        return lowCut * airRollOff * (0.95 * rainBed + 0.28 * softSpray + roofBody + scatteredDrops) * highCut;
    }

    if (profile === "light-rain") {
        const distantBed = Math.exp(-Math.pow(frequency / 3_900, 2.4)) / Math.sqrt(Math.max(frequency, 95));
        const fineMist = Math.exp(-Math.pow(frequency / 7_200, 3.6)) / Math.sqrt(Math.max(frequency, 360));
        const windowBody = Math.exp(-0.5 * Math.pow((frequency - 560) / 460, 2)) * 0.055;
        const sparseDrops = [820, 1_450, 2_300, 3_400].reduce((sum, center, index) => {
            const width = 420 + index * 180;
            return sum + 0.026 * Math.exp(-0.5 * Math.pow((frequency - center) / width, 2));
        }, 0);
        const softAirRollOff = Math.exp(-Math.pow(frequency / 8_800, 4));
        return lowCut * softAirRollOff * (0.72 * distantBed + 0.14 * fineMist + windowBody + sparseDrops) * highCut;
    }

    if (profile === "ocean") {
        const swell = [0.42, 0.84, 1.26, 2.1, 3.36].reduce((sum, center, index) => {
            const width = 0.08 + index * 0.04;
            return sum + (1 / (index + 1)) * Math.exp(-0.5 * Math.pow((frequency - center) / width, 2));
        }, 0);
        const surf = Math.exp(-Math.pow(frequency / 4_500, 2)) / Math.sqrt(Math.max(frequency, 28));
        return highCut * (0.55 * swell + 0.18 * surf);
    }

    if (profile === "forest") {
        const air = Math.exp(-Math.pow(frequency / 9_000, 4)) / Math.sqrt(Math.max(frequency, 90));
        const insects = [2_200, 3_400, 5_600, 7_200].reduce((sum, center, index) => {
            const width = 28 + index * 10;
            return sum + 0.08 * Math.exp(-0.5 * Math.pow((frequency - center) / width, 2));
        }, 0);
        const leaves = Math.exp(-0.5 * Math.pow((frequency - 780) / 360, 2)) * 0.16;
        return lowCut * highCut * (0.52 * air + leaves + insects);
    }

    if (profile === "fireplace") {
        const warmth = Math.exp(-Math.pow(frequency / 2_800, 2)) / Math.sqrt(Math.max(frequency, 65));
        const crackle = [1_100, 1_900, 3_300, 4_700, 6_100].reduce((sum, center, index) => {
            const width = 70 + index * 24;
            return sum + 0.13 * Math.exp(-0.5 * Math.pow((frequency - center) / width, 2));
        }, 0);
        return lowCut * highCut * (0.72 * warmth + crackle);
    }

    assertSupportedProfile(profile);
}

export function synthesize(asset, options = {}) {
    const normalizedOptions = normalizeOptions(options);
    const { sampleRate, sampleCount, targetRms, maxPeak } = normalizedOptions;
    assertSupportedProfile(asset.profile);
    assertPowerOfTwo(sampleCount);

    const real = new Float64Array(sampleCount);
    const imag = new Float64Array(sampleCount);
    const random = randomGenerator(asset.seed);
    const nyquistBin = sampleCount / 2;

    for (let bin = 1; bin < nyquistBin; bin += 1) {
        const frequency = (bin * sampleRate) / sampleCount;
        const amplitude = spectralAmplitude(asset.profile, frequency);
        const phase = random() * Math.PI * 2;
        const binReal = amplitude * Math.cos(phase);
        const binImag = amplitude * Math.sin(phase);
        real[bin] = binReal;
        imag[bin] = binImag;
        real[sampleCount - bin] = binReal;
        imag[sampleCount - bin] = -binImag;
    }

    inverseFft(real, imag);

    let mean = 0;
    for (const value of real) mean += value;
    mean /= sampleCount;

    let sumSquares = 0;
    let peak = 0;
    for (let index = 0; index < sampleCount; index += 1) {
        real[index] -= mean;
        sumSquares += real[index] * real[index];
        peak = Math.max(peak, Math.abs(real[index]));
    }
    const sourceRms = Math.sqrt(sumSquares / sampleCount);
    const scale = Math.min(targetRms / sourceRms, maxPeak / peak);

    const pcm = new Int16Array(sampleCount);
    for (let index = 0; index < sampleCount; index += 1) {
        pcm[index] = Math.round(Math.max(-1, Math.min(1, real[index] * scale)) * 32_767);
    }
    return pcm;
}

export function encodeWav(samples, options = {}) {
    const { sampleRate } = normalizeOptions(options);
    const dataBytes = samples.length * 2;
    const buffer = Buffer.alloc(44 + dataBytes);
    buffer.write("RIFF", 0);
    buffer.writeUInt32LE(36 + dataBytes, 4);
    buffer.write("WAVE", 8);
    buffer.write("fmt ", 12);
    buffer.writeUInt32LE(16, 16);
    buffer.writeUInt16LE(1, 20);
    buffer.writeUInt16LE(1, 22);
    buffer.writeUInt32LE(sampleRate, 24);
    buffer.writeUInt32LE(sampleRate * 2, 28);
    buffer.writeUInt16LE(2, 32);
    buffer.writeUInt16LE(16, 34);
    buffer.write("data", 36);
    buffer.writeUInt32LE(dataBytes, 40);
    for (let index = 0; index < samples.length; index += 1) {
        buffer.writeInt16LE(samples[index], 44 + index * 2);
    }
    return buffer;
}

export function analyze(samples, options = {}) {
    const { sampleRate } = normalizeOptions(options);
    let sumSquares = 0;
    let peak = 0;
    const deltas = [];
    for (let index = 0; index < samples.length; index += 1) {
        const normalized = samples[index] / 32_767;
        sumSquares += normalized * normalized;
        peak = Math.max(peak, Math.abs(normalized));
        if (index > 0) deltas.push(Math.abs(samples[index] - samples[index - 1]) / 32_767);
    }
    deltas.sort((left, right) => left - right);
    const p99Delta = deltas[Math.floor(deltas.length * 0.99)];
    const boundaryDelta = Math.abs(samples[0] - samples[samples.length - 1]) / 32_767;
    const rms = Math.sqrt(sumSquares / samples.length);
    const seamRatio = boundaryDelta / Math.max(p99Delta, 1e-9);

    if (rms < 0.06 || rms > 0.14) throw new Error(`RMS out of range: ${rms}`);
    if (peak > 0.8) throw new Error(`Peak out of range: ${peak}`);
    if (seamRatio > 2.5) throw new Error(`Loop boundary outlier: ${seamRatio}`);

    return {
        durationSeconds: samples.length / sampleRate,
        sampleRate,
        channels: 1,
        bitDepth: 16,
        rmsDbfs: 20 * Math.log10(rms),
        peakDbfs: 20 * Math.log10(peak),
        boundaryDelta,
        p99AdjacentDelta: p99Delta,
        seamRatio,
    };
}

export function decodeWav(buffer, options = {}) {
    const { sampleRate } = normalizeOptions(options);
    if (buffer.toString("ascii", 0, 4) !== "RIFF" || buffer.toString("ascii", 8, 12) !== "WAVE") {
        throw new Error("Invalid WAV header");
    }
    if (buffer.readUInt16LE(22) !== 1 || buffer.readUInt32LE(24) !== sampleRate || buffer.readUInt16LE(34) !== 16) {
        throw new Error("Unexpected WAV format");
    }
    const sampleLength = buffer.readUInt32LE(40) / 2;
    const samples = new Int16Array(sampleLength);
    for (let index = 0; index < sampleLength; index += 1) {
        samples[index] = buffer.readInt16LE(44 + index * 2);
    }
    return samples;
}

export function hash(buffer) {
    return createHash("sha256").update(buffer).digest("hex");
}

export function createAudioAsset(spec, options = {}) {
    const normalizedOptions = normalizeOptions(options);
    const samples = synthesize(spec, normalizedOptions);
    const qa = analyze(samples, normalizedOptions);
    const wav = encodeWav(samples, normalizedOptions);
    return {
        wav,
        qa,
        sha256: hash(wav),
        bytes: wav.length,
    };
}
