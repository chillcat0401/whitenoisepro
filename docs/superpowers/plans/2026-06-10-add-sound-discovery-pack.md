# 实施计划:add-sound-discovery-pack

执行模式:executing-plans(单人顺序),严格按 tasks.md 队列,每任务
TDD → verify → scope 对账 → evidence → 微提交,失败即停。

步骤与验证脚本与 tasks.md 三元组一一对应(1.1→4.1),不另行展开。
关键风险点:
- 倾斜合成的频谱测试用高/低通 RMS 比做单调性断言,避免依赖精确 dB/oct;
- SoundSource.Synthesized 签名变更会波及 ResolverTest,先改测试预期再改实现;
- 骰子命名需保证场景词去重(两层同类纹理只出一个词)。
