# Tasks(L1:通知栏恢复播放失效 + 媒体卡片样式)

- [x] 1.1 门控支持"稳定暂停后的控制器播放意图"重新授权
  - accept: 通知暂停→通知播放可恢复声层;UI 暂停后 stale StartLayers 仍被拒
    (既有测试不回归);门控新增用例覆盖通知恢复路径
  - verify: ./gradlew :composeApp:testDebugUnitTest --tests "*AudioFocusPlaybackGateTest"
  - scope: composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/, composeApp/src/androidUnitTest/

- [x] 1.2 媒体卡片精简:隐藏进度条与跳转按钮
  - accept: 卡片只保留播放/暂停;无 30s 循环进度条来回跳;真机/模拟器核对
  - verify: ./gradlew :composeApp:check && 模拟器锁屏/通知栏截图
  - scope: composeApp/src/androidMain/kotlin/com/whitenoisepro/audio/
