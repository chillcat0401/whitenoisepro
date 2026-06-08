# WhiteNoisePro 第三方产品评审

日期：2026-06-05

## 评审立场

本评审将 WhiteNoisePro 视为一个 Android 优先的白噪音与助眠声音应用早期 MVP。当前目标不只是再做一个工具型应用，而是要在体验、审美、稳定性和信任感上超过一批长期缺乏维护、UI 明显过时的竞品。首期验证目标是 Google Play，后续需要保留进入中国大陆市场，尤其是华为 / HarmonyOS 渠道的可能性，但不应因此拖慢第一版 Android 验证。

本文刻意采用第三方产品与上架准备视角。本文只做审视和补强建议，不修改代码。

## 背景与目标

WhiteNoisePro 当前定位围绕以下方向：

- 低干扰的睡眠、放松与专注音频。
- 现代化深色 UI，并具备中文优先的视觉和文案方向。
- 稳定的长时间播放能力，包括后台播放、锁屏播放和定时器行为。
- 先通过 Google Play 验证 Android MVP。
- 后续探索中国大陆 / 华为 / HarmonyOS 分发路径。

当前仓库已经具备较扎实的工程基础：

- 使用 Compose Multiplatform，并以 Android 作为首个生产目标。
- 使用单一 `:composeApp` 模块，同时区分 common 与 Android 专属 source set。
- 已有领域模型和 reducer 风格的状态转换。
- 已定义 common 层 `PlaybackEngine` 边界，并有 Android Media3 / ExoPlayer 实现。
- 已有 MediaSessionService 后台播放验证记录。
- 已有 Home、Mixer、Library、Timer、Saved Mixes、Settings 等深色 MVP 页面。
- 已有 Android 权限、隐私、Google Play 和 HarmonyOS 相关合规说明。

因此，这个项目已经不再只是脚手架。它是一个可运行的工程 MVP 候选，但还不是一个足以验证市场、并有把握击败老旧竞品的产品 MVP。

## 当前产物评估

### 已经做得比较好的部分

技术方向是可信的。代码将 common 领域 / UI 概念与 Android 音频实现分开，架构文档也明确要求 common 代码不能依赖 Android 和 Media3。对白噪音应用来说，这是关键，因为这类产品最终成败很大程度取决于播放稳定性。

领域层 reducer 风格也值得保留。混音和定时器行为是确定性的、可测试的，这让后续添加功能时不必把业务逻辑塞进 composable 函数里。

UI 方向已经比通用 Material 样例更接近现代助眠应用。Mixer 和 Timer 截图呈现出克制的深色界面、固定 Mini Player、紧凑底部导航和中文文案，方向与目标品类基本一致。

Android 构建已经通过本地 Gradle 验证，后台播放也通过 MediaSession 状态做过手动验证。对于这个产品类型，这比只完成 UI 更有价值。

### 还不具备竞争力的部分

视觉系统目前是统一的，但还不够有记忆点。当前页面更像一个干净的工程 MVP：卡片、滑块、标签和按钮都能用，但产品还没有形成鲜明的感官身份。要赢过长期未维护的竞品，WhiteNoisePro 可以很快在信任感和精致度上拉开差距，但前提是补强第一印象：更有质感的 Now Playing 区域、更好的声音浏览、更完整的空状态 / 加载状态，以及更高质量的音频内容呈现。

当前 `home-390.png` 截图看起来是 Android 启动或占位画面，而不是完整 Home UI。这会削弱视觉验收和商店截图准备的可信度。用于验收和上架素材的截图必须展示真实产品状态。

音频管线目前用一个生成的 1 秒低音量 `silence_loop.wav` 做验证。它证明的是播放管线可用，而不是产品音质可用。对白噪音应用来说，无缝 loop、响度一致性、声音命名、分类和授权都是核心产品内容。如果没有真实可用的 loop 音频资产，就还无法与竞品做有效比较。

Settings 页面有隐私政策、恢复购买、离线下载、音质和主题等入口，但不少仍是 placeholder。内部 MVP 可以存在 placeholder，但如果要对外测试或上架，就必须明确它们是禁用、隐藏，还是已经有真实行为和文案支撑。

架构上已经有 MVI-like reducer，但 app shell 仍在 `App.kt` 里直接维护 Compose state。这个做法对静态 MVP 可以接受，但随着恢复状态、播放副作用、定时器 tick、通知回调和 UI 测试增加，它会变得难以推理。

## Google Play MVP 缺口

以下是认真尝试 Google Play 验证前的主要缺口。

### 1. 缺少真实闭测计划

Google Play 对部分新的个人开发者账号有额外测试要求，可能需要完成一定规模和时长的 closed testing 后才能申请生产发布权限。是否适用取决于账号类型和 Play Console 当前提示，因此发布计划必须预留测试人员招募、反馈收集、版本迭代和商店资料准备时间。

建议下一步：建立 release-readiness checklist，覆盖 internal、closed、open 和 production track。不要把测试轨道当作纯行政工作，它会直接影响产品验证节奏。

### 2. 隐私政策和 Data safety 还未闭环

当前架构说明没有 analytics、账号、广告或云同步，这是很好的隐私定位。但 Google Play 仍需要 Data safety 表单与实际代码行为一致；在满足政策和应用行为要求时，也需要公开可访问的隐私政策 URL。

建议下一步：在 closed testing 前发布最小可用隐私政策。内容应明确说明是否收集个人数据、声音偏好是否只本地保存、是否没有崩溃日志 / analytics，以及用户如何联系支持。

### 3. 通知权限和后台播放说明还不完整

当前 Manifest 使用了：

- `FOREGROUND_SERVICE`
- `FOREGROUND_SERVICE_MEDIA_PLAYBACK`
- `POST_NOTIFICATIONS`
- `foregroundServiceType="mediaPlayback"`

这与后台音频方向一致，但用户侧权限叙事仍不完整。助眠应用应该解释通知权限的用途：锁屏 / 后台媒体控制，而不是营销通知。

建议下一步：补充 Android 13+ 通知权限的产品文案和测试场景。应用不应在用户理解收益前就请求通知权限。

### 4. 商店 listing 素材还不够

当前截图适合工程验收，但不适合作为最终商店素材。Google Play 截图需要展示真实且有吸引力的状态：漂亮的 Home / Now Playing、带真实声音名称的 Mixer、带淡出设置的 Timer、多分类 Library、以及有隐私 / 信任感的 Settings。

建议下一步：在真实声音资产和上架文案完成后，单独制作商店截图集。不要直接把 emulator debug 截图作为最终营销素材。

### 5. 真实声音资产和授权缺口

项目当前用 silence loop 验证播放管线，但这不是可发布声音目录。白噪音竞品的核心体验很大程度取决于音质。循环处有 click、响度突变、命名混乱或内容薄弱，即使 UI 更好，也会伤害留存。

建议下一步：制定 MVP 声音资产验收标准：

- loop 边界无明显 click。
- 不同声音之间主观响度一致。
- 授权明确覆盖 Google Play 和中国大陆分发。
- 包体大小可控，不显著影响安装转化。
- 至少有一个优秀默认混音，能在前 10 秒体现产品价值。

## 中国大陆 / 华为 / HarmonyOS 缺口

HarmonyOS 应继续作为 spike 路径，而不是第一版 MVP 上架门槛。当前 `docs/harmonyos-spike-plan.md` 方向正确：先验证打包、一个页面、本地音频和后台行为，再决定是否投入。

但更大的问题不是 Harmony 技术兼容，而是中国大陆分发准备。

### 1. 合规表面还不够完整

进入中国大陆分发前，需要准备：

- 中文隐私政策。
- 中文权限说明。
- 支持 / 联系 URL。
- 可覆盖中国大陆分发的音频资产授权。
- 判断托管的隐私政策 / 支持页面是否触发 ICP / APP 备案相关要求。
- 单独审查 Huawei AppGallery 要求，而不是复用 Google Play 清单。

建议下一步：将大陆市场准备拆成一个独立 future spike，覆盖法律 / 合规、商店政策和技术打包。不要把它只看成 ovCompose 或 HAP 构建任务。

### 2. shared core 不应依赖 Google

当前架构没有在 shared MVP 逻辑中依赖 Google Play Services，这一点应该继续坚持。如果未来加入 analytics、billing、crash reporting 或 review prompt，需要通过平台边界封装，并保留华为替代方案。

建议下一步：把“shared core 不依赖 Google-only 服务”写成后续变更的明确架构规则。

### 3. HarmonyOS 技术未知数

主要技术未知数包括：

- Compose Multiplatform 通过 ovCompose 类路径适配的兼容性。
- HAP 打包稳定性。
- 后台音频 API 和审核预期。
- 通知 / 媒体控制支持。
- 音频资产打包和包体大小。

建议下一步：HarmonyOS 只在 Google Play MVP 验证后排期，或作为严格 time-boxed spike，达到截止条件就停。

## 产品竞争力缺口

要打败老旧竞品，MVP 不能只停留在“能用且是深色界面”。产品需要少量但非常优秀、情绪明确的体验。

### 1. 首次使用体验

当前还没有清晰的 first-run promise。用户打开助眠应用时，应该立刻知道今晚该做什么。

建议下一步：设计一个极简 first-run 路径：一个默认混音、一个主播放动作、一个推荐定时器。除非 onboarding 能直接帮助用户开始播放，否则不要做多页引导。

### 2. Now Playing 身份感

Mini Player 很有用，但主 Now Playing 区域应该成为产品情绪中心。老旧竞品通常偏工具化，WhiteNoisePro 可以通过更平静、更高级、更可信的当前混音体验拉开差距。

建议下一步：围绕一个优秀默认混音重构 Home / Now Playing，加入轻量视觉反馈、清晰定时器状态和低亮度舒适度。

### 3. 声音发现

Library 当前更像功能列表或网格。它需要更清晰的分类和声音 affordance：什么正在播放、什么可叠加、什么是未来高级功能、什么适合睡眠 / 专注。

建议下一步：定义声音和混音分类体系：

- 睡眠
- 专注
- 环境遮噪
- 自然
- 风扇 / 房间底噪
- 雨声 / 雷雨

### 4. 信任与平静感

Settings 中有信任相关入口，但目前多为占位。对白噪音应用来说，如果业务目标允许，“无广告、无账号、可离线、隐私友好”本身就是竞争优势。

建议下一步：明确隐私 / 信任定位，并同步到 Settings、Google Play listing copy 和首次使用文案中。

### 5. 与老旧竞品的差异化

近期最有价值的差异化不是堆功能，而是：

- 更好的睡眠优先深色 UI。
- 真实无缝音频 loop。
- 可靠后台播放。
- 用户可以信任的定时器和淡出。
- 没有吵闹广告或操纵性付费墙。
- 中文可用的文案和后续分发策略。

## 架构与工程缺口

当前架构足够支撑 MVP，但若继续扩展功能，以下问题会逐步放大。

### 1. App state 所有权

当前 app shell 直接拥有并修改 `AppState`。这对静态 MVP 可以接受，但生产级助眠应用需要让 playback、timer tick、persistence、restore、notification callback 和 UI events 走一条一致的 state/effect 路径。

建议下一步：在增加更多行为前引入小型 `AppStore` 或 `AppViewModel` 概念。它应对外暴露 state，接受 app-level intents，并将纯状态转换委托给 reducer。

### 2. Use case 层

当前已有 domain reducer 和 data repository，但没有明确 use case 层。完整 Clean Architecture 通常会有 play mix、save mix、restore session、start timer、apply fade、stop at end 等 application use cases。

建议下一步：只在 use case 能真正降低复杂度时再添加，不要为每个简单动作制造仪式化层级。

### 3. 持久化真实性

仓储抽象已经存在，但真实存储行为还早。Saved mixes、last mix、timer defaults、settings 和音质偏好都需要真实持久化，用户才会信任产品。

建议下一步：把 restore last session 作为发布前 gate，因为这是助眠应用的核心体验。

### 4. 音频可靠性测试

当前验证证明了 MediaSession play/pause 状态，但还没有覆盖：

- 长 loop 稳定性。
- 多播放器 CPU / 电量成本。
- audio focus 行为。
- 蓝牙 / 耳机控制。
- 播放被打断后的恢复。
- 使用真实音频时的定时器淡出正确性。
- 真实设备上的锁屏行为。

建议下一步：创建手动音频 QA 矩阵，并在 closed testing 前至少用一台真实 Android 设备跑完。

## 建议的下一步补强顺序

### Phase A：产品 MVP 硬化

1. 用少量已授权真实音频目录替换 silence test loop。
2. 让 Home / Now Playing 成为产品旗舰界面。
3. 将核心交互接入一致的 state path。
4. 实现 last mix、saved mixes、timer defaults 和 settings 的真实持久化。
5. 将 placeholder Settings 项改为可用功能、明确禁用态或暂时隐藏。

### Phase B：Google Play 准备

1. 发布隐私政策和支持 URL。
2. 按实际行为完成 Data safety 答案。
3. 准备通知权限解释文案。
4. 准备 closed testing 计划和 tester feedback loop。
5. 准备商店 listing 文案和精选截图。
6. 跑真实设备播放 QA。

### Phase C：竞争力打磨

1. 将视觉身份从“通用深色 Material”升级为“睡眠优先的平静体验”。
2. 添加高质量默认混音和场景文案。
3. 改善声音发现和 active layer 反馈。
4. 只在能降低用户不确定性的位置加入轻量动效 / 反馈。
5. 如果符合商业目标，建立“隐私友好、无账号、无杂音”的定位。

### Phase D：中国大陆 / HarmonyOS Spike

1. 确认中国大陆合规和备案要求。
2. 确认 Huawei AppGallery 政策要求。
3. 保持 shared logic 不依赖 Google-only 服务。
4. time-box ovCompose / Harmony 打包 spike。
5. 决定继续 Compose-based Harmony 路径，还是规划原生 ArkUI adapter。

## Closed Testing 前建议验收门槛

- 真实音频目录替换 silence loop。
- Privacy Policy row 打开已发布 URL。
- 通知权限有用户可理解的解释文案。
- 后台播放在真实 Android 设备上可用。
- 使用真实音频时，timer stop / fade 行为可用。
- App 重启后能恢复 last mix。
- 商店截图展示真实应用状态，而不是 splash / debug placeholder。
- Data safety 答案与实际代码行为一致。
- 购买 / 离线功能 placeholder 不以可用功能的方式出现。
- 至少一个默认混音足以在前 10 秒展示产品价值。

## 已核验的外部参考

- Google Play 新个人开发者账号 closed testing 要求：https://support.google.com/googleplay/android-developer/answer/14151465?hl=en
- Google Play User Data policy 与隐私要求：https://support.google.com/googleplay/android-developer/answer/10144311?hl=en
- Google Play Data safety form 指南：https://support.google.com/googleplay/android-developer/answer/10787469?hl=en
- Google Play 隐私政策要求：https://support.google.com/googleplay/android-developer/answer/9888076
- Google Play target API level 要求：https://developer.android.com/google/play/requirements/target-sdk
- Android foreground service types，包括 media playback：https://developer.android.com/develop/background-work/services/fgs/service-types
- Android Media3 background playback：https://developer.android.com/media/media3/session/background-playback
- Android core app quality guidelines：https://developer.android.com/docs/quality-guidelines/core-app-quality
- Huawei Developer policy center：https://developer.huawei.com/consumer/en/policy-center/
- MIIT ICP / APP 备案入口：https://beian.miit.gov.cn/
