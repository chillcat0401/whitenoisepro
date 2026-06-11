# DevEco 外置盘软链运维(2026-06-11)

## 布局

- 应用本体:`/Volumes/Volumes2T/Applications/DevEco-Studio-6.1.1.app`(5.1.1 留作回退)
- `/Applications/DevEco-Studio.app` 为软链(KBA 鸿蒙工具链硬编码读此路径下 SDK)
- 6.1.1 SDK 布局与 5.1.1 一致,KBA 构建链绕缓存全量重编验证通过(55s)

## 软链"用着用着失效"的根因分析(本机取证)

本机 `disksleep=0 / sleep=0`,雷电 NVMe 固定盘,近期无掉盘记录——软链机制本身稳定。
失效场景按可能性:

1. **挂载点漂移(头号)**:不洁卸载在 /Volumes 留残留目录,下次挂载变
   `Volumes2T 1`,所有软链指向的老路径失效。修复:弹出磁盘 →
   `sudo rm -rf "/Volumes/Volumes2T"`(确认是残留空目录!)→ 重插。
2. **开机窗口期**:登录早期盘未挂载,LaunchServices 缓存"应用损坏"状态,
   盘挂上后不自愈——表象像链接坏了,实际 readlink 完好。
3. **应用自更新**:IDE 更新可能用真实目录顶掉软链(doctor 检测到会记日志且不覆盖)。

## 自愈机制(已部署)

- `~/.local/bin/volume-symlink-doctor.sh`(本仓 docs/ops/ 存参考副本):
  挂载触发校验/重建软链;检测漂移弹系统通知;日志
  `~/Library/Logs/volume-symlink-doctor.log`。新增外置盘应用在脚本 LINKS 表加一行。
- `~/Library/LaunchAgents/com.user.volume-symlink-doctor.plist`:
  `StartOnMount`(任何卷挂载触发)+ `RunAtLoad`(登录触发)。
- `kuikly2hmos/biz/build_ohos.sh` 前置 SDK 可达性检查,失效时给出修复指引。
