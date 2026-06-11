#!/bin/sh
# whitenoise 鸿蒙 so 构建(在 KuiklyUI workspace 根目录执行)
# 前置:已把 biz/whitenoise 拷入 workspace 并注册 settings.2.0.ohos.gradle.kts;
#       /Applications/DevEco-Studio.app 已安装(KBA 工具链从其内读 OHOS SDK)。
# 踩坑固化:
#  - 无完整 Xcode 的机器:KGP 在 mac 宿主强挂 xcodeVersion 检查,需排除任务
#    并стub 其输出文件(下两行);
#  - 构建命令勿接管道,否则退出码被吞。
set -e
# 软链健康前置检查(失效根因与自愈机制见 docs/ops/deveco-symlink-ops.md)
if [ ! -e /Applications/DevEco-Studio.app/Contents/sdk/default/openharmony ]; then
  echo "✗ DevEco SDK 路径不可达。先跑: ~/.local/bin/volume-symlink-doctor.sh" >&2
  ls -la /Applications/DevEco-Studio.app 2>&1 | head -1 >&2
  exit 1
fi
mkdir -p whitenoise/build && echo "15.4" > whitenoise/build/xcode-version.txt
KUIKLY_AGP_VERSION="7.4.2" KUIKLY_KOTLIN_VERSION="2.0.21-KBA-010" \
  ./gradlew -c settings.2.0.ohos.gradle.kts \
  :whitenoise:linkSharedDebugSharedOhosArm64 -x :whitenoise:xcodeVersion "$@"
echo "产物:"
ls -la whitenoise/build/bin/ohosArm64/sharedDebugShared/
# 装入壳工程(0.4 设备验证时取消注释)
# cp whitenoise/build/bin/ohosArm64/sharedDebugShared/libshared.so ohosApp/entry/libs/arm64-v8a/
# cp whitenoise/build/bin/ohosArm64/sharedDebugShared/libshared_api.h ohosApp/entry/src/main/cpp/thirdparty/biz_entry/
