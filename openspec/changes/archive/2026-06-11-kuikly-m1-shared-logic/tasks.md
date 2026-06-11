# Tasks(L1:鸿蒙复刻 M1——共享逻辑层移植,源自 REPLICATION_PLAN)

- [x] 1.1 纯逻辑层 + commonTest 移植并双端验证
  - accept: domain/data/synth/presentation/playback 35+ 文件移植;
    鸿蒙 K/N 编译通过;Android 单测全绿
  - verify: sh biz/build_ohos.sh(BUILD SUCCESSFUL)&&
    :whitenoise:testDebugUnitTest(94 tests, 0 failed)
  - scope: kuikly2hmos/biz/whitenoise/src/, 构建文件

备注:原计划 1.2(AppStore 接 Module 桥的 KuiklyPlaybackEngine)依赖鸿蒙侧
Module 实现细节,合并入 M2 执行。
