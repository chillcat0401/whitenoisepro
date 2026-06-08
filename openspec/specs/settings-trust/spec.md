# settings-trust Specification

## Purpose

定义 Settings 中面向用户的隐私、后台媒体控制和未开放能力说明，避免展示不可用的伪功能。文案必须准确反映本地存储、无账号和媒体通知行为，并为 closed testing 提供可理解的信任信息。

## Requirements
### Requirement: Release-ready Settings trust copy

The Settings screen SHALL display privacy and notification rationale suitable for closed testing.

#### Scenario: User reviews Settings before closed testing

- **WHEN** Settings is displayed
- **THEN** it explains that the app has no account, no ads, and stores preferences locally
- **AND** it explains that notification permission is for background and lock-screen media controls, not marketing

### Requirement: Placeholder functionality must not appear available

The Settings screen SHALL hide or disable features that are not implemented for the current MVP.

#### Scenario: Billing is not integrated

- **WHEN** billing is not integrated
- **THEN** Restore Purchases is not shown as an available Settings row

#### Scenario: Offline downloads are not implemented

- **WHEN** offline downloads are not implemented
- **THEN** Offline Downloads is shown as unavailable or hidden, not as an enabled toggle
