# ohosApp 壳工程定制快照

工作区(KuiklyUI/ 克隆)不入主仓,壳工程的全部改动以 patch 固化于此,
重建工作区后依次 `git apply`:

- `../workspace-patches/0001-workspace-setup.patch` — settings 注册 whitenoise +
  上游 compose jvmTarget 修复
- `0002-shell-customization.patch` — 剥离 bugly 崩溃上报(合规)+
  Index.ets 默认路由改 WhiteNoiseApp

注:0002 含 CrashReport.ets 整文件删除;apply 后需 `ohpm install --all` 重装依赖。

更新(2026-06-11,M2.1):
- `modules/WNPAudioPlayerModule.ets` — 新增文件,拷入
  `ohosApp/entry/src/main/ets/kuikly/modules/`(0002 补丁只含已有文件修改);
- rawfile 测试声源:从主仓 `composeApp/src/androidMain/res/raw/` 拷
  `rain_soft_loop.ogg`、`ocean_gentle_loop.ogg` 到
  `ohosApp/entry/src/main/resources/rawfile/`;
- 0002 补丁现含:bundleName(com.whitenoisepro.hmos)、应用名(白噪声 Pro)、
  bugly 剥离、默认路由、模块注册。
