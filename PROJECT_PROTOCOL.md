# WhiteNoisePro Project Protocol

This project uses OpenSpec for product planning, scope control, acceptance criteria, and spec archival. It uses Superpowers for implementation discipline, TDD execution, verification, and code review.

The product goal is to build a modern white noise and sleep sounds app with low-distraction UX, night-friendly UI, stable audio playback, and a path toward Android plus Huawei / HarmonyOS distribution.

## Core Rule

Never implement before an OpenSpec proposal is created and accepted.

## Project-Level Constraints

- New project documents must be written in Chinese by default, including docs, OpenSpec artifacts, review notes, verification reports, and planning documents.
- Existing documents do not need to be rewritten or translated retroactively unless the user explicitly asks for that work.
- Technical identifiers, code symbols, commands, file paths, official product names, API names, and external source titles may remain in English when that is clearer or more accurate.

OpenSpec owns:

- Scope
- Acceptance criteria
- Design decisions
- Task list
- Spec archive

Superpowers owns:

- TDD execution
- Task-by-task implementation
- Verification
- Code review

During explore and propose:

- Do not write production code.
- Do not implement UI.
- Do not perform hidden refactors.

During execute:

- Read `proposal.md`, `design.md`, and `tasks.md` first.
- Implement only what is in `tasks.md`.
- Every task requires a failing test first where applicable.
- Every task requires verification.
- Update task status as work progresses.
- If implementation needs to change scope, stop and update OpenSpec first.

Done means:

- All tasks are checked.
- Tests pass.
- Verification is complete.
- Review is complete.
- Archive acceptance record passes the required gate.
- OpenSpec is archived or applied.

## Archive Acceptance Gate

Every governed archive must have:

```text
docs/superpowers/acceptance/<change-id>.json
```

Modes:

- `strict`: required for new changes. OpenSpec tasks and Superpowers plan must be complete, review and
  verification evidence must exist, and `unverifiable` must be empty.
- `retrospective`: only for historical archives created before this gate. Missing or unsynchronized
  historical evidence must be listed explicitly and must not be represented as strict TDD proof.

Commands:

```bash
node tools/verify_archive_acceptance.mjs --all-archives
node tools/verify_archive_acceptance.mjs --change <change-id>
```

The Gradle `check` lifecycle validates all already archived records. For an active change:

1. Complete implementation, verification and code review.
2. Check every OpenSpec task and Superpowers plan step.
3. Create the strict acceptance record.
4. Run the single-change gate.
5. Archive only after the gate passes.

Current passing tests do not prove that historical RED/GREEN execution occurred. Never backfill an
unchecked plan or remove an `unverifiable` item unless repository evidence supports the claim.

## Workflow

When the user requests a feature, page, architecture change, market-readiness task, or implementation change, follow these stages in order.

### 1. Explore

Prefer:

```text
/opsx:explore
```

If unavailable, use:

```text
superpowers:brainstorm
```

Explore must:

- Clarify user goals, scope, non-goals, and success criteria.
- Research the existing codebase, technical stack, Figma design, and project constraints.
- Identify risks around audio playback, background mode, platform permissions, UI responsiveness, privacy, and app store requirements.
- Compare at least two viable approaches.
- Recommend one approach with tradeoffs.

Explore must not:

- Create or modify production code.
- Implement UI.
- Bypass requirement clarification.
- Treat uncertain assumptions as final decisions.

Explore may:

- Read code.
- Read Figma context.
- Read documentation.
- Write temporary analysis notes.
- Output option comparisons and risk lists.

### 2. Propose

After Explore, immediately run:

```text
/opsx:propose
```

Create the OpenSpec change files:

```text
openspec/changes/<change-id>/proposal.md
openspec/changes/<change-id>/design.md
openspec/changes/<change-id>/tasks.md
openspec/changes/<change-id>/specs/
```

The proposal must include:

- User stories
- Functional scope
- Non-goals
- UI/UX acceptance criteria
- Technical approach
- Data model or state model
- Platform differences, including Android, HarmonyOS, or web preview where relevant
- Test strategy
- Task breakdown
- Acceptance criteria for every task

Do not write production code in Propose.

Each task in `tasks.md` must be:

- Independently executable
- Clear about input and output
- Clear about acceptance criteria
- Clear about verification method
- Suitable for TDD

### 3. Apply / Execute

Only begin implementation after the user confirms the proposal or explicitly asks to enter apply / execute.

Prefer loading OpenSpec PlanExecution. If unavailable, manually read:

```text
openspec/changes/<change-id>/proposal.md
openspec/changes/<change-id>/design.md
openspec/changes/<change-id>/tasks.md
```

Then run:

```text
superpowers:executing-plans
```

or:

```text
superpowers:subagent-driven-development
```

Execution rules:

- Strictly follow `tasks.md`.
- Do not implement features outside `tasks.md`.
- If `tasks.md` is incomplete, stop and update OpenSpec before continuing.
- Follow TDD for every applicable task:
  1. Write the failing test.
  2. Implement the smallest code change.
  3. Run tests.
  4. Refactor.
  5. Verify again.
- Update task status after each task.
- Run verification after each task.
- Run code review after meaningful implementation work.

Alignment rules:

```text
Code implementation must align with tasks.md.
Tests must align with acceptance criteria.
UI must align with Figma and design.md.
```

Execution must not:

- Skip tests.
- Treat mocks as final implementation unless the spec allows it.
- Add unnecessary dependencies.
- Change architecture without explanation and spec update.
- Modify unrelated files.
- Overwrite user changes.
- Expand scope through incidental improvements.

### 4. Verification

Every task requires verification.

UI verification:

- Run the local app.
- Use browser, emulator, or screenshot inspection when available.
- Check mobile sizes: 360x800, 390x844, and 430x932.
- Check text overflow.
- Check bottom navigation and Mini Player overlap.
- Compare against Figma and `design.md`.

Audio verification:

- Play and pause.
- Multi-track mix.
- Per-track volume.
- Master volume.
- Background playback.
- Sleep timer.
- Fade-out.
- Lock screen or notification controls when supported.

State verification:

- Save mix.
- Restore last playback.
- Favorite mix.
- Delete mix.
- Empty states.
- Error states.

Platform verification:

- Android permission behavior.
- Huawei / HarmonyOS compatibility.
- Privacy policy and required compliance fields.
- No Google Play Services dependency unless explicitly allowed.

Each verification output must include:

```text
Task:
Verification:
Result:
Remaining risk:
```

### 5. Review

Run code review after each completed feature.

Review priorities:

1. Behavior bugs
2. Missing tests
3. UI/UX mismatch with spec
4. Platform compatibility risks
5. Performance issues
6. Maintainability issues

Review output must include:

```text
Findings:
Tests:
Spec alignment:
Remaining risks:
```

If review finds issues, fix them before moving to the next task.

### 6. Archive / Apply

After all tasks are complete, tests pass, and review is complete, run:

```text
/opsx:archive
```

or:

```text
/opsx:apply
```

The goal is to:

- Merge the Delta Spec.
- Archive the change.
- Output the final change summary.
- Remind the user to review.
- Mark optional follow-up improvements.

Final delivery must include:

- What was completed
- OpenSpec change id
- Tests passed
- Remaining risks needing manual review
- Recommended next step

## Product Constraints

This product is a sleep utility, not a marketing site and not primarily a sound marketplace.

MVP screens:

- Home / Now Playing
- Mixer
- Library
- Timer
- Saved Mixes
- Settings
- Mini Player / Full Player

Core capabilities:

- One-tap playback
- Multi-track sound mixing
- Per-track volume
- Master volume
- Timer
- Fade-out stop
- Save mix
- Favorite mix
- Recently used mixes
- Low-brightness dark UI
- Background playback
- Offline sound assets

Priority experience:

- Gentle at night
- Low-friction when opened half-asleep
- One-handed operation
- Stable playback
- Non-disruptive monetization
- Simple settings
- Modern but restrained UI

Avoid:

- Marketing-first home screens
- Long instructional copy
- Complex first-launch onboarding
- Full-screen ad-like UI
- Harsh bright colors
- Visual flourish that harms playback reliability

## UI/UX Constraints

UI implementation must follow:

- Default low-brightness dark theme
- Responsive support for 360x800, 390x844, and 430x932
- Fixed bottom navigation
- Mini Player above bottom navigation
- Minimum 44px touch targets
- No text overflow
- No button compression
- Easy-to-drag sliders
- Clearly visible primary playback action
- Home supports one-tap playback resume
- Mixer clearly shows each sound layer
- Timer supports presets and custom duration
- Settings remains restrained and utility-focused

Figma alignment rules:

- Prefer matching Figma screen frames.
- Map Figma component names to code components.
- Do not copy meaningless temporary layer names into code.
- If Figma layer structure is messy, propose cleanup before implementation.
- Code component names should be more semantic than temporary Figma layers.

## Technical Constraints

Do not assume the framework before the project stack is chosen.

After the stack is chosen:

- Follow the existing directory structure.
- Follow the existing state management pattern.
- Follow the existing styling system.
- Do not add Tailwind, Redux, MobX, audio libraries, or UI kits without a task and rationale.
- Use mature audio playback libraries when appropriate.
- Encapsulate platform capabilities away from business logic.
- Make persisted data migration-friendly.
- Consider app package size and offline sound strategy.

## China Mainland / HarmonyOS Constraints

If a task involves China Mainland or HarmonyOS distribution, consider:

- Privacy policy
- User agreement
- App filing /备案
- App market review materials
- Minimum permissions
- No Google Play Services dependency
- Huawei AppGallery compatibility
- Whether HarmonyOS NEXT requires native implementation
- Compliance for domestic ads, payment, analytics, and crash SDKs

Do not introduce strong dependencies on overseas services before the compliance path is confirmed.

## Scope Deviation

If implementation needs to deviate from OpenSpec, stop and output:

```text
Spec deviation detected:
Reason:
Impact:
Recommended update:
Need user confirmation:
```

Do not continue with deviating implementation without user confirmation.

## Final Response Format

Every full feature delivery must include:

```text
Summary
Spec / change-id
Tasks completed
Tests / verification
Files changed
Risks / manual review needed
Recommended next step
```

Keep final responses concise, but do not omit verification results.
