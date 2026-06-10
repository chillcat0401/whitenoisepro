# 代码评审:library-tap-auto-preview(2026-06-10)

范围:单提交,AppStore.ToggleSound 加入分支 +4 行 + 1 个测试。

- 行为缺陷:无。hasActivePlayRequest 判定复用 togglePlayback 同一私有属性,
  Buffering 状态不会重复拉起;移除分支不受影响。
- 测试:空闲加入自动播放/播放内容含新层/暂停后移除不拉起,三态覆盖;
  既有 addSoundWhilePlaying 同步测试不回归。
- 风险:夜间静默编排混音的用户会被自动出声打扰——产品决策上接受
  (与预设/骰子行为一致,迷你播放器一键可停)。

结论:通过。
