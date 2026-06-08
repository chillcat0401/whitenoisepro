import { deflateSync } from "node:zlib";
import { mkdirSync, writeFileSync } from "node:fs";
import { dirname, resolve } from "node:path";

const SIZE = 512;
const SAMPLE_GRID = 4;
const PLAY_MAX_BYTES = 1024 * 1024;
const DEFAULT_OUTPUT = "docs/store-assets/google-play-icon-512.png";

const COLORS = {
    background: [0x11, 0x13, 0x17, 0xff],
    moon: [0x8a, 0xd3, 0xce, 0xff],
    wave: [0xac, 0xca, 0xe3, 0xff],
};

function parseOutput(args) {
    const outputIndex = args.indexOf("--output");
    if (outputIndex === -1) return resolve(DEFAULT_OUTPUT);
    if (!args[outputIndex + 1]) throw new Error("--output requires a path");
    return resolve(args[outputIndex + 1]);
}

function insideCircle(x, y, centerX, centerY, radius) {
    const dx = x - centerX;
    const dy = y - centerY;
    return dx * dx + dy * dy <= radius * radius;
}

function quadraticPoint(start, control, end, t) {
    const inverse = 1 - t;
    return {
        x: inverse * inverse * start.x + 2 * inverse * t * control.x + t * t * end.x,
        y: inverse * inverse * start.y + 2 * inverse * t * control.y + t * t * end.y,
    };
}

function distanceToSegmentSquared(x, y, start, end) {
    const dx = end.x - start.x;
    const dy = end.y - start.y;
    const lengthSquared = dx * dx + dy * dy;
    const projection = lengthSquared === 0
        ? 0
        : Math.max(0, Math.min(1, ((x - start.x) * dx + (y - start.y) * dy) / lengthSquared));
    const closestX = start.x + projection * dx;
    const closestY = start.y + projection * dy;
    const distanceX = x - closestX;
    const distanceY = y - closestY;
    return distanceX * distanceX + distanceY * distanceY;
}

function createWaveSegments(start, control, end) {
    const segments = [];
    let previous = start;
    for (let index = 1; index <= 28; index += 1) {
        const current = quadraticPoint(start, control, end, index / 28);
        segments.push([previous, current]);
        previous = current;
    }
    return segments;
}

const waveSegments = [
    createWaveSegments({ x: 292, y: 220 }, { x: 336, y: 256 }, { x: 292, y: 292 }),
    createWaveSegments({ x: 330, y: 198 }, { x: 390, y: 256 }, { x: 330, y: 314 }),
    createWaveSegments({ x: 370, y: 176 }, { x: 446, y: 256 }, { x: 370, y: 336 }),
];

function sampleColor(x, y) {
    const inMoon = insideCircle(x, y, 204, 256, 126)
        && !insideCircle(x, y, 255, 222, 105);
    if (inMoon) return COLORS.moon;

    const strokeRadiusSquared = 8 * 8;
    for (const wave of waveSegments) {
        for (const [start, end] of wave) {
            if (distanceToSegmentSquared(x, y, start, end) <= strokeRadiusSquared) {
                return COLORS.wave;
            }
        }
    }
    return COLORS.background;
}

function renderPixels() {
    const raw = Buffer.alloc((SIZE * 4 + 1) * SIZE);
    for (let y = 0; y < SIZE; y += 1) {
        const rowOffset = y * (SIZE * 4 + 1);
        raw[rowOffset] = 0;
        for (let x = 0; x < SIZE; x += 1) {
            const totals = [0, 0, 0, 0];
            for (let sampleY = 0; sampleY < SAMPLE_GRID; sampleY += 1) {
                for (let sampleX = 0; sampleX < SAMPLE_GRID; sampleX += 1) {
                    const color = sampleColor(
                        x + (sampleX + 0.5) / SAMPLE_GRID,
                        y + (sampleY + 0.5) / SAMPLE_GRID,
                    );
                    for (let channel = 0; channel < 4; channel += 1) {
                        totals[channel] += color[channel];
                    }
                }
            }
            const pixelOffset = rowOffset + 1 + x * 4;
            const sampleCount = SAMPLE_GRID * SAMPLE_GRID;
            for (let channel = 0; channel < 4; channel += 1) {
                raw[pixelOffset + channel] = Math.round(totals[channel] / sampleCount);
            }
        }
    }
    return raw;
}

function crc32(buffer) {
    let crc = 0xffffffff;
    for (const byte of buffer) {
        crc ^= byte;
        for (let bit = 0; bit < 8; bit += 1) {
            crc = (crc >>> 1) ^ (0xedb88320 & -(crc & 1));
        }
    }
    return (crc ^ 0xffffffff) >>> 0;
}

function pngChunk(type, data) {
    const typeBuffer = Buffer.from(type, "ascii");
    const length = Buffer.alloc(4);
    length.writeUInt32BE(data.length);
    const checksum = Buffer.alloc(4);
    checksum.writeUInt32BE(crc32(Buffer.concat([typeBuffer, data])));
    return Buffer.concat([length, typeBuffer, data, checksum]);
}

function encodePng(rawPixels) {
    const header = Buffer.alloc(13);
    header.writeUInt32BE(SIZE, 0);
    header.writeUInt32BE(SIZE, 4);
    header[8] = 8;
    header[9] = 6;
    return Buffer.concat([
        Buffer.from([137, 80, 78, 71, 13, 10, 26, 10]),
        pngChunk("IHDR", header),
        pngChunk("IDAT", deflateSync(rawPixels, { level: 9 })),
        pngChunk("IEND", Buffer.alloc(0)),
    ]);
}

function verifyPng(png) {
    if (png.readUInt32BE(16) !== SIZE || png.readUInt32BE(20) !== SIZE) {
        throw new Error("Generated icon must be 512 by 512 pixels");
    }
    if (png[24] !== 8 || png[25] !== 6) {
        throw new Error("Generated icon must be an 8-bit RGBA PNG");
    }
    if (png.length >= PLAY_MAX_BYTES) {
        throw new Error(`Generated icon exceeds the ${PLAY_MAX_BYTES}-byte Play limit`);
    }
}

const output = parseOutput(process.argv.slice(2));
const png = encodePng(renderPixels());
verifyPng(png);
mkdirSync(dirname(output), { recursive: true });
writeFileSync(output, png);
console.log(`Generated ${output} (${png.length} bytes)`);
