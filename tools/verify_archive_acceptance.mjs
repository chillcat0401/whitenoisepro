#!/usr/bin/env node

import { readFile, readdir } from "node:fs/promises";
import path from "node:path";

import {
  validateAcceptanceRecord,
  validateChangeId,
} from "./archive_acceptance.mjs";

const args = parseArguments(process.argv.slice(2));
const root = path.resolve(args.root ?? process.cwd());

try {
  const changeIds = args.allArchives
    ? await listArchiveIds(root)
    : [args.change];

  let failed = false;
  for (const changeId of changeIds) {
    const result = await validateChange(root, changeId);
    if (result.errors.length === 0) {
      process.stdout.write(`PASS ${changeId} (${result.mode})\n`);
    } else {
      failed = true;
      process.stderr.write(`FAIL ${changeId}\n`);
      for (const error of result.errors) {
        process.stderr.write(`  - ${error}\n`);
      }
    }
  }

  if (failed) {
    process.exitCode = 1;
  }
} catch (error) {
  process.stderr.write(`${error.message}\n`);
  process.exitCode = 1;
}

function parseArguments(argv) {
  const parsed = {
    root: null,
    change: null,
    allArchives: false,
  };

  for (let index = 0; index < argv.length; index += 1) {
    const argument = argv[index];
    if (argument === "--root") {
      parsed.root = requireValue(argv, ++index, "--root");
    } else if (argument === "--change") {
      parsed.change = requireValue(argv, ++index, "--change");
    } else if (argument === "--all-archives") {
      parsed.allArchives = true;
    } else {
      throw new Error(`Unknown argument: ${argument}`);
    }
  }

  if (parsed.allArchives === Boolean(parsed.change)) {
    throw new Error("Provide exactly one of --change or --all-archives");
  }

  return parsed;
}

function requireValue(argv, index, option) {
  const value = argv[index];
  if (!value || value.startsWith("--")) {
    throw new Error(`${option} requires a value`);
  }
  return value;
}

async function listArchiveIds(root) {
  const archiveRoot = path.join(root, "openspec/changes/archive");
  const entries = await readdir(archiveRoot, { withFileTypes: true });
  return entries
    .filter((entry) => entry.isDirectory())
    .map((entry) => entry.name)
    .sort();
}

async function validateChange(root, changeId) {
  const changeIdError = validateChangeId(changeId);
  if (changeIdError) {
    return { mode: "unknown", errors: [changeIdError] };
  }

  const recordPath = path.join(
    root,
    "docs/superpowers/acceptance",
    `${changeId}.json`,
  );
  let record;
  try {
    record = JSON.parse(await readFile(recordPath, "utf8"));
  } catch (error) {
    if (error?.code === "ENOENT") {
      return {
        mode: "unknown",
        errors: [
          `Missing acceptance record: docs/superpowers/acceptance/${changeId}.json`,
        ],
      };
    }
    if (error instanceof SyntaxError) {
      return {
        mode: "unknown",
        errors: [`Invalid acceptance JSON for ${changeId}: ${error.message}`],
      };
    }
    throw error;
  }

  const errors = [];
  if (record.changeId !== changeId) {
    errors.push(
      `Acceptance record changeId ${record.changeId} does not match ${changeId}`,
    );
  }
  const validation = await validateAcceptanceRecord(root, record);
  errors.push(...validation.errors);
  return { mode: record.mode ?? "unknown", errors };
}
