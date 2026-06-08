# Tasks

## 1. 平台 Timer 边界

- [x] 1.1 添加 AppStore 平台 runtime 红灯测试
- [x] 1.2 实现 `PlatformSleepTimerRuntime` 并接入 start/extend/restore/cancel/completion

## 2. Deadline Runner

- [x] 2.1 添加协程虚拟时间红灯测试
- [x] 2.2 实现绝对时间 fade、stop、reschedule 和 cancel

## 3. Android Service 集成

- [x] 3.1 新增 Android runtime adapter 和 service 调度入口
- [x] 3.2 将 AndroidPlaybackEngine 改为 application-context 进程级单例
- [x] 3.3 Service 生命周期取消 runner，Activity 不再释放 engine

## 4. 验证与归档

- [x] 4.1 更新后台 timer QA 和架构文档
- [x] 4.2 运行完整构建、代码审查、严格校验并归档
