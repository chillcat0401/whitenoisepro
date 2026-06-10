# Tasks(L1:听审交互反馈——声音库点击即试听)

- [x] 1.1 加入声音时若播放空闲则自动开始播放(移除不触发)
  - accept: 空闲状态点未选声音 → 混音含新层并开始播放;点已选声音移除不拉起播放;
    与预设/骰子的"点击即发声"行为一致
  - verify: ./gradlew :composeApp:testDebugUnitTest --tests "*AppStoreTest"
  - scope: composeApp/src/commonMain/kotlin/com/whitenoisepro/presentation/, composeApp/src/commonTest/
