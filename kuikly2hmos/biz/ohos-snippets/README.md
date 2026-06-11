# ohosApp 壳工程定制快照

工作区(KuiklyUI/ 克隆)不入主仓,壳工程的全部改动以 patch 固化于此,
重建工作区后依次 `git apply`:

- `../workspace-patches/0001-workspace-setup.patch` — settings 注册 whitenoise +
  上游 compose jvmTarget 修复
- `0002-shell-customization.patch` — 剥离 bugly 崩溃上报(合规)+
  Index.ets 默认路由改 WhiteNoiseApp

注:0002 含 CrashReport.ets 整文件删除;apply 后需 `ohpm install --all` 重装依赖。
