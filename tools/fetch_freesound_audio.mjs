#!/usr/bin/env node
/**
 * Freesound 官方 APIv2 素材拉取工具,替代浏览器自动化下载。
 *
 * 凭据(https://freesound.org/apiv2/apply 申请):
 *   FREESOUND_CLIENT_ID  — OAuth client id
 *   FREESOUND_API_KEY    — API key(同时作为 token 认证的 token 与 OAuth client secret)
 *
 * 用法:
 *   node tools/fetch_freesound_audio.mjs auth
 *       首次运行:打印授权 URL,粘贴回跳 code,换取并缓存 OAuth token
 *       (存于 work/audio-intake/freesound-oauth.json,该目录已被 .gitignore 忽略)。
 *
 *   node tools/fetch_freesound_audio.mjs search "rain on roof" [--min-duration 30] [--max-duration 180] [--page 1]
 *       仅搜索 CC0 素材,输出候选表(id/名称/作者/时长/评分/下载数/格式)。
 *
 *   node tools/fetch_freesound_audio.mjs fetch <freesoundId> --as <targetId> [--transcode] [--bitrate 64k]
 *       下载原始文件到 work/audio-intake/originals/,
 *       写许可证快照(完整 API 元数据 + SHA-256 + 拉取时间)到 work/audio-intake/metadata/,
 *       --transcode 时用 ffmpeg 转 Opus 写入 composeApp/src/androidMain/res/raw/<targetId>_loop.ogg。
 *       非 CC0 素材直接拒绝。
 */
import { createHash } from "node:crypto";
import { mkdir, readFile, writeFile } from "node:fs/promises";
import { spawnSync } from "node:child_process";
import { createInterface } from "node:readline/promises";
import { join } from "node:path";

const root = new URL("../", import.meta.url).pathname;
const intakeDir = join(root, "work/audio-intake");
const originalsDir = join(intakeDir, "originals");
const metadataDir = join(intakeDir, "metadata");
const rawDir = join(root, "composeApp/src/androidMain/res/raw");
const tokenPath = join(intakeDir, "freesound-oauth.json");

const apiBase = "https://freesound.org/apiv2";
const cc0 = "Creative Commons 0";

const clientId = process.env.FREESOUND_CLIENT_ID ?? "";
const apiKey = process.env.FREESOUND_API_KEY ?? "";

function fail(message) {
  console.error(`✗ ${message}`);
  process.exit(1);
}

function requireApiKey() {
  if (!apiKey) fail("缺少 FREESOUND_API_KEY 环境变量");
}

function parseFlags(args) {
  const flags = {};
  const positional = [];
  for (let i = 0; i < args.length; i++) {
    if (args[i].startsWith("--")) {
      const key = args[i].slice(2);
      const next = args[i + 1];
      if (next !== undefined && !next.startsWith("--")) {
        flags[key] = next;
        i++;
      } else {
        flags[key] = true;
      }
    } else {
      positional.push(args[i]);
    }
  }
  return { flags, positional };
}

async function tokenRequest(form) {
  const response = await fetch(`${apiBase}/oauth2/access_token/`, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: new URLSearchParams({
      client_id: clientId,
      client_secret: apiKey,
      ...form,
    }),
  });
  if (!response.ok) {
    fail(`OAuth token 请求失败 (${response.status}): ${await response.text()}`);
  }
  return response.json();
}

async function saveTokens(tokens) {
  await mkdir(intakeDir, { recursive: true });
  const record = {
    ...tokens,
    obtained_at: new Date().toISOString(),
    expires_at_epoch_ms: Date.now() + (tokens.expires_in ?? 0) * 1000,
  };
  await writeFile(tokenPath, JSON.stringify(record, null, 2));
  return record;
}

async function loadTokens() {
  try {
    return JSON.parse(await readFile(tokenPath, "utf8"));
  } catch {
    return null;
  }
}

async function accessToken() {
  if (!clientId) fail("缺少 FREESOUND_CLIENT_ID 环境变量");
  requireApiKey();
  let tokens = await loadTokens();
  if (!tokens) fail("尚未授权,请先运行: node tools/fetch_freesound_audio.mjs auth");
  if (Date.now() > (tokens.expires_at_epoch_ms ?? 0) - 60_000) {
    console.log("· access token 已过期,刷新中…");
    tokens = await saveTokens(
      await tokenRequest({ grant_type: "refresh_token", refresh_token: tokens.refresh_token }),
    );
  }
  return tokens.access_token;
}

async function commandAuth() {
  if (!clientId) fail("缺少 FREESOUND_CLIENT_ID 环境变量");
  requireApiKey();
  const authorizeUrl = `${apiBase}/oauth2/authorize/?client_id=${encodeURIComponent(clientId)}&response_type=code`;
  console.log("1. 浏览器打开并授权:\n\n   " + authorizeUrl + "\n");
  console.log("2. 授权后页面会显示 code(或回跳 URL 的 code 参数)。\n");
  const rl = createInterface({ input: process.stdin, output: process.stdout });
  const code = (await rl.question("粘贴 code: ")).trim();
  rl.close();
  if (!code) fail("code 为空");
  await saveTokens(await tokenRequest({ grant_type: "authorization_code", code }));
  console.log(`✓ OAuth token 已保存到 ${tokenPath}`);
}

async function commandSearch(args) {
  requireApiKey();
  const { flags, positional } = parseFlags(args);
  const query = positional.join(" ").trim();
  if (!query) fail('用法: search "rain on roof" [--min-duration 30] [--max-duration 180]');

  const min = flags["min-duration"] ?? "20";
  const max = flags["max-duration"] ?? "*";
  const filter = `license:"${cc0}" duration:[${min} TO ${max}]`;
  const fields = "id,name,username,license,duration,filesize,type,samplerate,channels,avg_rating,num_downloads";
  const url =
    `${apiBase}/search/text/?` +
    new URLSearchParams({
      query,
      filter,
      fields,
      sort: "downloads_desc",
      page_size: "15",
      page: String(flags.page ?? 1),
      token: apiKey,
    });

  const response = await fetch(url);
  if (!response.ok) fail(`搜索失败 (${response.status}): ${await response.text()}`);
  const data = await response.json();

  console.log(`共 ${data.count} 条 CC0 结果(第 ${flags.page ?? 1} 页,按下载数排序):\n`);
  for (const s of data.results ?? []) {
    const mb = (s.filesize / 1024 / 1024).toFixed(1);
    console.log(
      `  #${s.id}  ${s.name}\n` +
        `      作者 ${s.username} · ${s.duration.toFixed(0)}s · ${s.type} ${mb}MB · ` +
        `${s.samplerate}Hz/${s.channels}ch · 评分 ${s.avg_rating?.toFixed(1) ?? "-"} · 下载 ${s.num_downloads}`,
    );
  }
  console.log('\n下一步: node tools/fetch_freesound_audio.mjs fetch <id> --as <targetId> [--transcode]');
}

async function commandFetch(args) {
  requireApiKey();
  const { flags, positional } = parseFlags(args);
  const soundId = positional[0];
  const targetId = flags.as;
  if (!soundId || !targetId) fail("用法: fetch <freesoundId> --as <targetId> [--transcode] [--bitrate 64k]");
  if (!/^[a-z0-9_]+$/.test(targetId)) fail("targetId 只允许小写字母、数字、下划线(将用作资源名)");

  const detailResponse = await fetch(`${apiBase}/sounds/${soundId}/?token=${apiKey}`);
  if (!detailResponse.ok) fail(`获取元数据失败 (${detailResponse.status})`);
  const detail = await detailResponse.json();

  const isCc0 = /^https?:\/\/creativecommons\.org\/publicdomain\/zero\//.test(detail.license ?? "");
  if (!isCc0) {
    fail(`许可证不是 CC0,拒绝拉取: ${detail.license}\n  (intake 规则只接受 CC0,见 docs/release-readiness/audio-source-channels.md)`);
  }

  console.log(`· #${detail.id} ${detail.name} — ${detail.username} · ${detail.duration.toFixed(1)}s · ${detail.type}`);

  const token = await accessToken();
  console.log("· 下载原始文件…");
  const download = await fetch(`${apiBase}/sounds/${soundId}/download/`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  if (!download.ok) fail(`下载失败 (${download.status}): ${await download.text()}`);
  const bytes = Buffer.from(await download.arrayBuffer());
  const sha256 = createHash("sha256").update(bytes).digest("hex");

  await mkdir(originalsDir, { recursive: true });
  await mkdir(metadataDir, { recursive: true });
  const safeName = detail.name.replace(/[^A-Za-z0-9._-]+/g, "-").slice(0, 80);
  const originalPath = join(originalsDir, `${detail.id}__${detail.username}__${safeName}.${detail.type}`);
  await writeFile(originalPath, bytes);
  console.log(`✓ 原始文件 ${originalPath} (${(bytes.length / 1024 / 1024).toFixed(1)}MB)`);

  const snapshot = {
    targetId,
    fetchedAt: new Date().toISOString(),
    fetchedVia: "freesound-apiv2",
    sha256,
    bytes: bytes.length,
    originalPath: originalPath.replace(root, ""),
    soundPageUrl: detail.url,
    license: detail.license,
    licenseName: cc0,
    author: detail.username,
    freesound: detail,
  };
  const snapshotPath = join(metadataDir, `${targetId}__freesound-${detail.id}.json`);
  await writeFile(snapshotPath, JSON.stringify(snapshot, null, 2));
  console.log(`✓ 许可证快照 ${snapshotPath}`);

  if (flags.transcode) {
    const bitrate = typeof flags.bitrate === "string" ? flags.bitrate : "64k";
    const outPath = join(rawDir, `${targetId}_loop.ogg`);
    console.log(`· ffmpeg 转码 Opus ${bitrate} → ${outPath}`);
    const result = spawnSync(
      "ffmpeg",
      ["-y", "-i", originalPath, "-c:a", "libopus", "-b:a", bitrate, "-vn", outPath],
      { stdio: ["ignore", "ignore", "pipe"] },
    );
    if (result.status !== 0) {
      fail(`ffmpeg 失败:\n${result.stderr?.toString().split("\n").slice(-8).join("\n")}`);
    }
    console.log("✓ 转码完成。后续手动步骤:");
    console.log("  1. SoundCatalog / AndroidSoundResourceResolver 注册新 id");
    console.log("  2. docs/audio-assets/external-release-audio-manifest.json 登记");
    console.log("  3. 真机 loop 接缝听测(docs/release-readiness/audio-asset-qa.md)");
  }
}

const [command, ...rest] = process.argv.slice(2);
switch (command) {
  case "auth":
    await commandAuth();
    break;
  case "search":
    await commandSearch(rest);
    break;
  case "fetch":
    await commandFetch(rest);
    break;
  default:
    console.log("子命令: auth | search | fetch(用法见文件头注释)");
    process.exit(command ? 1 : 0);
}
