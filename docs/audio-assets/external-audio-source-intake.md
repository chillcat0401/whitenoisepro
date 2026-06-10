# External Audio Source Intake

状态：外部音频来源调研、Freesound CC0 首批种子抓取与取证。当前不表示可发布入包。

## 结论

内部程序化生成适合工具验证和占位，但不适合作为最终睡眠音频质量来源。外部真实录音应作为下一阶段主路线。

## 渠道分级

| 渠道 | 结论 | 使用条件 |
| --- | --- | --- |
| Freesound CC0 | 优先 | 仅选单条页面明确为 Creative Commons 0 的文件；保存 URL、作者、license、原始 hash |
| Sonniss GDC Bundle | 推荐备选 | 可用于商业交互项目；需要下载 bundle 后筛选自然环境长录音并保存 EULA |
| Pixabay / Signature Sounds / BigSoundBank / OpenGameArt | 可补充 | 逐条保存 license 页面；避免 standalone 分发风险；优先 CC0 |
| BBC Sound Effects | 默认禁用 | 免费 RemArc 不适合商业 app；只有购买商业授权后才可进入 release intake |

## Seed Candidate Shortlist

| 类别 | 候选 | 来源 | License | 状态 | 备注 |
| --- | --- | --- | --- | --- | --- |
| 小雨 | Soft rain.WAV | Freesound `640655` | CC0 | 人工听感通过，待处理晋升 | 花园小雨；后续处理仍需控制高频疲劳 |
| 小雨/屋顶 | Light rain on roof | Freesound `669484` | CC0 | 人工听感通过，待处理晋升 | 轻雨/近雪落在金属屋顶；后续处理需保留柔和感 |
| 窗雨 | Rain on window (interior) | Freesound `669486` | CC0 | 人工听感通过，待处理晋升 | 室内闭窗视角；需裁剪/淡入淡出处理 loop 边缘 |
| 屋顶雨 | Rain on roof 0001.wav | Freesound `650428` | CC0 | 人工听感通过，待处理晋升 | 10 分钟塑料屋顶雨；后续需留足峰值余量 |
| 海浪 | Gentle Ocean Waves Mix (2018) | Freesound `417797` | CC0 | 人工听感通过，待处理晋升 | 约 12 分钟海浪主素材候选；后续需检查低频和静音段 |
| 海浪 | GentleWaves.wav | Freesound `431853` | CC0 | 人工听感通过，待处理晋升 | 约 5 分钟柔和海浪备选；后续需控制低频隆隆声 |
| 海浪 | Calm_Seashore.wav | Freesound `278982` | CC0 | 人工听感通过，待处理晋升 | 短海岸声音；需裁剪/淡入淡出处理 loop 边缘 |
| 火炉 | Crackling Flames (loop) | Freesound `813328` | CC0 | 人工听感通过，待处理晋升 | 页面声明可无缝 loop；后续需控制近距 crackle 峰值 |
| 火炉 | Hearthfire (Louder) | Freesound `836535` | CC0 | 人工听感通过，待处理晋升 | 通过短链接解析到正确作者页；后续仍需保留 derivative/composite 证据 |
| 风扇 | Large Floor Fan - Steady Deep Hum - 2 Minutes | Freesound `843484` | CC0 | 人工听感通过，待处理晋升 | 稳定深层 floor fan hum；后续需修 loop 边界和峰值余量 |
| 风/环境 | Ambiance_Wind_Forest_Trees_Loop_01.wav | Freesound `530908` | CC0 | 人工听感通过，待处理晋升 | 可作轻风底层；后续需保留人工听感通过记录 |
| 雨滴/屋面 | drops rain close-up on yurt | Freesound `736845` | CC0 | 仅取证，暂不下载 | 12 分钟近距雨滴，可能过于敲击，听测前不拉入 originals |
| 小雨 | Light Rain SFX | Signature Sounds | CC0 | 待下载 | 24-bit WAV light rain pack，适合作小雨种子 |
| 小雨/远雷 | Light Rain Distant Thunder July 5th 2016.wav | Wikimedia Commons / Freesound source | CC0 | 待下载 | 有远雷和鸟声，需判断是否影响睡眠场景 |
| 窗雨 | Rain on Window Loop | OpenGameArt | CC-BY 3.0 | 待评估 | 约 11 秒 loop；需要署名，低于 CC0 优先级 |
| 海浪 | Calm ocean waves / tide / shingle beach candidates | Freesound IDs from oceansoundsforsleeping.com license page | CC0 | 待逐条打开原 Freesound 页面 | 可作海浪多层种子 |
| 火炉 | Fireplace #5 | BigSoundBank | CC0 | 待下载 | 45 秒 24-bit stereo，适合火炉候选 |
| 火炉 | Burning fireplace | directory.audio | CC0 | 待登录下载 | 37 秒 32-bit stereo，需确认原始来源 |
| 火炉 | crackling fire 021012 | Freesound mirror / original source | CC0 | 待打开原 Freesound 页面 | 3 分钟立体声，适合长底噪 |
| 风/环境 | Wind loop | Freesound ID 530908 via oceansoundsforsleeping.com | CC0 | 待打开原 Freesound 页面 | 可作轻风底层 |

## Source URLs To Open First

- Freesound license guide: `https://freesound.org/help/faq/`
- Signature Sounds Light Rain SFX: `https://signaturesounds.org/store/p/light-rain-sfx`
- Signature Sounds Light Rain Recordings: `https://signaturesounds.org/store/p/light-rain-recordings`
- Wikimedia / Freesound light rain distant thunder: `https://commons.wikimedia.org/wiki/File:Light_Rain_Distant_Thunder_July_5th_2016.wav`
- OpenGameArt Rain on Window Loop: `https://opengameart.org/content/rain-on-window-loop`
- Freesound Gentle Ocean Waves Mix: `https://freesound.org/s/417797/`
- Freesound first intake manifest: `work/audio-intake/intake-manifest.json`
- Sonniss GDC Bundle license: `https://sonniss.com/gdc-bundle-license`
- Sonniss GameAudioGDC archive: `https://sonniss.com/gameaudiogdc/`
- Directory.audio Burning fireplace: `https://directory.audio/sound-effects/household/32830-burning-fireplace`
- Ocean Sounds for Sleeping license/source index: `https://oceansoundsforsleeping.com/licensing/`

## Intake Evidence Fields

每条音频下载后必须记录：

- source channel
- source URL
- author/provider
- license name and license URL
- commercial-use decision
- downloaded filename
- original SHA-256
- original duration/sample rate/bit depth/channels
- processing steps
- processed SHA-256
- human listening QA status
- loop QA status
- loudness QA status
- release decision

## Freesound Intake Run 2026-06-09

- 登录账号：`ino0601`
- 原始文件目录：`work/audio-intake/originals/`
- 页面证据目录：`work/audio-intake/evidence/`
- Manifest：`work/audio-intake/intake-manifest.json`
- 已下载 CC0 原始素材:18 个(11 个首批 + 雷雨 + 6 个扩展品类,全部含许可证快照)
- 原始文件总大小：约 420.8 MB
- Git 策略：`work/audio-intake/` 已加入 `.gitignore`，原始音频和 intake JSON 不提交仓库

首批文件已由项目负责人确认听感可以满足种子素材要求。它们仍不是可直接发布资源，必须完成循环测试、响度测试、包体评估和处理后取证后，才能通过单独的发布音频晋升变更进入 app 资源。

## Listening QA Run 2026-06-09

- 听测页：`work/audio-intake/audition/audition.html`
- 报告：`work/audio-intake/audition/listening-qa-report.md`
- 预览片段：每个已下载文件生成 `start`、`mid`、`end`、`seam` 四段 m4a
- 可视证据：每个已下载文件生成 waveform 与前 90 秒 spectrogram
- Manifest QA：`work/audio-intake/intake-manifest.json` 已更新 `machinePrecheck`、`machineFlags`、`machineWarnings`

- 人工听测确认：项目负责人用户确认“素材验证可用，听感可以满足”
- 结论边界：听感通过只表示可进入处理/晋升候选池，不表示原始 WAV 可直接发布入包

机器预筛不能替代人工主观听测。本轮已记录人工听感通过，但没有把任何素材标记为可发布，只把明显风险保留给后续处理和发布晋升决策。

## Next Action

1. 另开发布音频晋升变更，选择首批 4-6 个核心类别素材。
2. 对入选候选做裁剪、淡入淡出、loop QA、响度 QA、频谱/低频检查和包体评估。
3. 记录处理步骤、处理后 hash、发布文件名、Android raw/压缩格式决策后再进入 app。
4. 保留 Signature Sounds、Sonniss、BigSoundBank 作为第二批补源渠道。

## API 自动化拉取(2026-06-10 起替代浏览器自动化)

工具:`tools/fetch_freesound_audio.mjs`(Node 18+,Freesound 官方 APIv2)。

- 凭据:在 https://freesound.org/apiv2/apply 申请,设置环境变量 `FREESOUND_CLIENT_ID`、`FREESOUND_API_KEY`;首次运行 `node tools/fetch_freesound_audio.mjs auth` 完成 OAuth 授权(token 缓存于 `work/audio-intake/freesound-oauth.json`,已被 .gitignore 忽略)。
- 搜索:`node tools/fetch_freesound_audio.mjs search "rain on roof" --min-duration 30`,服务端强制 `license:"Creative Commons 0"` 过滤,从源头排除 CC-BY/NC。
- 拉取:`node tools/fetch_freesound_audio.mjs fetch <id> --as <targetId> [--transcode] [--bitrate 64k]`:
  - 原始文件 → `work/audio-intake/originals/`;
  - 许可证快照(完整 API 元数据 + SHA-256 + 拉取时间 + 来源 URL)→ `work/audio-intake/metadata/<targetId>__freesound-<id>.json`,即本文档要求的取证记录,无需再人工截图;
  - `--transcode` 直接产出 `res/raw/<targetId>_loop.ogg`(Opus,默认 64kbps);
  - 非 CC0 素材工具直接拒绝。
- 拉取后仍需人工完成:目录/解析器注册、`external-release-audio-manifest.json` 登记、真机 loop 接缝听测。
