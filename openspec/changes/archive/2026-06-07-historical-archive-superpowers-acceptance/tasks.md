# Tasks

## 1. 规格与证据模型

- [x] 1.1 定义 strict/retrospective 验收记录 schema 和路径规则
- [x] 1.2 创建 Superpowers 设计与逐步执行计划
- [x] 1.3 strict validate 当前 OpenSpec change

## 2. Gate TDD

- [x] 2.1 添加 checkbox、strict 和 retrospective 失败场景红灯测试
- [x] 2.2 实现验收记录解析与单 change 校验
- [x] 2.3 添加 CLI 和真实目录集成测试
- [x] 2.4 运行 Node tests 并确认全部通过

## 3. 历史追溯验收

- [x] 3.1 为 12 个非严格通过归档创建 retrospective JSON 记录
- [x] 3.2 运行 `--all-archives` 并修正证据映射
- [x] 3.3 更新人类可读审计报告和项目协议

## 4. 自动 Gate 集成

- [x] 4.1 为当前 change 创建 strict plan、review 和 acceptance record
- [x] 4.2 将 strict gate 接入 Gradle `check`
- [x] 4.3 验证 gate 对缺失/未完成证据返回非零，历史 archive gate 返回零

## 5. 完整验收与归档

- [x] 5.1 运行 Node tests 和历史 archive gate
- [x] 5.2 运行 Gradle check、lint、debug APK、release AAB、签名和音频资产验证
- [x] 5.3 完成代码审查并修复发现
- [x] 5.4 strict validate 当前 change
