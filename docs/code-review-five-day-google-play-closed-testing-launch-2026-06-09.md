# 代码审查：五天 Google Play closed testing launch

**变更：** `five-day-google-play-closed-testing-launch`

**审查日期：** 2026-06-09

**审查范围：** 外部音频晋升脚本、11 个 release 音频资源、声音 catalog/resolver、隐私政策页面、release readiness 文档、轻量 UI polish 和 release 构建/模拟器 smoke 证据。

## 结论

未发现阻断 closed testing 本地技术 ready 的代码问题。signed AAB、bundle verifier、unit tests、lint、release APK assemble 和模拟器 smoke 均通过。

## 关注点

- 外部原始音频没有直接入包；release 资源来自 `tools/promote_external_audio.mjs` 处理后的 Ogg 文件，并有 processed hash、loudness、loop、人听 QA 和来源证据。
- `SoundCatalog` 和 `AndroidSoundResourceResolver` 已覆盖 19 个发布 sound id，未知 id 继续 fallback 到 `brown_noise_loop`。
- UI polish 没有改变导航结构或核心工作流；底部 mini player 与 bottom nav 的 padding token 已同步到新高度。
- 隐私政策页面仍含开发者信息占位；这是 Play Console 提交前的 owner input blocker，不应被误标为 ready。
- 模拟器 smoke 证明 release build 可安装、启动、导航和进入 PLAYING MediaSession；它不能替代真实设备听感、蓝牙、锁屏和打断测试。

## 验证证据

- `node --test tools/*.test.mjs`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:lintDebug`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:bundleRelease`
- `node tools/verify_release_bundle.mjs composeApp/build/outputs/bundle/release/composeApp-release.aab`
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:assembleRelease`
- `docs/release-readiness/release-candidate-2026-06-09.md`
- `docs/release-readiness/five-day-closed-testing-readiness-summary-2026-06-09.md`

## 剩余风险

- Play Console 账号侧信息无法从本地仓库验证。
- 真实设备音频 QA 尚未完成。
- closed testing 12+ tester / 14 天运营证据尚未开始。
