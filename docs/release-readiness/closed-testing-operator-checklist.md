# Closed Testing Operator Checklist

## Tester Instruction Copy

```text
请测试 WhiteNoisePro 的以下路径：

1. 打开应用后，直接播放默认混音。
2. 进入 Mixer，查看当前声音层和音量。
3. 进入 Library，浏览声音并尝试“适合今晚的声音”。
4. 设置 30 分钟 timer，检查播放状态和剩余时间显示。
5. 锁屏或切到后台，确认声音是否继续播放。
6. 使用耳机、蓝牙音箱或系统媒体控制暂停 / 恢复。
7. 进入 Settings，查看隐私和播放设置是否清晰。
8. 确认 Settings 是否清楚表达：无账号、无广告、无分析、偏好本地保存、第一方本地声音、后台媒体控制不用于营销通知。

请反馈：

- 哪一步让你困惑？
- 声音是否舒适，有无明显循环断点？
- 后台播放是否稳定？
- 30/45 分钟入睡 timer 是否容易找到？
- 你是否愿意睡前继续使用？
- 是否有任何文字、按钮或权限说明不可信？
- 使用手机扬声器、耳机或蓝牙设备时是否有明显差异？
```

## Day 0

- [ ] 上传 release candidate 到 internal testing。
- [ ] 确认 install link 可用。
- [ ] 发送 tester instruction copy。
- [ ] 记录 tester opt-in date。
- [ ] 建立反馈入口：表格、群聊、邮件或 issue 表。

## Day 2-3

- [ ] 检查安装失败、启动失败、无法播放。
- [ ] 汇总 P0/P1。
- [ ] 确认至少 8 名 tester 已 opt-in 或补招。
- [ ] 提醒未活跃 tester 完成基础路径。

## Day 7

- [ ] 汇总声音舒适度和 loop 反馈。
- [ ] 汇总后台播放、锁屏控制、timer 反馈。
- [ ] 决定是否修复 P0/P1 并上传新 build。
- [ ] 更新 production access answer draft。

## Day 12-14

- [ ] 核对连续 opt-in tester 数量。
- [ ] 整理主要反馈主题。
- [ ] 记录已修复问题和未修复风险。
- [ ] 准备 production access 申请回答。
