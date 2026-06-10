# 五天 Google Play closed testing launch 执行计划

> 计划用于归档验收证据。该计划与 OpenSpec `five-day-google-play-closed-testing-launch` 的 tasks.md 对齐。

**目标：** 在五天窗口内把 WhiteNoisePro 推进到 Google Play closed testing 可上传的本地技术 ready 状态，并明确外部账号侧阻断项。

## 执行清单

- [x] 归档 `external-audio-source-intake` 并通过归档验收
- [x] 创建并验证 `five-day-google-play-closed-testing-launch` OpenSpec change
- [x] 为外部发布音频 manifest 和声音 id 覆盖添加测试
- [x] 实现可重复运行的外部音频晋升脚本
- [x] 处理 11 个 Freesound CC0 原始素材为 Android release Ogg 资源
- [x] 生成 `docs/audio-assets/external-release-audio-manifest.json`
- [x] 更新发布音频 QA 文档
- [x] 扩展 app catalog 到 19 个声音
- [x] 为 Android resolver 添加 19 个声音资源映射
- [x] 更新默认内容、sample mixes、icon mapping 和 resolver
- [x] 新增 GitHub Pages-ready 隐私政策 HTML
- [x] 更新 store listing、Play Console worksheet、Data safety 和 closed testing 文档
- [x] 做轻量 UI token、icon、card 和 spacing polish
- [x] 运行 Node audio/tool tests 和 Gradle unit tests
- [x] 运行 lint、signed release bundle、bundle verifier 和 release APK assemble
- [x] 在 `emulator-5554` 执行 release APK smoke 并记录截图/logcat/MediaSession 证据
- [x] 产出最终 readiness summary 和剩余外部 Play Console blockers

## RED / GREEN 记录

- RED：新增/更新 `tools/promote_external_audio.test.mjs`、`SoundCatalogTest`、`AndroidSoundResourceResolverTest`、`DesignTokenTest`，覆盖外部 manifest、19 个声音 catalog/resolver、UI token 和 icon coverage。
- GREEN：实现音频晋升脚本、11 个 release raw resources、catalog/resolver/sample content、隐私政策页面和轻量 UI token 后，目标测试通过。
- 修正：UI token 放宽后，`ScreenBottomWithPlayer` 聚合值从旧值同步到 194dp，并补断言保证未来与 bottom chrome 组成 token 一致。

## 验证记录

- `node --test tools/*.test.mjs`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:lintDebug`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:bundleRelease`
- `node tools/verify_release_bundle.mjs composeApp/build/outputs/bundle/release/composeApp-release.aab`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:assembleRelease`
- `adb -s emulator-5554 install -r composeApp/build/outputs/apk/release/composeApp-release.apk`
- `adb -s emulator-5554 shell am start -n com.whitenoisepro/.MainActivity`
- `adb -s emulator-5554 shell dumpsys media_session`
- `openspec validate five-day-google-play-closed-testing-launch --strict`
- `openspec validate --all --strict`
