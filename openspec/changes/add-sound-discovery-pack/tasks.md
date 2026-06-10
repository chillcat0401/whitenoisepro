# Tasks

- [x] 1.1 场景预设目录 PresetCatalog(6 预设)+ 引用合法性测试
  - accept: 6 个 preset-* 混音,全部 soundId 在 SoundCatalog 中,音量∈(0,1]
  - verify: ./gradlew :composeApp:testDebugUnitTest --tests "*PresetCatalogTest"
  - scope: composeApp/src/commonMain/kotlin/com/whitenoisepro/data/, composeApp/src/commonTest/kotlin/com/whitenoisepro/data/

- [x] 1.2 MixIntent.ReplaceCurrentMix 归约 + 测试
  - accept: currentMix 替换、recentMixes 头部去重插入,与 PlaySavedMix 语义一致
  - verify: ./gradlew :composeApp:testDebugUnitTest --tests "*MixReducerTest"
  - scope: composeApp/src/commonMain/kotlin/com/whitenoisepro/domain/, composeApp/src/commonTest/kotlin/com/whitenoisepro/domain/

- [x] 1.3 AppIntent.PlayPresetMix + 首页「场景入睡」横滑区
  - accept: 点击预设卡 → currentMix 替换并开始播放;AppStoreTest 覆盖播放副作用
  - verify: ./gradlew :composeApp:testDebugUnitTest --tests "*AppStoreTest"
  - scope: composeApp/src/commonMain/kotlin/com/whitenoisepro/{presentation,app}/, composeApp/src/commonMain/kotlin/com/whitenoisepro/App.kt, composeApp/src/commonTest/

- [x] 2.1 MixDice 生成器 + 规则测试
  - accept: 固定种子可复现;1 底噪 + 1~2 纹理 + 0~1 点缀;名称为场景词「·」连接
  - verify: ./gradlew :composeApp:testDebugUnitTest --tests "*MixDiceTest"
  - scope: composeApp/src/commonMain/kotlin/com/whitenoisepro/domain/, composeApp/src/commonTest/kotlin/com/whitenoisepro/domain/

- [x] 2.2 AppIntent.RollDiceMix + 声音库骰子按钮(Dice 图标)
  - accept: 点击骰子 → 生成混音替换当前并播放;Store 注入 Random 可测
  - verify: ./gradlew :composeApp:testDebugUnitTest --tests "*AppStoreTest"
  - scope: composeApp/src/commonMain/kotlin/com/whitenoisepro/{presentation,app,design}/, composeApp/src/commonMain/kotlin/com/whitenoisepro/App.kt, composeApp/src/commonTest/

- [x] 3.1 NoiseSynthesizer 倾斜合成 + 自定义 soundId 编解码 + 测试
  - accept: parse/customSoundId 往返;tilt 增大时高频/低频能量比单调下降
  - verify: ./gradlew :composeApp:testDebugUnitTest --tests "*NoiseSynthesizerTest"
  - scope: composeApp/src/commonMain/kotlin/com/whitenoisepro/audio/, composeApp/src/commonTest/kotlin/com/whitenoisepro/audio/

- [x] 3.2 SoundSource/缓存/解析器支持自定义噪声 + 名称与图标回退
  - accept: noise_custom_t* 路由到合成缓存;nameOf 回退「自定义噪声」;Noise 图标
  - verify: ./gradlew :composeApp:testDebugUnitTest --tests "*AndroidSoundResourceResolverTest" --tests "*SoundCatalogTest"
  - scope: composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/, composeApp/src/commonMain/kotlin/com/whitenoisepro/{data,design}/, composeApp/src/commonTest/, composeApp/src/androidUnitTest/

- [x] 3.3 声音库「噪声实验室」卡(滑杆 + 加入混音)
  - accept: 滑杆 0~-6 dB/oct;加入后混音出现自定义层并可播放
  - verify: ./gradlew :composeApp:testDebugUnitTest
  - scope: composeApp/src/commonMain/kotlin/com/whitenoisepro/{app,design}/

- [x] 4.1 全量门禁 + 模拟器验证(三功能实操 + 截图)
  - accept: check 全绿;模拟器上预设/骰子/实验室全部出声
  - verify: ./gradlew :composeApp:check :composeApp:assembleDebug
  - scope: 全仓(只读验证)

## 5. 听审反馈修复(2026-06-10 真机听审发现)

- [x] 5.1 声音点击改为切换语义(已选再点 = 移除,杜绝重复层)
  - accept: 同一 soundId 重复点击在加入/移除间切换;不会产生重复层
  - verify: ./gradlew :composeApp:testDebugUnitTest --tests "*AppStoreTest"
  - scope: composeApp/src/commonMain/kotlin/com/whitenoisepro/presentation/, composeApp/src/commonMain/kotlin/com/whitenoisepro/{app,App.kt}, composeApp/src/commonTest/

- [ ] 5.2 全局操作反馈浮层(加入/移除/保存)
  - accept: 加入声音、移除声音、保存混音均出现 2 秒浮层提示并自动消失
  - verify: ./gradlew :composeApp:testDebugUnitTest --tests "*AppStoreTest"
  - scope: composeApp/src/commonMain/kotlin/com/whitenoisepro/{presentation,app}/, composeApp/src/commonMain/kotlin/com/whitenoisepro/App.kt, composeApp/src/commonTest/

- [ ] 5.3 混音页试听/暂停控件
  - accept: 混音页标题栏可直接播放/暂停当前混音
  - verify: ./gradlew :composeApp:testDebugUnitTest && 模拟器实操
  - scope: composeApp/src/commonMain/kotlin/com/whitenoisepro/{app,App.kt}
