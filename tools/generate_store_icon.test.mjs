import assert from "node:assert/strict";
import { mkdtempSync, readFileSync, rmSync, statSync } from "node:fs";
import { tmpdir } from "node:os";
import { join } from "node:path";
import { spawnSync } from "node:child_process";
import test from "node:test";

const projectRoot = new URL("../", import.meta.url);

test("generates a deterministic 512px RGBA PNG below the Play size limit", () => {
    const outputDirectory = mkdtempSync(join(tmpdir(), "white-noise-pro-icon-"));
    const firstOutput = join(outputDirectory, "icon-first.png");
    const secondOutput = join(outputDirectory, "icon-second.png");

    try {
        for (const output of [firstOutput, secondOutput]) {
            const result = spawnSync(
                process.execPath,
                ["tools/generate_store_icon.mjs", "--output", output],
                {
                    cwd: projectRoot,
                    encoding: "utf8",
                },
            );
            assert.equal(result.status, 0, result.stderr || result.stdout);
        }

        const first = readFileSync(firstOutput);
        const second = readFileSync(secondOutput);
        assert.deepEqual(first, second);
        assert.deepEqual(
            [...first.subarray(0, 8)],
            [137, 80, 78, 71, 13, 10, 26, 10],
        );
        assert.equal(first.readUInt32BE(16), 512);
        assert.equal(first.readUInt32BE(20), 512);
        assert.equal(first[24], 8);
        assert.equal(first[25], 6);
        assert.ok(statSync(firstOutput).size < 1024 * 1024);
    } finally {
        rmSync(outputDirectory, { recursive: true, force: true });
    }
});
