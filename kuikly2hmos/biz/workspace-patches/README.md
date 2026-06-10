# 工作区补丁(重建 KuiklyUI 克隆后重放)

```sh
cd kuikly2hmos/KuiklyUI
git apply ../biz/workspace-patches/0001-workspace-setup.patch
cp -r ../biz/whitenoise ./whitenoise   # 业务模块(真源码在 biz/,工作区为副本)
```

补丁内容:
1. settings.2.0.ohos.gradle.kts / settings.gradle.kts:注册 :whitenoise;
2. compose/build.2.1.21.gradle.kts:android jvmTarget 1.8→17
   (上游 :core 默认 17,不改则 inline 字节码冲突,见 M1 证据)。
