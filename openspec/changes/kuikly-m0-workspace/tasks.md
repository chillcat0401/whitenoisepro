# Tasks(L1:鸿蒙复刻 M0——工作区打通,源自 kuikly2hmos/REPLICATION_PLAN.md)

- [x] 0.3 KuiklyUI License 商用审查
  - accept: 通读 LICENSE,商用分发结论写入 REPLICATION_PLAN 附录 A
  - verify: 附录含明确结论与约束清单
  - scope: kuikly2hmos/REPLICATION_PLAN.md

- [x] 0.2 whitenoise 模块接入工作区并编译出鸿蒙 so
  - accept: KuiklyUI 工作区 settings 注册 whitenoise;
    `:whitenoise:linkSharedDebugSharedOhosArm64` 产出 libshared.so + 头文件;
    编译驱动修正骨架(Module API 签名等)回写 biz/whitenoise
  - verify: 构建日志 + 产物路径存在
  - scope: kuikly2hmos/biz/whitenoise/, kuikly2hmos/KuiklyUI(工作区不入库)

- [ ] 0.4 (依赖设备,可延后)官方 demo / whitenoise 装入 ohosApp 真机验证
  - accept: DevEco 运行 ohosApp 显示 AppPage 骨架画面
  - verify: 设备截图
  - scope: 工作区
