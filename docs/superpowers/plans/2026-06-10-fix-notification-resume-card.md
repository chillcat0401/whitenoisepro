# 实施计划:fix-notification-resume-card(L1)

- [x] 1.1 AudioFocusPlaybackGate 增加 controllerSettledPaused 状态:稳定暂停后的
      播放决策视为用户新意图重新授权;cancelPlay 清状态保住 stale 拒绝语义
      (verify: GateTest 5 用例 + 模拟器媒体键闭环, evidence: 1.1.md)
- [x] 1.2 会话播放器包 ForwardingPlayer 隐藏 seek/跳转/时长(evidence: 1.2.md)
