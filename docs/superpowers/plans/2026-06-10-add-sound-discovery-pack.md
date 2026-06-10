# 实施计划:add-sound-discovery-pack

执行模式:executing-plans(单人顺序),严格按 tasks.md 队列,每任务
TDD → verify → scope 对账 → evidence → 微提交,失败即停。
每步证据:docs/superpowers/evidence/add-sound-discovery-pack/<task-id>.md。

## 步骤

- [x] 1.1 PresetCatalog 六场景预设 + 引用合法性测试(verify: PresetCatalogTest)
- [x] 1.2 MixIntent.ReplaceCurrentMix 归约 + 测试(verify: MixReducerTest)
- [x] 1.3 PlayPresetMix + 首页场景入睡横滑区(verify: AppStoreTest)
- [x] 2.1 MixDice 生成器,200 种子结构规则测试(verify: MixDiceTest)
- [x] 2.2 声音库骰子按钮 + RollDiceMix,Random 注入(verify: AppStoreTest)
- [x] 3.1 倾斜噪声合成 + noise_custom_t 编解码,频谱单调性测试(verify: NoiseSynthesizerTest)
- [x] 3.2 SoundSource/缓存/解析器自定义路由 + 名称图标回退(verify: Resolver/SoundCatalog/DesignTokenTest)
- [x] 3.3 噪声实验室卡片(verify: 全量单测 + 模拟器实操)
- [x] 4.1 全量门禁 + 模拟器三功能实操 + AAB 重建验签 + 商店截图刷新
- [x] 5.1 ToggleSound 切换语义修复重复添加(verify: AppStoreTest)
- [x] 5.2 加入/移除/保存全局反馈浮层(verify: AppStoreTest)
- [x] 5.3 混音页试听/暂停控件(verify: 全量单测 + 模拟器实操)
- [x] 6.1 操作按钮间距 4dp→12dp(verify: 单测 + 截图核对)
- [x] 6.2 Lora 衬线标题/数字字体管线(verify: check + 截图核对)
- [x] 6.3 霞鹜文楷 GB2312 子集中文标题(verify: check + 截图核对)

## 关键风险点(执行前评估,均已兑现处理)

- 倾斜合成频谱测试用高/低通能量比做单调断言,锚定白/棕端点;
- SoundSource.Synthesized 签名变更先改测试预期再改实现;
- 骰子命名场景词去重,200 种子验证多样性。
