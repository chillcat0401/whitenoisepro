# WhiteNoisePro 鸿蒙复刻总体规划(Kuikly 路线)

制定:2026-06-10 · 状态:规划完成,待执行 agent 落地
背景:Google Play 账号审核等待期的提前准备。技术路线由 ArkTS 原生重写改为
**Kuikly(Tencent-TDS/KuiklyUI)**——经调研,其 Compose DSL 在鸿蒙为一等公民,
现有 Compose 代码可近乎直译,且 domain/data/合成器等纯 Kotlin 层可原样复用,
复用率显著高于 ArkTS 重写。

## 0. 关键调研结论(执行 agent 必读)

| 事实 | 依据 | 对我们的意义 |
|---|---|---|
| Kuikly = KMP 框架,**原生组件映射渲染**(非自绘),鸿蒙正式版支持 | README / docs Introduction/arch | 性能与包体(so 级)适合工具 App |
| **Compose DSL 支持鸿蒙**,与官方 Compose API 基本一致,material3 可用 | docs /Compose/overview.html;demo/pages/compose/ 实例 | 现有 UI 翻译 = 改 import 包名 `androidx.compose.*` → `com.tencent.kuikly.compose.*` 为主 |
| 工程形态:业务 KMP 模块编译为 `libshared.so` + 头文件,装入 ohosApp(DevEco 壳工程) | 2.0_ohos_demo_build.sh;settings.2.0.ohos.gradle.kts | 我们在 KuiklyUI workspace 内加 `whitenoise` 业务模块,壳工程复用 ohosApp |
| 鸿蒙构建用专用文件 `build.2.0.ohos.gradle.kts`(每模块一份),Kotlin 锁 **2.0.21-KBA-010**,Gradle 8.0,AGP 7.4.2 | demo/build.2.0.ohos.gradle.kts(`ohosArm64 { binaries.sharedLib("shared") }`);构建脚本 | 版本不可乱升;mac 直接跑 `2.0_ohos_demo_build.sh` |
| 原生能力桥 = Module 机制:common 侧 `Module` 子类 + 鸿蒙侧 `KuiklyRenderBaseModule`(ArkTS,亦支持 C Module),`getCustomRenderModuleCreatorRegisterMap()` 注册 | docs /DevGuide/expand-native-api.html | 音频/存储/媒体会话都走这条路,骨架已建 |
| 环境:DevEco Studio 5.1.0+,HarmonyOS API ≥ 18(NEXT 5.0+),JDK 17 | README | 本机已有 DevEco(/Volumes/Volumes2T/DevEco) |
| 页面注册:`@Page("route") class X : ComposeContainer()` + KSP 生成入口 | demo/pages/compose/AppBarDemo.kt | 骨架 AppPage.kt 已按此范式 |

参考资料:
- 仓库:https://github.com/Tencent-TDS/KuiklyUI(本目录 KuiklyUI/ 为浅克隆工作区,已 gitignore)
- 文档:https://kuikly.tds.qq.com/(QuickStart/Compose/DevGuide/QA)
- 团队博客:https://kuikly.tds.qq.com/Blog/roadmap2025.html 、roadmap2026.html(架构演进与官方路线)
- 许可证:KuiklyUI License(KuiklyUI/LICENSE)——**M0 必须通读并确认商用分发条款**

## 1. 架构决策

```
kuikly2hmos/
├── KuiklyUI/            # 官方仓库浅克隆 = 构建工作区(gitignore,不入主仓)
│   └── (执行时把 biz/whitenoise 拷入/链接进来,加入 settings)
├── biz/whitenoise/      # 我们的业务 KMP 模块(入主仓,真源码所在)
│   ├── build.gradle.kts            # 自 demo 派生,已去 core-wx/ktor/markdown
│   ├── build.2.0.ohos.gradle.kts   # 自 demo 派生(ohosArm64 → libshared.so)
│   └── src/commonMain/.../kuikly/
│       ├── pages/AppPage.kt        # @Page("WhiteNoiseApp") 根页(M0 已建)
│       └── modules/                # AudioPlayer/Storage 桥 common 侧(已建)
├── REPLICATION_PLAN.md  # 本文件
└── PORTING_MAP.md       # 主仓 → 鸿蒙 文件级映射
```

分层策略(复用率从高到低):

1. **原样复用(纯 Kotlin,主仓直接拷贝)**:domain 全部、data 编解码与目录、
   NoiseSynthesizer、MixDice、PresetCatalog、BrandCopy、AppState/AppSnapshotCodec。
2. **小改复用**:AppStore(PlaybackEngine 接口背后换 Module 桥实现;协程 scope 由
   Pager 生命周期提供)、SoundCatalog(canonicalId 等逻辑不变)。
3. **翻译**(Compose → Kuikly Compose,机械为主):WnpTheme、Components、Screens、
   AppShell、Icons(Canvas 绘制 API 兼容性见风险 R2)。
4. **重写(平台层,对照 androidMain)**:播放引擎鸿蒙侧(AVPlayer×N + AVSession +
   长时任务)、存储(preferences)、合成缓存写盘(Kotlin/Native posix 直写,见 M2)。

## 2. 里程碑与任务(superpowers-bridge 三元组格式)

### M0 工作区打通(预计 0.5~1 天)

- [ ] 0.1 官方 demo 跑通鸿蒙
  - accept: `./2.0_ohos_demo_build.sh` 产出 libshared.so;DevEco 打开 ohosApp,
    签名后真机/模拟器运行 demo 页面
  - verify: 设备截图 + 构建日志
  - scope: kuikly2hmos/KuiklyUI(工作区,不提交)
- [ ] 0.2 whitenoise 模块接入工作区
  - accept: 把 biz/whitenoise 拷入 KuiklyUI/,settings.2.0.ohos.gradle.kts 增加
    `include(":whitenoise")` + buildFileName 行;构建脚本目标改为
    `:whitenoise:linkSharedDebugSharedOhosArm64`;ohosApp 启动显示 AppPage 骨架画面
  - verify: 设备截图(深底 + 「白噪声 Pro」);so 与头文件产物存在
  - scope: 工作区 + biz/whitenoise 构建文件修正(派生文件首次真实构建可能需微调,
    以 demo 同名文件 diff 为准)
- [ ] 0.3 许可证审查
  - accept: 通读 KuiklyUI LICENSE,输出商用分发结论与约束清单入本文件附录
  - verify: 附录章节存在且含结论
  - scope: 本文件

### M1 纯逻辑层移植(预计 1 天,几乎零风险)

- [ ] 1.1 按 PORTING_MAP「原样复用」清单拷贝源码与 commonTest
  - accept: domain/data/audio(synth)/presentation 编译通过;kotlinx-serialization
    与 coroutines 依赖在两份 gradle 中补齐(版本对齐 KuiklyUI workspace)
  - verify: `./gradlew :whitenoise:testDebugUnitTest`(androidTarget 单测)全绿
  - scope: biz/whitenoise/src/{commonMain,commonTest}
- [ ] 1.2 AppStore 适配:PlaybackEngine 实现切换为 Module 桥
  - accept: KuiklyPlaybackEngine 实现主仓 PlaybackEngine 接口,内部调
    AudioPlayerModule;音量乘法(layer×master×fade)保持在共享层;状态流语义不变
  - verify: 移植 AppStoreTest(Fake 引擎路径)全绿
  - scope: biz/whitenoise/src/commonMain

### M2 原生能力桥(预计 2~3 天,核心攻坚)

- [ ] 2.1 鸿蒙侧 AudioPlayerModule(ArkTS)
  - accept: AVPlayer 实例池(≤5 层,loop=true,资源为 rawfile 或沙箱文件路径);
    playLayers/pauseAll/stopAll/setLayerVolume/setLayerMuted 全实现;
    注册进 ohosApp 的 getCustomRenderModuleCreatorRegisterMap
  - verify: 测试页播放双层混音可听、调音量即时生效
  - scope: KuiklyUI/ohosApp/entry(执行期同步一份到 biz/ohos-snippets/ 入主仓)
- [ ] 2.2 后台播放与媒体会话
  - accept: AVSession(标题/播放态/播放暂停回调接 Module 反向通知)+
    backgroundTasks 长时任务 AUDIO_PLAYBACK(权限 ohos.permission.KEEP_BACKGROUND_RUNNING);
    熄屏 10 分钟播放不断;控制中心卡片可暂停/恢复(注意主仓踩过的坑:
    暂停后恢复的授权语义,见主仓 AudioFocusPlaybackGate 注释)
  - verify: 真机熄屏实测 + 控制中心操作闭环
  - scope: 同 2.1
- [ ] 2.3 噪声合成落盘(无桥方案优先)
  - accept: NoiseSynthesizer 输出经 Kotlin/Native `platform.posix` 直接写 wav 到
    应用沙箱(expect/actual:android 用 java.io,ohosArm64 用 posix);
    AVPlayer 以 fd/路径播放合成文件;自定义音色 noise_custom_t* 同路生效
  - verify: 测试页播放白/粉/棕噪 + 一个自定义音色
  - scope: biz/whitenoise/src/{commonMain,ohosArm64Main,androidMain}
- [ ] 2.4 StorageModule 鸿蒙侧(@ohos.data.preferences)
  - accept: AppSnapshot JSON 存取;杀进程重启状态恢复(含旧 soundId 迁移路径)
  - verify: 保存混音 → 杀进程 → 重启可见
  - scope: 同 2.1
- [ ] 2.5 音频资源进包
  - accept: 18 个 ogg 进 ohosApp rawfile(或 hap resources);资源名与
    soundId 解析约定写入共享层 expect/actual(对照主仓 AndroidSoundResourceResolver)
  - verify: 全 21 声音可播(18 资源 + 3 合成)
  - scope: ohosApp resources + biz 共享层

### M3 UI 移植(预计 2~3 天)

- [ ] 3.1 设计系统翻译:WnpTheme/Components(import 换包名;Slider 自定义 track、
  呼吸光晕动画、TimerProgressRing 逐一验证 Kuikly Compose 等价 API)
  - verify: 单页视觉对照截图(与主仓模拟器截图并排)
- [ ] 3.2 Icons:Canvas 绘制 API 验证;不支持则降级为 svg/png 资源(风险 R2 预案)
- [ ] 3.3 五页 + AppShell + 反馈浮层 + 迷你播放器(对照主仓 Screens/AppShell)
  - verify: 五页导航 + 播放全链路在真机跑通
- [ ] 3.4 字体:Lora + 文楷子集接入(Kuikly 字体注册方式查 docs API/字体章节;
  ttf 文件已在主仓 composeResources/font/ 可直接复制)
  - verify: 标题衬线/楷体、数字衬线与主仓一致

### M4 平台行为补全(预计 1~2 天)

- [ ] 4.1 睡眠定时器:SleepTimerDeadlineRunner 纯逻辑复用 + 长时任务内协程 tick;
  熄屏到点淡出停止
- [ ] 4.2 启动恢复(startLastMix)、通知卡片精简语义(只播放/暂停,对照主仓
  fix-notification-resume-card 的结论)
- [ ] 4.3 主仓验证矩阵全量回归(PROJECT_PROTOCOL.md 音频/状态/UI 三矩阵)

### M5 合规与上架(与开发并行启动)

- [ ] 5.1 软著/备案/华为开发者认证状态对账(主仓 docs/launch-and-monetization-roadmap.md 第 3 节)
- [ ] 5.2 AGC 材料:图标/截图/隐私政策(复用已发布页面)/权限清单
  (KEEP_BACKGROUND_RUNNING 需用途声明)
- [ ] 5.3 提审免费版(变现待企业资质,见路线图)

## 3. 风险登记

| # | 风险 | 概率 | 预案 |
|---|---|---|---|
| R1 | Kotlin 锁 2.0.21-KBA-010 与主仓 2.2.21 语法差异(如 `kotlin.time.Clock` 实验 API) | 中 | M1 移植时逐文件编译驱动修正;AppClock 已是接口可换实现 |
| R2 | Kuikly Compose 的 Canvas/drawBehind/InfiniteTransition 子集缺口(图标、呼吸光晕、环形进度) | 中 | 图标降级 svg 资源;光晕降级 alpha 动画;环形进度用分段图片;M3.1 首日先做 API 探测页 |
| R3 | 多 AVPlayer 并发混音的时延/资源上限 | 低中 | ≤5 层与主仓一致;必要时降级 OH AudioRenderer 混音(C Module) |
| R4 | KuiklyUI License 商用条款限制 | 低 | M0.3 前置审查,有问题立即上报用户决策 |
| R5 | so 体积(Compose DSL + 业务) | 低 | demo 实测基线;Kuikly 宣称 AOT 轻量;上架前 hap 体积核对 |
| R6 | ohosApp 壳的工程定制(包名/签名/图标)与官方 demo 耦合 | 低 | 壳工程改动清单单独记录,便于框架升级时重放 |

## 4. 执行约定(给落地 agent)

- 流程:遵循主仓 superpowers-bridge(openspec/config.yaml 加载链);本计划的
  M0~M5 即变更分解依据,每个 Mx 建一个 OpenSpec 变更,任务三元组照搬本文件。
- 工作区:KuiklyUI/ 克隆不入主仓;**ohosApp 壳工程的全部改动须同步快照到
  biz/ohos-snippets/**(入主仓,防工作区重建丢失)。
- 验证:鸿蒙模拟器/真机截图 + 构建日志为每任务证据,落
  docs/superpowers/evidence/<change-id>/。
- 主仓联动:共享层源码以主仓为单一事实源,移植采用「拷贝 + 版本注记」,
  暂不做 gradle 级共享(两边 Kotlin 版本不同,R1)。

## 附录 A:License 审查结论

(M0.3 填写)
