# 代码评审:add-sound-discovery-pack(2026-06-10)

范围:16 个提交(53092a0…6029521),场景预设/混音骰子/噪声实验室 + 听审修复(5.x)+ 视觉打磨(6.x)。

## Findings

按协议优先级逐项检查:

1. **行为缺陷**:未发现。重点核查:
   - `ToggleSound` else 分支在循环中逐层 `updateMix(RemoveLayer)`,layer id 取自进入分支前的快照——逐次 reduce 各自独立,无失效引用风险;
   - `MixDice.defaultVolumes` 在对象初始化时引用 `SoundCatalog.all`,两者无环依赖,类加载顺序安全;
   - 倾斜合成全部音色共用固定 `CustomSeed`,确定性可复现;缓存文件名含 `Version` 常量,合成算法变更时自定义音色与预置噪声同步失效重建;
   - 字体回退链逐字形回退在模拟器(API 36)实测生效;`WnpFonts.display` 为主题单写者的可变全局,无并发写入路径。
2. **缺测试**:无阻断项。骰子结构规则 200 种子、倾斜频谱单调性、ToggleSound、反馈状态机、预设引用合法性均有覆盖。UI 组合层(浮层渲染、实验室卡)无 compose UI 测试——与项目现状一致,由模拟器实操证据补偿。
3. **与 spec 不符**:无。Out of Scope(实时试听拖动、呼吸引导)未越界。
4. **平台风险**:文楷子集 1.66MB 仅打包一份,Android/未来鸿蒙均可复用;Compose 字形级回退需要较新 compose-text,当前 CMP 1.11 满足,**鸿蒙 ArkTS 重写时需另行确认字体回退机制**。
5. **性能**:倾斜噪声首次生成约 30~100ms,在播放路径同步执行——与既有预置噪声同路径,prewarm 不覆盖自定义音色;首次添加自定义层可能有亚秒级延迟,可接受。
6. **可维护性**:`SoundCatalog.nameOf` 内联引用 `audio.NoiseSynthesizer`(data→audio 依赖),方向可接受但属轻度耦合,后续如拆模块需留意。

## 显式记录不修(known issues,非阻断)

- **NoiseLabCard 滑杆状态随 LazyColumn 回收重置**:卡片滚出可视区再回来,tilt 回到默认 3.0。低频路径,留待用户反馈决定是否提升状态到 Store。
- **实验室同音色二次点击 = 移除**(ToggleSound 语义的自然结果):已在交付说明中向用户披露,等真机听审反馈再定是否改为"替换最新"。

## 结论

- Tests:`:composeApp:check` 全绿(含 14 任务逐项 verify 证据,docs/superpowers/evidence/add-sound-discovery-pack/)。
- Spec alignment:proposal 验收标准逐条满足,模拟器与真机实操通过。
- Remaining risks:见上两条 known issues + 鸿蒙字体回退确认。
- **结论:通过,可归档。**
