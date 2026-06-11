#!/bin/sh
# 外置盘软链自愈:由 launchd StartOnMount 在任何卷挂载时触发。
# 脚本必须放本地盘(外置盘未挂载时它要能运行)。
LOG="$HOME/Library/Logs/volume-symlink-doctor.log"
ts() { date "+%Y-%m-%d %H:%M:%S"; }

# 软链清单:链接路径|目标路径(新增外置盘应用在此追加一行)
LINKS="
/Applications/DevEco-Studio.app|/Volumes/Volumes2T/Applications/DevEco-Studio-6.1.1.app
"

# 1) 挂载点漂移告警:出现 'Volumes2T 1' 说明上次不洁卸载留下残留目录
if [ -d "/Volumes/Volumes2T 1" ]; then
  echo "$(ts) [WARN] 检测到挂载点漂移 /Volumes/Volumes2T 1(不洁卸载残留)" >> "$LOG"
  osascript -e 'display notification "外置盘挂载成了 Volumes2T 1,软链已失效。请弹出磁盘后执行: sudo rm -rf \"/Volumes/Volumes2T\" 再重新插入" with title "磁盘挂载点漂移"' 2>/dev/null
fi

# 2) 软链自愈:目标存在但链接缺失/悬空时重建(绝不动真实目录)
echo "$LINKS" | while IFS='|' read -r link target; do
  [ -z "$link" ] && continue
  [ ! -e "$target" ] && continue          # 盘没挂载,跳过
  if [ -L "$link" ]; then
    [ -e "$link" ] && continue            # 链接健康
    rm "$link"                            # 悬空软链,清掉重建
  elif [ -e "$link" ]; then
    echo "$(ts) [SKIP] $link 是真实文件/目录(可能应用自更新顶掉了软链),不自动覆盖" >> "$LOG"
    continue
  fi
  ln -s "$target" "$link" && echo "$(ts) [FIX] 重建 $link -> $target" >> "$LOG"
done
exit 0
