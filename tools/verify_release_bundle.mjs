import { existsSync, openSync, readSync, closeSync } from "node:fs";
import { resolve } from "node:path";
import { spawnSync } from "node:child_process";

function fail(message) {
    console.error(message);
    process.exit(1);
}

const input = process.argv[2];
if (!input) fail("Usage: node tools/verify_release_bundle.mjs <bundle.aab>");

const bundle = resolve(input);
if (!existsSync(bundle)) fail(`Bundle does not exist: ${bundle}`);

const descriptor = openSync(bundle, "r");
const signature = Buffer.alloc(4);
readSync(descriptor, signature, 0, signature.length, 0);
closeSync(descriptor);
if (!signature.equals(Buffer.from([0x50, 0x4b, 0x03, 0x04]))) {
    fail(`Bundle is not a ZIP archive: ${bundle}`);
}

const listResult = spawnSync("jar", ["tf", bundle], { encoding: "utf8" });
if (listResult.status !== 0) {
    fail(`Bundle ZIP could not be read:\n${listResult.stderr || listResult.stdout}`);
}

const entries = new Set(listResult.stdout.split(/\r?\n/).filter(Boolean));
for (const required of ["BundleConfig.pb", "base/manifest/AndroidManifest.xml"]) {
    if (!entries.has(required)) fail(`Bundle is missing required entry: ${required}`);
}

const verifyResult = spawnSync(
    "jarsigner",
    [
        "-J-Duser.language=en",
        "-J-Duser.country=US",
        "-verify",
        "-certs",
        bundle,
    ],
    { encoding: "utf8" },
);
const verificationOutput = `${verifyResult.stdout}\n${verifyResult.stderr}`;
if (
    verifyResult.status !== 0
    || /jar is unsigned/i.test(verificationOutput)
    || !/jar verified/i.test(verificationOutput)
) {
    fail(`Bundle signature verification failed:\n${verificationOutput}`);
}

console.log(`Verified signed Android App Bundle: ${bundle}`);
