# Code Review: App 状态、交互与持久化闭环

日期：2026-06-06

## 范围

本次审查覆盖 `app-state-interaction-persistence` 变更：

- `AppStore` / `AppIntent`。
- App snapshot restore/save。
- Home、Mixer、Library、Timer、Saved Mixes、Settings 的核心 callback 接线。
- `docs/compliance-readiness.md` 状态同步。

## 结论

未发现阻塞问题。

## 已修复的问题

### 播放中修改 mix 后 PlaybackEngine 未同步

审查时发现：如果用户正在播放时添加声音，`AppState` 会更新，但播放引擎不会拿到新的完整 mix。

处理：

- 添加测试 `addSoundWhilePlayingSendsUpdatedMixToPlaybackEngine`。
- 让 `AppStore` 在播放中处理 `AddSound` 后重新调用 `playbackEngine.play(updatedMix)`。
- 保持保存、收藏、音量类 intent 不触发完整 replay，只执行各自副作用。

## 剩余风险

- 默认 repository 仍是内存 fake storage；真实跨进程/重启持久化需要后续 Android storage 实现或平台注入。
- Timer fade slider 已改为只读说明；真实 fade 配置仍需单独规格化。
- Library 分类 UI 当前只展示前几个分类；当前 catalog 主要声音仍可访问，后续声音分类扩展时应改成横向滚动或多行 chips。
- 添加声音时只保证 state 与 playback 同步；真实多层音频质量仍依赖后续真实资产和 Media3 多层播放 QA。

## 验证

- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:testDebugUnitTest --tests 'com.whitenoisepro.presentation.AppStoreTest'`：通过。
- `JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home ./gradlew :composeApp:check :composeApp:assembleDebug`：通过。

