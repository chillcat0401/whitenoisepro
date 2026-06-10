# 主仓 → Kuikly 鸿蒙 文件级移植映射

图例:♻️ 原样复用(拷贝即用) / 🔧 小改复用 / 🔁 翻译(换 import 包名为主) / 🆕 平台重写 / ⛔ 不移植

## commonMain(共享层)

| 主仓文件(composeApp/src/commonMain/.../whitenoisepro/) | 策略 | 说明 |
|---|---|---|
| domain/model/SoundModels.kt | ♻️ | 纯数据 |
| domain/reducer/MixReducer.kt(含 ReplaceCurrentMix) | ♻️ | 纯函数 |
| domain/reducer/TimerReducer.kt | ♻️ | 纯函数 |
| domain/MixDice.kt | ♻️ | kotlin.random 可用 |
| data/SoundCatalog.kt(canonicalId/nameOf) | ♻️ | 21 声音 + 旧 ID 迁移原样生效 |
| data/PresetCatalog.kt | ♻️ | 6 场景预设 |
| data/SampleContent.kt | ♻️ | |
| data/AppSnapshotCodec.kt | ♻️ | kotlinx-serialization 依赖需在模块 gradle 补 |
| data/AppRepository.kt | 🔧 | AppStorage 接口背后换 StorageModule 桥 |
| audio/NoiseSynthesizer.kt(含倾斜/自定义音色) | ♻️ | 纯计算;落盘走 expect/actual(M2.3) |
| presentation/AppState.kt(含 UiFeedback) | ♻️ | |
| presentation/AppStore.kt | 🔧 | scope 来源、PlaybackEngine 注入桥实现;逻辑不动 |
| presentation/BrandCopy.kt | ♻️ | |
| presentation/SettingsContent.kt | 🔧 | 隐私文案换鸿蒙语境(权限名不同) |
| playback/PlaybackEngine.kt(接口)/TimerPlaybackCoordinator/SleepTimerDeadlineRunner | ♻️ | 接口与纯逻辑 |
| playback/PlatformSleepTimerRuntime.kt | 🆕 | 鸿蒙长时任务内 tick(M4.1) |
| app/AppShell.kt(五 Tab/迷你播放器/反馈浮层) | 🔁 | Compose→Kuikly Compose;TopBar/BottomNav 结构不变 |
| app/Screens.kt(五页 + 全部私有组件) | 🔁 | 同上;OutlinedTextField/Slider slot API 需逐个验证 |
| design/WnpTheme.kt(色彩/字阶/间距/WnpMotion/WnpFonts) | 🔁 | 字体加载方式按 Kuikly 文档替换(M3.4) |
| design/Components.kt(按钮/SoundIcon 呼吸光晕/滑杆/输入框/环形进度) | 🔁 | 动画与 Canvas 兼容性 = 风险 R2,先做探测页 |
| design/Icons.kt(线性图标 Canvas DSL) | 🔁/降级 | R2:不兼容则导出 svg 资源替代 |

## commonTest(随源码同迁)

| 主仓测试 | 策略 |
|---|---|
| MixReducerTest / TimerReducerTest / DomainModelTest / MixDiceTest | ♻️ |
| SoundCatalogTest / PresetCatalogTest / AppSnapshotCodecTest / AppRepositoryTest | ♻️ |
| NoiseSynthesizerTest | ♻️ |
| AppStoreTest | 🔧(Fake 引擎原样,Random/Clock 注入不变) |
| DesignTokenTest / AppShellTest / SettingsContentTest | 🔁(随翻译层调整断言) |
| PlaybackEngineTest / TimerPlaybackCoordinatorTest / SleepTimerDeadlineRunnerTest | ♻️ |

## androidMain → 鸿蒙侧(全部 🆕,对照重写)

| 主仓 Android 实现 | 鸿蒙对应物 | 落点 |
|---|---|---|
| AndroidPlaybackEngine(ExoPlayer×N + 音量乘法) | 乘法留共享层;AVPlayer×N 在 ArkTS Module | KuiklyPlaybackEngine(common)+ AudioPlayerModule(ohosApp) |
| WhiteNoiseMediaSessionService + addSession + ForwardingPlayer 卡片精简 | AVSession + 长时任务;卡片仅播放/暂停 | ohosApp entry(快照同步 biz/ohos-snippets/) |
| AudioFocusPlaybackGate(含「稳定暂停后重新授权」修复) | 同语义移植(音频焦点 = OH InterruptEvent) | 共享层可复用大部分 |
| AndroidSoundResourceResolver(SoundSource 三态) | rawfile 路径 / 合成沙箱文件路径解析 | expect/actual |
| SynthesizedSoundCache(filesDir 预热) | posix 直写沙箱 + 预热协程 | ohosArm64Main |
| AndroidDataStoreAppStorage | @ohos.data.preferences | StorageModule |
| MainActivity(POST_NOTIFICATIONS 请求) | 长时任务权限声明 + AGC 用途说明,无运行时弹窗等价物 | ohosApp module.json5 |
| res/raw/*.ogg ×18 | ohosApp rawfile | 资源拷贝 |
| 字体 composeResources/font(lora×2 + 文楷子集) | Kuikly 字体注册 | M3.4 |

## ⛔ 不移植

- Media3/ExoPlayer 相关全部;POST_NOTIFICATIONS 流程;Google Play 发布文档;
  generate_mvp_audio 等工具链(主仓继续唯一维护)。
