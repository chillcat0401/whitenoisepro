# Audio Source Channels Research

日期：2026-06-08

## Recommendation

当前 closed testing 候选版本已将 Freesound CC0 种子素材处理为本地打包资源。若下一轮继续扩充真实录音素材，优先选择：

1. 付费买断 / 明确允许 app 内分发的商业音频库。
2. Freesound 中明确为 CC0 的单条声音。
3. 经过逐条许可核对的 Pixabay / Sonniss 素材。

任何第三方素材都必须进入 asset intake 流程：记录来源 URL、下载日期、作者、许可证、许可证快照、文件 hash、编辑步骤、loop QA、人工听测结论，并确认不违反“standalone audio redistribution / soundboard / relaxation product primary value”限制。

## Channel Matrix

| 渠道 | 可用性 | 适合 WhiteNoisePro 的判断 | 关键风险 |
| --- | --- | --- | --- |
| First-party procedural generation | high | 当前最佳，版权链最干净，可复现 | 声音真实感有限 |
| Paid bespoke recording / commissioned sound design | high | 最适合正式上架和长期品牌 | 需要预算和合同 |
| Freesound CC0 | medium-high | 可作为候选；只选 CC0，避免 attribution 和 NC 混入 | 需要逐条核验，质量和版权声明由上传者提供 |
| Pixabay audio | medium | 可候选，但要保留下载证明和许可证摘要 | 不是 public domain；需避免 standalone 分发风险和第三方 claim |
| Sonniss GameAudioGDC | medium | 商业项目可用，但更偏游戏/影视 SFX；适合补充 ambience，不一定适合助眠 loop | 大包筛选成本高，AI/ML 禁止，仍需确认 app 内分发边界 |
| Soundly | medium | 商业可用，适合有账号/订阅时筛选 | 需要遵守其服务和库授权，保留下载证据 |
| ZapSplat Standard License | low for this app | 不建议作为助眠声音主素材 | 标准许可禁止把声音作为 relaxation/soundboard 类独立产品主要价值 |
| BBC Sound Effects free archive | low | 不建议直接用于上架包 | 免费 archive 商业使用需另购授权 |
| YouTube / random free SFX sites | avoid | 不建议 | 权属和授权证据不可控 |

## Download / License Links

| 渠道 | 链接 | 使用建议 |
| --- | --- | --- |
| Freesound FAQ / licenses | `https://freesound.org/help/faq/` | 搜索时只筛 CC0；如使用 CC-BY，必须设计 attribution 露出位置 |
| Freesound search | `https://freesound.org/search/` | 逐条记录 sound page、license、author、download date |
| Pixabay Content License | `https://pixabay.com/service/license/` | 可筛 sound effects，但要保存 license certificate / source page |
| Sonniss GameAudioGDC archive | `https://sonniss.com/gameaudiogdc/` | 可下载大包后筛 ambience；不要用于 AI/ML training |
| Sonniss GDC license | `https://sonniss.com/gdc-bundle-license` | 核对 commercial use、no attribution、raw redistribution 限制 |
| Soundly commercial FAQ | `https://getsoundly.com/faq/how-can-i-use-the-sounds/` | 适合订阅筛选；需保存账户/下载/许可证证据 |
| ZapSplat Standard License | `https://www.zapsplat.com/license-type/standard-license/` | 不建议用于 WhiteNoisePro 主声音，因 relaxation/soundboard primary value 风险 |
| BBC Sound Effects licensing | `https://sound-effects.bbcrewind.co.uk/licensing` | 免费 archive 不直接用于商业 app；商业使用需购买授权 |

## Suggested Intake Rules

- 只接受 WAV / high-quality source，统一转码前记录原始 hash。
- 每个候选声音必须能形成 30 秒以上无明显断点的 loop，或明确作为一次性 SFX 而非助眠底噪。
- 不接受 `CC-BY-NC`、不可商用、仅个人用途、仅教育/研究用途素材。
- `CC-BY` 素材只有在 App 内、商店详情或第三方声明页能提供稳定 attribution 时才可用；closed testing 首轮建议不使用。
- 对 relaxation / soundboard / standalone audio app 有禁止条款的渠道，不进入发布包。
- 每次新增素材必须更新 `docs/audio-assets/generated-audio-manifest.json` 或新建第三方 asset manifest。

## Candidate Workflow

1. 建立候选清单，不下载或不打包任何 license 未清晰的素材。
2. 对每条素材保存来源页面和许可证页面 URL。
3. 下载原始文件后记录 SHA-256、格式、长度、采样率、声道、响度、peak、loop seam QA。
4. 人工听测至少覆盖手机扬声器、耳机和低音量。
5. 只有通过许可和 QA 的素材才允许进入 `res/raw`。

## Source Notes

- Freesound：可筛选 Free Cultural Works approved licenses，包括 CC0 和 CC-BY；Freesound FAQ 明确不同 Creative Commons 许可证会带来 attribution 或非商业限制。
- Pixabay：提供 sound effects 并使用 Pixabay Content License；FAQ 表示素材通常可商业使用且无需 attribution，但仍要保留下载链接、文件名和许可证证明。
- Sonniss GameAudioGDC：官方页面表示 bundle 可用于商业项目且无需 attribution，但授权面向 media production，并禁止 AI/ML training。
- ZapSplat：标准许可允许商业项目，但禁止把素材作为 relaxation video、soundboard 或类似独立产品的主要价值；因此不适合作为 WhiteNoisePro 主声音库来源。
- BBC Sound Effects：免费 archive 的商业使用需要通过 Pro Sound Effects 购买授权；不能直接按免费 archive 打包进商业 app。
