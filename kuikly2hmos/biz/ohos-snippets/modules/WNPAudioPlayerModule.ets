/**
 * [whitenoise] 多层环境音播放桥(对照主仓 AndroidPlaybackEngine 语义)。
 * - 每声层一个 AVPlayer(loop=true);音量乘法在 Kotlin 共享层完成,桥只接受最终音量;
 * - AVSession:控制中心/锁屏媒体卡片(标题 + 播放/暂停),指令直接驱动实例池
 *   (对照主仓踩坑:暂停后的恢复指令必须可用,见 AudioFocusPlaybackGate 结论);
 * - 长时任务 AUDIO_PLAYBACK:息屏后进程保活,停止/暂停即退出保活(省电合规)。
 */
import { KRAny, KuiklyRenderBaseModule, KuiklyRenderCallback } from '@kuikly-open/render';
import media from '@ohos.multimedia.media';
import avSession from '@ohos.multimedia.avsession';
import backgroundTaskManager from '@ohos.resourceschedule.backgroundTaskManager';
import wantAgent from '@ohos.app.ability.wantAgent';
import common from '@ohos.app.ability.common';

interface LayerSpec {
  layerId: string;
  source: string;   // "rawfile:<名称>" 或绝对路径(合成缓存)
  volume: number;
  muted: boolean;
}

export class WNPAudioPlayerModule extends KuiklyRenderBaseModule {
  static readonly MODULE_NAME = 'WNPAudioPlayerModule';

  private players: Map<string, media.AVPlayer> = new Map();
  private volumes: Map<string, number> = new Map();
  private mutedSet: Set<string> = new Set();
  private sources: Map<string, string> = new Map();
  private session: avSession.AVSession | null = null;
  private bgTaskRunning: boolean = false;
  private nowTitle: string = '白噪屿';

  syncMode(): boolean {
    return false;
  }

  call(method: string, params: KRAny, _callback: KuiklyRenderCallback | null): KRAny {
    switch (method) {
      case 'playLayers':
        this.playLayers(params as string);
        this.startBackgroundTask();
        this.updateSessionState(true);
        break;
      case 'pauseAll':
        this.pauseAllLayers();
        break;
      case 'stopAll':
        this.releaseAll();
        this.updateSessionState(false);
        this.stopBackgroundTask();
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
      case 'updateNowPlaying': {
        const parts = (params as string).split('|');
        this.nowTitle = parts[0];
        this.ensureSession().then(() => {
          this.updateSessionMetadata();
          this.updateSessionState(parts[1] === 'true');
        });
        break;
      }
      default:
        break;
    }
    return null;
  }

  // ── 播放池 ────────────────────────────────────────────────

  private playLayers(layersJson: string): void {
    const specs = JSON.parse(layersJson) as LayerSpec[];
    const aliveIds = new Set<string>(specs.map(s => s.layerId));
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

  private pauseAllLayers(): void {
    this.players.forEach(p => { p.pause(); });
    this.updateSessionState(false);
    this.stopBackgroundTask();
  }

  private resumeAllLayers(): void {
    this.players.forEach(p => { p.play(); });
    this.startBackgroundTask();
    this.updateSessionState(true);
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

  // ── AVSession(控制中心/锁屏媒体卡片) ─────────────────────

  private async ensureSession(): Promise<void> {
    if (this.session) { return; }
    const context = getContext(this) as common.UIAbilityContext;
    try {
      const session = await avSession.createAVSession(context, 'whitenoiyu', 'audio');
      this.session = session;
      session.on('play', () => {
        console.info('[WNPAudio] avsession command: play');
        this.resumeAllLayers();
      });
      session.on('pause', () => {
        console.info('[WNPAudio] avsession command: pause');
        this.pauseAllLayers();
      });
      session.on('stop', () => {
        console.info('[WNPAudio] avsession command: stop');
        this.releaseAll();
        this.updateSessionState(false);
        this.stopBackgroundTask();
      });
      await session.activate();
      console.info('[WNPAudio] avsession activated');
      this.updateSessionMetadata();
    } catch (err) {
      console.error(`[WNPAudio] avsession create failed: ${JSON.stringify(err)}`);
    }
  }

  private updateSessionMetadata(): void {
    this.session?.setAVMetadata({
      assetId: 'whitenoiyu-mix',
      title: this.nowTitle,
      artist: '白噪屿',
    });
  }

  private updateSessionState(playing: boolean): void {
    this.ensureSession().then(() => {
      this.session?.setAVPlaybackState({
        state: playing ? avSession.PlaybackState.PLAYBACK_STATE_PLAY
                       : avSession.PlaybackState.PLAYBACK_STATE_PAUSE,
      });
    });
  }

  // ── 长时任务(息屏保活) ───────────────────────────────────

  private startBackgroundTask(): void {
    if (this.bgTaskRunning) { return; }
    const context = getContext(this) as common.UIAbilityContext;
    const wantAgentInfo: wantAgent.WantAgentInfo = {
      wants: [{ bundleName: context.abilityInfo.bundleName, abilityName: 'EntryAbility' }],
      actionType: wantAgent.OperationType.START_ABILITY,
      requestCode: 0,
      actionFlags: [wantAgent.WantAgentFlags.UPDATE_PRESENT_FLAG],
    };
    wantAgent.getWantAgent(wantAgentInfo).then((agent) => {
      backgroundTaskManager.startBackgroundRunning(
        context,
        backgroundTaskManager.BackgroundMode.AUDIO_PLAYBACK,
        agent,
      ).then(() => {
        this.bgTaskRunning = true;
        console.info('[WNPAudio] bg task started');
      }).catch((err: Object) => {
        console.error(`[WNPAudio] bg task start failed: ${JSON.stringify(err)}`);
      });
    });
  }

  private stopBackgroundTask(): void {
    if (!this.bgTaskRunning) { return; }
    const context = getContext(this) as common.UIAbilityContext;
    backgroundTaskManager.stopBackgroundRunning(context).then(() => {
      this.bgTaskRunning = false;
      console.info('[WNPAudio] bg task stopped');
    });
  }
}
