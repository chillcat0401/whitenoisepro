/**
 * [whitenoise] 多层环境音播放桥(对照主仓 AndroidPlaybackEngine 语义)。
 * 每声层一个 AVPlayer(loop=true);音量乘法在 Kotlin 共享层完成,
 * 本模块只接受最终音量,保持无业务状态。
 */
import { KRAny, KuiklyRenderBaseModule, KuiklyRenderCallback } from '@kuikly-open/render';
import media from '@ohos.multimedia.media';
import common from '@ohos.app.ability.common';

interface LayerSpec {
  layerId: string;
  source: string;   // "rawfile:<名称>" 或绝对路径(合成缓存,M2.3)
  volume: number;
  muted: boolean;
}

export class WNPAudioPlayerModule extends KuiklyRenderBaseModule {
  static readonly MODULE_NAME = 'WNPAudioPlayerModule';

  private players: Map<string, media.AVPlayer> = new Map();
  private volumes: Map<string, number> = new Map();
  private mutedSet: Set<string> = new Set();
  private sources: Map<string, string> = new Map();

  syncMode(): boolean {
    return false; // 异步模块,跑 UI 线程,便于取 context 与媒体回调
  }

  call(method: string, params: KRAny, _callback: KuiklyRenderCallback | null): KRAny {
    switch (method) {
      case 'playLayers':
        this.playLayers(params as string);
        break;
      case 'pauseAll':
        this.players.forEach(p => { p.pause(); });
        break;
      case 'stopAll':
        this.releaseAll();
        break;
      case 'setLayerVolume': {
        const parts = (params as string).split('|');
        this.setVolume(parts[0], Number(parts[1]));
        break;
      }
      case 'setLayerMuted': {
        const parts = (params as string).split('|');
        this.setMuted(parts[0], parts[1] === 'true');
        break;
      }
      case 'updateNowPlaying':
        // M2.2:AVSession 元数据,本切片暂记日志
        console.info(`[WNPAudio] nowPlaying ${params}`);
        break;
      default:
        break;
    }
    return null;
  }

  private playLayers(layersJson: string): void {
    const specs = JSON.parse(layersJson) as LayerSpec[];
    const aliveIds = new Set<string>(specs.map(s => s.layerId));
    // 释放被移除的层
    this.players.forEach((player, id) => {
      if (!aliveIds.has(id)) {
        player.release();
        this.players.delete(id);
        this.volumes.delete(id);
        this.sources.delete(id);
        this.mutedSet.delete(id);
      }
    });
    for (const spec of specs) {
      this.volumes.set(spec.layerId, spec.volume);
      if (spec.muted) { this.mutedSet.add(spec.layerId); } else { this.mutedSet.delete(spec.layerId); }
      const existing = this.players.get(spec.layerId);
      if (existing && this.sources.get(spec.layerId) === spec.source) {
        existing.setVolume(this.effectiveVolume(spec.layerId));
        existing.play();
        continue;
      }
      if (existing) {
        existing.release();
        this.players.delete(spec.layerId);
      }
      this.createPlayer(spec);
    }
  }

  private createPlayer(spec: LayerSpec): void {
    media.createAVPlayer().then((player: media.AVPlayer) => {
      this.players.set(spec.layerId, player);
      this.sources.set(spec.layerId, spec.source);
      player.on('stateChange', (state: string) => {
        console.info(`[WNPAudio] layer=${spec.layerId} state=${state}`);
        if (state === 'initialized') {
          player.prepare();
        } else if (state === 'prepared') {
          player.loop = true;
          player.setVolume(this.effectiveVolume(spec.layerId));
          player.play();
        }
      });
      player.on('error', (err) => {
        console.error(`[WNPAudio] layer=${spec.layerId} error=${JSON.stringify(err)}`);
      });
      if (spec.source.startsWith('rawfile:')) {
        const name = spec.source.substring('rawfile:'.length);
        const context = getContext(this) as common.UIAbilityContext;
        context.resourceManager.getRawFd(name).then((rawFd) => {
          player.fdSrc = { fd: rawFd.fd, offset: rawFd.offset, length: rawFd.length };
        });
      } else {
        player.url = `file://${spec.source}`;
      }
    });
  }

  private effectiveVolume(layerId: string): number {
    if (this.mutedSet.has(layerId)) { return 0; }
    return this.volumes.get(layerId) ?? 0.5;
  }

  private setVolume(layerId: string, volume: number): void {
    this.volumes.set(layerId, volume);
    this.players.get(layerId)?.setVolume(this.effectiveVolume(layerId));
  }

  private setMuted(layerId: string, muted: boolean): void {
    if (muted) { this.mutedSet.add(layerId); } else { this.mutedSet.delete(layerId); }
    this.players.get(layerId)?.setVolume(this.effectiveVolume(layerId));
  }

  private releaseAll(): void {
    this.players.forEach(p => { p.release(); });
    this.players.clear();
    this.volumes.clear();
    this.sources.clear();
    this.mutedSet.clear();
  }
}
