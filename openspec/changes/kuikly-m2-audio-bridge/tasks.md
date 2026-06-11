# Tasks(L2:鸿蒙复刻 M2——音频桥,源自 REPLICATION_PLAN)

- [x] 2.1 ArkTS AudioPlayerModule + 首个可听声音
  - accept: AVPlayer 实例池(loop/音量/静音/暂停/停止);两个 rawfile 测试声源;
    AppPage 测试按钮触发双层混音播放;hilog 显示 AVPlayer playing 且无错误
  - verify: 重编 so + hap → 模拟器实操 + hilog 证据
  - scope: ohosApp(快照 biz/ohos-snippets), biz/whitenoise/src/commonMain
- [x] 2.2 AVSession 媒体会话 + 长时任务后台播放(2026-06-11 完成,见 evidence)
- [ ] 2.3 噪声合成 posix 落盘(后续)
- [ ] 2.4 StorageModule 鸿蒙侧(后续)
- [ ] 2.5 全量音频资源进包(后续)
