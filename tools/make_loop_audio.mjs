#!/usr/bin/env node
/**
 * 把原始环境录音处理为可无缝循环的发布素材(rain_thunder 流程的工具化)。
 *
 * 步骤:包络分析自动选安静切点 → 响度对齐(目标 RMS -30.5dB,峰值 ≤ -2dB)
 *       → 2s 等功率交叉淡化接缝 → libopus 64k 编码。
 *
 * 用法:
 *   node tools/make_loop_audio.mjs <input> <targetId> [--max-len 150] [--start N --end N] [--bitrate 64k]
 *
 * 输出:composeApp/src/androidMain/res/raw/<targetId>_loop.ogg + 处理参数 JSON(stdout)
 */
import { spawnSync } from "node:child_process";
import { join } from "node:path";

const root = new URL("../", import.meta.url).pathname;
const rawDir = join(root, "composeApp/src/androidMain/res/raw");

const TARGET_RMS_DB = -30.5;
const PEAK_CAP_DB = -2.0;
const CROSSFADE_S = 2.0;
const ANALYZE_SR = 8000;
const WIN_S = 0.5;

function fail(msg) {
  console.error(`✗ ${msg}`);
  process.exit(1);
}

function run(args, opts = {}) {
  const r = spawnSync("ffmpeg", args, { maxBuffer: 1 << 28, ...opts });
  if (r.status !== 0 && !opts.allowFail) {
    fail(`ffmpeg ${args.slice(0, 6).join(" ")}… 失败:\n${r.stderr?.toString().split("\n").slice(-6).join("\n")}`);
  }
  return r;
}

function decodeMono(input) {
  const r = run(["-v", "error", "-i", input, "-f", "s16le", "-ac", "1", "-ar", String(ANALYZE_SR), "-"], {
    stdio: ["ignore", "pipe", "pipe"],
  });
  return r.stdout;
}

function rmsWindows(pcm) {
  const win = ANALYZE_SR * WIN_S;
  const samples = Math.floor(pcm.length / 2);
  const out = [];
  for (let w = 0; w + win <= samples; w += win) {
    let sum = 0;
    let count = 0;
    for (let i = w; i < w + win; i += 4) {
      const v = pcm.readInt16LE(i * 2) / 32768;
      sum += v * v;
      count++;
    }
    out.push(Math.sqrt(sum / count));
  }
  return out;
}

function db(x) {
  return x > 0 ? 20 * Math.log10(x) : -120;
}

// 在窗口序列上找安静且电平互相匹配的 [start, end] 切点(秒)。
function pickCutPoints(windows, durationS, maxLenS) {
  const dbs = windows.map(db);
  const median = [...dbs].sort((a, b) => a - b)[Math.floor(dbs.length / 2)];
  const smooth = dbs.map((_, i) => {
    const lo = Math.max(0, i - 3);
    const hi = Math.min(dbs.length, i + 4);
    return dbs.slice(lo, hi).reduce((a, b) => a + b, 0) / (hi - lo);
  });
  const calm = (i) => Math.abs(smooth[i] - median) <= 2.5;

  let startIdx = Math.floor(2 / WIN_S);
  while (startIdx < smooth.length / 3 && !calm(startIdx)) startIdx++;

  const latestEndS = Math.min(durationS - CROSSFADE_S - 0.5, startIdx * WIN_S + maxLenS);
  let endIdx = Math.floor(latestEndS / WIN_S);
  while (
    endIdx > startIdx + Math.floor(10 / WIN_S) &&
    !(calm(endIdx) && Math.abs(smooth[endIdx] - smooth[startIdx]) <= 2)
  ) {
    endIdx--;
  }
  const start = startIdx * WIN_S;
  const end = endIdx * WIN_S;
  if (end - start < 20) fail(`可用循环段太短 (${(end - start).toFixed(1)}s),请手动指定 --start/--end`);
  return { start, end, medianDb: median, startDb: smooth[startIdx], endDb: smooth[endIdx] };
}

function measure(input, start, end) {
  const r = run(
    ["-v", "info", "-i", input, "-af", `atrim=start=${start}:end=${end + CROSSFADE_S},astats`, "-f", "null", "-"],
    { stdio: ["ignore", "ignore", "pipe"], allowFail: true },
  );
  const text = r.stderr.toString();
  const overall = text.slice(text.lastIndexOf("Overall"));
  const peak = parseFloat(overall.match(/Peak level dB:\s*(-?[\d.]+)/)?.[1]);
  const rms = parseFloat(overall.match(/RMS level dB:\s*(-?[\d.]+)/)?.[1]);
  if (!Number.isFinite(peak) || !Number.isFinite(rms)) fail("astats 解析失败");
  return { peak, rms };
}

function probeDuration(input) {
  const r = spawnSync("ffprobe", ["-v", "quiet", "-show_entries", "format=duration", "-of", "csv=p=0", input]);
  const d = parseFloat(r.stdout.toString().trim());
  if (!Number.isFinite(d)) fail("ffprobe 时长解析失败");
  return d;
}

const args = process.argv.slice(2);
const positional = args.filter((a) => !a.startsWith("--"));
const flag = (name, fallback) => {
  const i = args.indexOf(`--${name}`);
  return i >= 0 ? args[i + 1] : fallback;
};
const [input, targetId] = positional;
if (!input || !targetId) fail("用法: make_loop_audio.mjs <input> <targetId> [--max-len 150]");
if (!/^[a-z0-9_]+$/.test(targetId)) fail("targetId 只允许小写字母、数字、下划线");

const duration = probeDuration(input);
const maxLen = parseFloat(flag("max-len", "150"));
let start;
let end;
let picked = null;
if (flag("start") !== undefined && flag("end") !== undefined) {
  start = parseFloat(flag("start"));
  end = parseFloat(flag("end"));
} else {
  picked = pickCutPoints(rmsWindows(decodeMono(input)), duration, maxLen);
  start = picked.start;
  end = picked.end;
}

const { peak, rms } = measure(input, start, end);
const gain = Math.min(TARGET_RMS_DB - rms, PEAK_CAP_DB - peak);
const loopLen = end - start;
const outPath = join(rawDir, `${targetId}_loop.ogg`);
const bitrate = flag("bitrate", "64k");

run([
  "-y",
  "-v",
  "error",
  "-i",
  input,
  "-filter_complex",
  `[0:a]atrim=start=${start}:end=${end + CROSSFADE_S},asetpts=PTS-STARTPTS,volume=${gain.toFixed(2)}dB[seg];` +
    `[seg]asplit[s1][s2];` +
    `[s1]atrim=end=${loopLen},asetpts=PTS-STARTPTS,afade=t=in:d=${CROSSFADE_S}:curve=qsin[body];` +
    `[s2]atrim=start=${loopLen},asetpts=PTS-STARTPTS,afade=t=out:d=${CROSSFADE_S}:curve=qsin[tail];` +
    `[body][tail]amix=inputs=2:duration=first:normalize=0[out]`,
  "-map",
  "[out]",
  "-c:a",
  "libopus",
  "-b:a",
  bitrate,
  outPath,
]);

const final = measure(outPath, 0, loopLen - CROSSFADE_S);
console.log(
  JSON.stringify(
    {
      targetId,
      outputFile: `${targetId}_loop.ogg`,
      sourceDurationS: +duration.toFixed(1),
      cut: { start, end, loopLenS: +loopLen.toFixed(1), auto: picked !== null },
      gainDb: +gain.toFixed(2),
      result: { peakDbfs: +final.peak.toFixed(2), rmsDbfs: +final.rms.toFixed(2) },
    },
    null,
    2,
  ),
);
