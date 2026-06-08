import assert from "node:assert/strict";
import { execFile } from "node:child_process";
import { mkdtemp, mkdir, readFile, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import path from "node:path";
import test from "node:test";
import { promisify } from "node:util";

import {
  parseChecklist,
  validateAcceptanceRecord,
  validateChangeId,
  validateRelativePath,
} from "./archive_acceptance.mjs";

const execFileAsync = promisify(execFile);
const cliPath = path.resolve("tools/verify_archive_acceptance.mjs");

test("parseChecklist reports checked and unchecked tasks", () => {
  assert.deepEqual(
    parseChecklist("# Tasks\n- [x] complete\n- [ ] pending\n- [X] complete too\n"),
    { total: 3, checked: 2, unchecked: 1 },
  );
});

test("strict mode rejects incomplete plan, missing review, and missing verification", async () => {
  const root = await createFixture();
  await writeFixture(root, "openspec/changes/sample/tasks.md", "- [x] done\n");
  await writeFixture(root, "docs/superpowers/plans/sample.md", "- [ ] pending\n");

  const result = await validateAcceptanceRecord(root, {
    schemaVersion: 1,
    changeId: "sample",
    mode: "strict",
    status: "passed",
    evidence: {
      tasks: "openspec/changes/sample/tasks.md",
      plan: "docs/superpowers/plans/sample.md",
      review: "docs/review-sample.md",
      verification: [],
    },
    unverifiable: [],
    remainingRisks: [],
    verifiedAt: "2026-06-08",
  });

  assert.deepEqual(result.errors, [
    "Superpowers plan has 1 unchecked task(s): docs/superpowers/plans/sample.md",
    "Evidence file does not exist: docs/review-sample.md",
    "Strict acceptance requires at least one verification item",
  ]);
});

test("strict mode accepts complete evidence without unverifiable claims", async () => {
  const root = await createFixture();
  await writeFixture(root, "openspec/changes/sample/tasks.md", "- [x] done\n");
  await writeFixture(root, "docs/superpowers/plans/sample.md", "- [x] done\n");
  await writeFixture(root, "docs/review-sample.md", "# Review\n");

  const result = await validateAcceptanceRecord(root, strictRecord());

  assert.deepEqual(result.errors, []);
});

test("retrospective mode requires historical gaps to be declared", async () => {
  const root = await createFixture();
  await writeFixture(
    root,
    "openspec/changes/archive/2026-01-01-sample/tasks.md",
    "- [x] done\n",
  );

  const result = await validateAcceptanceRecord(root, {
    schemaVersion: 1,
    changeId: "2026-01-01-sample",
    mode: "retrospective",
    status: "passed",
    evidence: {
      tasks: "openspec/changes/archive/2026-01-01-sample/tasks.md",
      plan: null,
      review: null,
      verification: ["Current tests pass"],
    },
    unverifiable: [],
    remainingRisks: [],
    verifiedAt: "2026-06-08",
  });

  assert.deepEqual(result.errors, [
    "Retrospective acceptance must declare missing plan evidence in unverifiable",
    "Retrospective acceptance must declare missing review evidence in unverifiable",
  ]);
});

test("retrospective mode accepts explicit historical limitations", async () => {
  const root = await createFixture();
  await writeFixture(
    root,
    "openspec/changes/archive/2026-01-01-sample/tasks.md",
    "- [x] done\n",
  );

  const result = await validateAcceptanceRecord(root, {
    schemaVersion: 1,
    changeId: "2026-01-01-sample",
    mode: "retrospective",
    status: "passed",
    evidence: {
      tasks: "openspec/changes/archive/2026-01-01-sample/tasks.md",
      plan: null,
      review: null,
      verification: ["Current tests pass"],
    },
    unverifiable: [
      "plan: No Superpowers plan was retained",
      "review: No dedicated review was retained",
      "tdd: Historical RED execution cannot be reconstructed",
    ],
    remainingRisks: [],
    verifiedAt: "2026-06-08",
  });

  assert.deepEqual(result.errors, []);
});

test("retrospective mode accepts an incomplete retained plan when declared", async () => {
  const root = await createFixture();
  await writeFixture(
    root,
    "openspec/changes/archive/2026-01-01-sample/tasks.md",
    "- [x] done\n",
  );
  await writeFixture(
    root,
    "docs/superpowers/plans/2026-01-01-sample.md",
    "- [ ] historical checkbox was not synchronized\n",
  );
  await writeFixture(root, "docs/review-sample.md", "# Review\n");

  const result = await validateAcceptanceRecord(root, {
    schemaVersion: 1,
    changeId: "2026-01-01-sample",
    mode: "retrospective",
    status: "passed",
    evidence: {
      tasks: "openspec/changes/archive/2026-01-01-sample/tasks.md",
      plan: "docs/superpowers/plans/2026-01-01-sample.md",
      review: "docs/review-sample.md",
      verification: ["Current tests pass"],
    },
    unverifiable: [
      "plan: Retained plan has unchecked historical steps",
      "tdd: Historical RED execution cannot be reconstructed",
    ],
    remainingRisks: [],
    verifiedAt: "2026-06-08",
  });

  assert.deepEqual(result.errors, []);
});

test("relative evidence paths cannot escape the repository", () => {
  assert.equal(validateRelativePath("docs/review.md"), null);
  assert.equal(
    validateRelativePath("../secret"),
    "Evidence path must stay inside the repository: ../secret",
  );
  assert.equal(
    validateRelativePath("/tmp/secret"),
    "Evidence path must be relative: /tmp/secret",
  );
});

test("change ids reject path traversal and unsupported characters", () => {
  assert.equal(validateChangeId("valid-change-1"), null);
  assert.equal(
    validateChangeId("../../secret"),
    "Invalid change id: ../../secret",
  );
  assert.equal(validateChangeId("Bad_Name"), "Invalid change id: Bad_Name");
});

test("acceptance record cannot use another change tasks as evidence", async () => {
  const root = await createFixture();
  await writeFixture(root, "openspec/changes/other/tasks.md", "- [x] done\n");
  await writeFixture(root, "docs/superpowers/plans/sample.md", "- [x] done\n");
  await writeFixture(root, "docs/review-sample.md", "# Review\n");
  const record = strictRecord();
  record.evidence.tasks = "openspec/changes/other/tasks.md";

  const result = await validateAcceptanceRecord(root, record);

  assert.deepEqual(result.errors, [
    "Tasks evidence does not belong to sample: openspec/changes/other/tasks.md",
  ]);
});

test("CLI validates one change and reports success", async () => {
  const root = await createFixture();
  await writeFixture(root, "openspec/changes/sample/tasks.md", "- [x] done\n");
  await writeFixture(root, "docs/superpowers/plans/sample.md", "- [x] done\n");
  await writeFixture(root, "docs/review-sample.md", "# Review\n");
  await writeFixture(
    root,
    "docs/superpowers/acceptance/sample.json",
    JSON.stringify(strictRecord()),
  );

  const { stdout } = await execFileAsync(
    process.execPath,
    [cliPath, "--root", root, "--change", "sample"],
    { encoding: "utf8" },
  );

  assert.match(stdout, /PASS sample \(strict\)/);
});

test("CLI exits non-zero and identifies a missing acceptance record", async () => {
  const root = await createFixture();
  await writeFixture(
    root,
    "openspec/changes/archive/2026-01-01-sample/tasks.md",
    "- [x] done\n",
  );

  await assert.rejects(
    execFileAsync(
      process.execPath,
      [cliPath, "--root", root, "--all-archives"],
      { encoding: "utf8" },
    ),
    (error) => {
      assert.equal(error.code, 1);
      assert.match(
        error.stderr,
        /Missing acceptance record: docs\/superpowers\/acceptance\/2026-01-01-sample\.json/,
      );
      return true;
    },
  );
});

test("Gradle check lifecycle depends on the strict archive gate", async () => {
  const buildScript = await readFile("build.gradle.kts", "utf8");

  assert.match(
    buildScript,
    /tasks\.register<Exec>\("verifyArchiveAcceptance"\)/,
  );
  assert.match(
    buildScript,
    /tasks\.matching\s*\{\s*it\.name == "check"\s*\}[\s\S]*dependsOn\(rootProject\.tasks\.named\("verifyArchiveAcceptance"\)\)/,
  );
  assert.match(
    buildScript,
    /"tools\/verify_archive_acceptance\.mjs",\s*"--all-archives"/,
  );
});

function strictRecord() {
  return {
    schemaVersion: 1,
    changeId: "sample",
    mode: "strict",
    status: "passed",
    evidence: {
      tasks: "openspec/changes/sample/tasks.md",
      plan: "docs/superpowers/plans/sample.md",
      review: "docs/review-sample.md",
      verification: ["node --test tools/*.test.mjs"],
    },
    unverifiable: [],
    remainingRisks: [],
    verifiedAt: "2026-06-08",
  };
}

async function createFixture() {
  return mkdtemp(path.join(tmpdir(), "archive-acceptance-"));
}

async function writeFixture(root, relativePath, contents) {
  const filePath = path.join(root, relativePath);
  await mkdir(path.dirname(filePath), { recursive: true });
  await writeFile(filePath, contents);
}
