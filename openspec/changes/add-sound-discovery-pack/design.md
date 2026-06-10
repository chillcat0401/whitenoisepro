# 设计

## 数据与领域层

- `data/PresetCatalog.kt`:6 个 `SoundMix`(id 前缀 `preset-`),只引用已发布 soundId。
- `domain/reducer/MixReducer.kt`:新增 `MixIntent.ReplaceCurrentMix(mix, nowEpochMillis)` —
  currentMix 替换 + 写入 recentMixes 头部(语义对齐 PlaySavedMix)。
- `domain/MixDice.kt`:`roll(random: Random): SoundMix`。规则:1 底噪(噪声/风扇/机舱)
  + 1~2 主纹理(雨/海/溪/火/列车/咖啡馆)+ 0~1 点缀(虫鸣/风),音量在各声音
  defaultVolume ±0.08 抖动;名称 = 场景词映射去重后以「·」连接(如「雨夜·炉边」)。
- `audio/NoiseSynthesizer.kt`:自定义音色 —
  - soundId 约定:`noise_custom_t<XX>`,XX = 倾斜 ×10(0~60),如 t15 = -1.5dB/oct;
  - `customSoundId(tilt)` / `parseCustomSoundId(id): Float?`;
  - 合成:单次 RNG 流同步产生 white/pink/brown 三路,按倾斜分段等功率混合
    (t≤3 混 white+pink,t>3 混 pink+brown),复用现有归一化与交叉淡化。

## 状态层(AppStore)

- `AppIntent.PlayPresetMix(presetId)` → ReplaceCurrentMix + playbackEngine.play。
- `AppIntent.RollDiceMix` → MixDice.roll(injected Random) → ReplaceCurrentMix + play。
- 构造函数注入 `random: Random = Random.Default`(测试用种子)。
- `SoundCatalog.nameOf` 对 `noise_custom_*` 回退「自定义噪声」;`soundIconKind`
  前缀匹配返回 Noise 图标。

## Android 层

- `SoundSource.Synthesized` 改为携带 `soundId: String`(不再绑定枚举),
  `SynthesizedSoundCache.ensureFile(context, soundId)` 内部解析 profile 或自定义倾斜,
  缓存文件名 `<soundId>_loop_v<N>.wav`。未知 id 兜底 `brown_noise` 不变。

## UI 层

- 首页:`BedtimeTimerCallout` 与「最近使用」之间插入「场景入睡」横滑行
  (SoundIcon + 名称 + 成分摘要,点击 = PlayPresetMix)。
- 声音库:TopBar action 增加骰子 IconButton(AppIconKind.Dice 新图标);
  搜索框下方「噪声实验室」卡:滑杆(明亮 0 ↔ 深沉 -6)+「加入混音」。

## 模块作用域表

| 区域 | 允许路径 |
|---|---|
| 数据/领域 | composeApp/src/commonMain/kotlin/com/whitenoisepro/{data,domain}/ |
| 合成 | composeApp/src/commonMain/kotlin/com/whitenoisepro/audio/ |
| 状态 | composeApp/src/commonMain/kotlin/com/whitenoisepro/presentation/ |
| UI | composeApp/src/commonMain/kotlin/com/whitenoisepro/{app,design}/ + App.kt |
| Android | composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/ |
| 测试 | composeApp/src/commonTest/**, composeApp/src/androidUnitTest/** |

## 测试策略

预设目录引用合法性;ReplaceCurrentMix 语义;骰子规则(层数/分类/确定性种子/命名);
倾斜合成频谱单调性(高低频能量比随 tilt 单调)与 id 解析往返;解析器自定义路由;
AppStore 两个新 intent 的播放副作用。
