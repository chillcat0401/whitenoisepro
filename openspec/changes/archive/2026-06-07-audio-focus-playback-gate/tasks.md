# Tasks

## 1. Controller 决策策略

- [x] 1.1 添加 Start/Await/Pause 红灯测试
- [x] 1.2 实现 controller playback decision 纯策略
- [x] 1.3 运行 Android policy 单测

## 2. Layer 播放闸门

- [x] 2.1 添加 pending play、focus await、pause/stop 防恢复红灯测试
- [x] 2.2 engine play 改为仅 prepare + Buffering
- [x] 2.3 controller Start/Await/Pause 驱动 layer 和 PlaybackState
- [x] 2.4 验证 mute、timer fade 和多 layer 回归

## 3. MediaSession 与 AppStore

- [x] 3.1 listener 覆盖 isPlaying、playWhenReady、suppression 和 playbackState
- [x] 3.2 play callback 不屏蔽，pause/stop 保留防递归保护
- [x] 3.3 添加 AppStore 非乐观播放红灯测试并移除手动 `isPlaying=true`
- [x] 3.4 运行 common/Android 目标测试和 debug APK

## 4. 上架验证与归档

- [x] 4.1 更新 Android 12/15 焦点验证文档
- [x] 4.2 全量测试、lint、APK、签名 AAB、Node 与音频资产验证
- [x] 4.3 代码审查并修复发现
- [x] 4.4 strict validate 并归档 change
