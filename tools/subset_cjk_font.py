#!/usr/bin/env python3
"""中文展示字体子集化(B 方案:GB2312 一级常用字)。

字符集 = GB2312 一级字(按拼音序 3755 字)
       + 源码(commonMain)中出现的全部 CJK 字符(保险并集,防二级字遗漏)
       + 中文标点
剔除 ASCII 拉丁与数字字形,让 FontFamily 回退链里的 Lora 接管拉丁/数字。

用法:
  python3 tools/subset_cjk_font.py \
      work/fonts/LXGWWenKai-Medium.ttf \
      composeApp/src/commonMain/composeResources/font/lxgw_wenkai_subset.ttf

新增中文文案后重跑本脚本即可补字;子集缺字时 Android 自动回退系统字体。
"""
import pathlib
import re
import subprocess
import sys

ROOT = pathlib.Path(__file__).resolve().parent.parent


def gb2312_level1_chars() -> set[str]:
    """GB2312 一级字区:区位 16~55 区(0xB0A1-0xD7F9)。"""
    chars = set()
    for high in range(0xB0, 0xD8):
        for low in range(0xA1, 0xFF):
            try:
                chars.add(bytes([high, low]).decode("gb2312"))
            except UnicodeDecodeError:
                continue
    return chars


def source_cjk_chars() -> set[str]:
    chars = set()
    src = ROOT / "composeApp/src/commonMain"
    for path in src.rglob("*.kt"):
        text = path.read_text(encoding="utf-8")
        chars.update(re.findall(r"[一-鿿]", text))
    return chars


CJK_PUNCTUATION = set("、。!?:;,·…—()《》〈〉「」『』【】〔〕“”‘’~×")


def main() -> None:
    if len(sys.argv) != 3:
        sys.exit(__doc__)
    src_font, out_font = sys.argv[1], sys.argv[2]

    charset = gb2312_level1_chars() | source_cjk_chars() | CJK_PUNCTUATION
    missing_from_gb = source_cjk_chars() - gb2312_level1_chars()
    print(f"字符集: GB2312一级 {len(gb2312_level1_chars())} + 源码补充 {len(missing_from_gb)} + 标点 → 共 {len(charset)}")

    text_file = ROOT / "work/fonts/subset-charset.txt"
    text_file.write_text("".join(sorted(charset)), encoding="utf-8")

    subprocess.run(
        [
            sys.executable, "-m", "fontTools.subset",
            src_font,
            f"--text-file={text_file}",
            f"--output-file={out_font}",
            "--layout-features=*",
            "--no-hinting",
            "--desubroutinize",
            "--name-IDs=1,2,3,4,6",
        ],
        check=True,
    )
    size = pathlib.Path(out_font).stat().st_size
    print(f"✓ {out_font}{size / 1024 / 1024:.2f}MB")


if __name__ == "__main__":
    main()
