# kuikly2hmos —— WhiteNoisePro 鸿蒙复刻(Kuikly 路线)

Google Play 审核等待期的前置准备。阅读顺序:

1. `REPLICATION_PLAN.md` —— 总体规划:调研结论、架构决策、M0~M5 里程碑
   (任务已写成 superpowers-bridge 三元组,可直接开 OpenSpec 变更执行)、风险登记。
2. `PORTING_MAP.md` —— 主仓 → 鸿蒙的文件级移植映射(♻️/🔧/🔁/🆕)。
3. `biz/whitenoise/` —— 业务 KMP 模块骨架:根页(Compose DSL)、
   音频/存储桥 common 侧定义、自官方 demo 派生的两份构建文件。
4. `KuiklyUI/` —— 官方仓库浅克隆,**构建工作区**(已 gitignore;
   重建命令:`git clone --depth 1 https://github.com/Tencent-TDS/KuiklyUI.git kuikly2hmos/KuiklyUI`)。

执行 agent 入口:按 REPLICATION_PLAN「执行约定」一节,从 M0.1(跑通官方
demo 到鸿蒙设备)开始,每个里程碑建一个 OpenSpec 变更。
