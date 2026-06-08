# Tasks

## 1. 播放状态流

- [x] 1.1 添加 fake engine 状态流红灯测试，验证 play/pause/stop 发射最新状态
- [x] 1.2 用 `MutableStateFlow` 实现 common fake 和 Android engine 状态源
- [x] 1.3 运行 common 与 Android 播放相关单测

## 2. AppStore 系统状态同步

- [x] 2.1 添加外部 pause/resume 更新 `AppStore.state.isPlaying` 的红灯测试
- [x] 2.2 初始化完成后用协程收集 `PlaybackEngine.states`
- [x] 2.3 验证恢复、timer 和用户 TogglePlayback 回归

## 3. Android 音频焦点策略

- [x] 3.1 添加 controller/layer audio focus 与 becoming-noisy 策略红灯测试
- [x] 3.2 controller player 配置 media attributes、自动焦点和 becoming noisy
- [x] 3.3 layer players 配置 media attributes，但不独立管理焦点
- [x] 3.4 运行 Android 单测并编译 debug APK

## 4. 文档、审查与归档

- [x] 4.1 更新 Android 播放验证文档和真实设备检查项
- [x] 4.2 运行完整测试、lint、debug APK、release AAB、签名与资产验证
- [x] 4.3 完成代码审查并修复发现
- [x] 4.4 strict validate 后归档 OpenSpec change
