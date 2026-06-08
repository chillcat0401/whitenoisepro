import { readFile, stat } from "node:fs/promises";
import path from "node:path";

export function parseChecklist(markdown) {
  const matches = markdown.matchAll(/^- \[([ xX])\]/gm);
  let total = 0;
  let checked = 0;

  for (const match of matches) {
    total += 1;
    if (match[1].toLowerCase() === "x") {
      checked += 1;
    }
  }

  return { total, checked, unchecked: total - checked };
}

export function validateRelativePath(relativePath) {
  if (path.isAbsolute(relativePath)) {
    return `Evidence path must be relative: ${relativePath}`;
  }

  const normalized = path.normalize(relativePath);
  if (normalized === ".." || normalized.startsWith(`..${path.sep}`)) {
    return `Evidence path must stay inside the repository: ${relativePath}`;
  }

  return null;
}

export function validateChangeId(changeId) {
  return /^[a-z0-9][a-z0-9-]*$/.test(changeId)
    ? null
    : `Invalid change id: ${changeId}`;
}

export async function validateAcceptanceRecord(root, record) {
  const errors = validateRecordShape(record);
  if (errors.length > 0) {
    return { errors };
  }

  if (
    path.basename(path.dirname(record.evidence.tasks)) !== record.changeId
  ) {
    errors.push(
      `Tasks evidence does not belong to ${record.changeId}: ${record.evidence.tasks}`,
    );
  }

  const taskResult = await validateChecklistEvidence(
    root,
    record.evidence.tasks,
    "OpenSpec tasks",
  );
  errors.push(...taskResult);

  if (record.evidence.plan) {
    const planErrors = await validateChecklistEvidence(
      root,
      record.evidence.plan,
      "Superpowers plan",
    );
    const declaredHistoricalPlanGap =
      record.mode === "retrospective" && hasUnverifiable(record, "plan:");
    errors.push(
      ...planErrors.filter(
        (error) =>
          !declaredHistoricalPlanGap ||
          !error.startsWith("Superpowers plan has "),
      ),
    );
  } else if (record.mode === "strict") {
    errors.push("Strict acceptance requires Superpowers plan evidence");
  } else if (!hasUnverifiable(record, "plan:")) {
    errors.push(
      "Retrospective acceptance must declare missing plan evidence in unverifiable",
    );
  }

  if (record.evidence.review) {
    errors.push(...(await validateFileEvidence(root, record.evidence.review)));
  } else if (record.mode === "strict") {
    errors.push("Strict acceptance requires code review evidence");
  } else if (!hasUnverifiable(record, "review:")) {
    errors.push(
      "Retrospective acceptance must declare missing review evidence in unverifiable",
    );
  }

  if (record.evidence.verification.length === 0) {
    errors.push(
      record.mode === "strict"
        ? "Strict acceptance requires at least one verification item"
        : "Retrospective acceptance requires at least one current verification item",
    );
  }

  if (record.mode === "strict" && record.unverifiable.length > 0) {
    errors.push("Strict acceptance cannot contain unverifiable items");
  }

  return { errors };
}

function validateRecordShape(record) {
  const errors = [];

  if (record?.schemaVersion !== 1) {
    errors.push("Acceptance record schemaVersion must be 1");
  }
  if (!record?.changeId || typeof record.changeId !== "string") {
    errors.push("Acceptance record changeId is required");
  }
  if (!["strict", "retrospective"].includes(record?.mode)) {
    errors.push("Acceptance record mode must be strict or retrospective");
  }
  if (record?.status !== "passed") {
    errors.push("Acceptance record status must be passed");
  }
  if (!record?.evidence || typeof record.evidence !== "object") {
    errors.push("Acceptance record evidence is required");
  } else {
    if (!record.evidence.tasks || typeof record.evidence.tasks !== "string") {
      errors.push("Acceptance record tasks evidence is required");
    }
    if (!Array.isArray(record.evidence.verification)) {
      errors.push("Acceptance record verification must be an array");
    }
  }
  if (!Array.isArray(record?.unverifiable)) {
    errors.push("Acceptance record unverifiable must be an array");
  }
  if (!Array.isArray(record?.remainingRisks)) {
    errors.push("Acceptance record remainingRisks must be an array");
  }
  if (!record?.verifiedAt || typeof record.verifiedAt !== "string") {
    errors.push("Acceptance record verifiedAt is required");
  }

  return errors;
}

async function validateChecklistEvidence(root, relativePath, label) {
  const errors = await validateFileEvidence(root, relativePath);
  if (errors.length > 0) {
    return errors;
  }

  const markdown = await readFile(path.join(root, relativePath), "utf8");
  const checklist = parseChecklist(markdown);
  if (checklist.total === 0) {
    return [`${label} contains no checklist tasks: ${relativePath}`];
  }
  if (checklist.unchecked > 0) {
    return [
      `${label} has ${checklist.unchecked} unchecked task(s): ${relativePath}`,
    ];
  }

  return [];
}

async function validateFileEvidence(root, relativePath) {
  const pathError = validateRelativePath(relativePath);
  if (pathError) {
    return [pathError];
  }

  try {
    const file = await stat(path.join(root, relativePath));
    return file.isFile() ? [] : [`Evidence path is not a file: ${relativePath}`];
  } catch (error) {
    if (error?.code === "ENOENT") {
      return [`Evidence file does not exist: ${relativePath}`];
    }
    throw error;
  }
}

function hasUnverifiable(record, prefix) {
  return record.unverifiable.some((item) => item.startsWith(prefix));
}
