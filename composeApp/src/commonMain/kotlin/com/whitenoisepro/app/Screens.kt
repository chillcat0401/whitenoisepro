package com.whitenoisepro.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.whitenoisepro.design.AppIcon
import com.whitenoisepro.design.AppIconKind
import com.whitenoisepro.data.SampleContent
import com.whitenoisepro.data.SoundCatalog
import com.whitenoisepro.design.IconButton
import com.whitenoisepro.design.PrimaryButton
import com.whitenoisepro.design.SecondaryButton
import com.whitenoisepro.design.SectionHeader
import com.whitenoisepro.design.TimerProgressRing
import com.whitenoisepro.design.WnpTextField
import com.whitenoisepro.design.SettingsRow
import com.whitenoisepro.design.SoundIcon
import com.whitenoisepro.design.TimerPresetChip
import com.whitenoisepro.design.ToggleRow
import com.whitenoisepro.design.VolumeSlider
import com.whitenoisepro.design.WnpColors
import com.whitenoisepro.design.WnpRadius
import com.whitenoisepro.design.WnpSpacing
import com.whitenoisepro.design.WnpTypography
import com.whitenoisepro.domain.model.Sound
import com.whitenoisepro.domain.model.SoundLayer
import com.whitenoisepro.domain.model.SoundMix
import com.whitenoisepro.presentation.AppState
import com.whitenoisepro.presentation.BrandCopy
import com.whitenoisepro.presentation.recommendedBedtimeTimerMinutes
import com.whitenoisepro.presentation.SettingsContent
import com.whitenoisepro.presentation.SettingsRowContent
import com.whitenoisepro.presentation.SettingsRowKind

val timerPresetMinutes: List<Int> = listOf(15, 30, 45, 60, 120)
val recommendedLibrarySoundIds: List<String> = listOf("rain_soft", "ocean_gentle", "wind_forest", "fire_hearth")

@Composable
fun HomeScreen(
    state: AppState,
    padding: PaddingValues,
    onNavigate: (AppTab) -> Unit,
    onTogglePlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    onMasterVolumeChange: (Float) -> Unit,
    onStartRecommendedTimer: () -> Unit,
    onPlayRecentMix: (String) -> Unit,
    onSoundSelected: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(WnpSpacing.SectionGap),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(WnpSpacing.Sm)) {
                TopBar(
                    title = "晚安",
                    action = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(WnpColors.SurfaceHigh)
                                .clickable { onNavigate(AppTab.Settings) },
                            contentAlignment = Alignment.Center,
                        ) {
                            AppIcon(AppIconKind.Settings, tint = WnpColors.OnSurface, modifier = Modifier.size(22.dp))
                        }
                    },
                )
                Text(
                    text = BrandCopy.HomeSupportingLine,
                    color = WnpColors.OnSurfaceVariant,
                    style = WnpTypography.Body,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        item {
            NowPlayingHero(
                mix = state.mixState.currentMix,
                isPlaying = state.isPlaying,
                onTogglePlay = onTogglePlay,
                onToggleFavorite = onToggleFavorite,
                onMasterVolumeChange = onMasterVolumeChange,
                onTimer = { onNavigate(AppTab.Timer) },
                onEdit = { onNavigate(AppTab.Mixer) },
            )
        }
        item {
            BedtimeTimerCallout(
                onStart = onStartRecommendedTimer,
                onOpenTimer = { onNavigate(AppTab.Timer) },
            )
        }
        item {
            SectionHeader(title = "最近使用", actionText = "全部", onActionClick = { onNavigate(AppTab.Saved) })
            Row(horizontalArrangement = Arrangement.spacedBy(WnpSpacing.Lg)) {
                state.mixState.recentMixes.take(3).forEach { mix ->
                    CompactMixCard(
                        mix = mix,
                        onClick = { onPlayRecentMix(mix.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        item {
            SectionHeader(title = BrandCopy.RecommendedSoundsTitle, actionText = "浏览", onActionClick = { onNavigate(AppTab.Library) })
            Row(horizontalArrangement = Arrangement.spacedBy(WnpSpacing.Lg)) {
                SampleContent.sounds.take(4).forEach { sound ->
                    SoundPill(
                        sound = sound,
                        selected = sound.id in state.mixState.currentMix.layers.map { it.soundId },
                        onClick = { onSoundSelected(sound.id) },
                    )
                }
            }
        }
    }
}

@Composable
fun MixerScreen(
    state: AppState,
    padding: PaddingValues,
    onAddSound: () -> Unit,
    onSaveMix: () -> Unit,
    onMasterVolumeChange: (Float) -> Unit,
    onLayerVolumeChange: (String, Float) -> Unit,
    onLayerMutedChange: (String, Boolean) -> Unit,
    onRemoveLayer: (String) -> Unit,
) {
    LazyColumn(
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(WnpSpacing.SectionGap),
    ) {
        item { TopBar(title = "混音") }
        item {
            Text(state.mixState.currentMix.title, color = WnpColors.OnSurface, style = WnpTypography.Display)
            Text("${state.mixState.currentMix.layers.size} 个声音层", color = WnpColors.OnSurfaceVariant, style = WnpTypography.Body)
        }
        items(state.mixState.currentMix.layers) { layer ->
            LayerRow(
                layer = layer,
                isPlaying = state.isPlaying,
                onVolumeChange = { volume -> onLayerVolumeChange(layer.id, volume) },
                onMutedChange = { muted -> onLayerMutedChange(layer.id, muted) },
                onRemove = { onRemoveLayer(layer.id) },
            )
        }
        item {
            SecondaryButton(text = "添加声音", onClick = onAddSound, modifier = Modifier.fillMaxWidth())
        }
        item {
            SectionHeader(title = "主音量")
            VolumeSlider(value = state.mixState.currentMix.masterVolume, onValueChange = onMasterVolumeChange)
        }
        item {
            PrimaryButton(text = "保存混音", onClick = onSaveMix, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun LibraryScreen(
    state: AppState,
    sounds: List<Sound>,
    padding: PaddingValues,
    onQueryChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onSoundSelected: (String) -> Unit,
) {
    val activeSoundIds = state.mixState.currentMix.layers.map { it.soundId }.toSet()
    val categories = listOf("全部") + SoundCatalog.availableCategories.map { it.displayName }
    LazyColumn(
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(WnpSpacing.SectionGap),
    ) {
        item { TopBar(title = "声音库") }
        item {
            WnpTextField(
                value = state.libraryQuery,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = "搜索声音",
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(WnpSpacing.Md)) {
                categories.take(4).forEach { category ->
                    TimerPresetChip(
                        text = category,
                        selected = category == state.selectedCategory,
                        onClick = { onCategorySelected(category) },
                    )
                }
            }
        }
        item {
            SectionHeader(title = BrandCopy.RecommendedSoundsTitle)
            Row(horizontalArrangement = Arrangement.spacedBy(WnpSpacing.Md)) {
                recommendedLibrarySoundIds.mapNotNull { soundId ->
                    SoundCatalog.all.firstOrNull { it.id == soundId }
                }.forEach { sound ->
                    TimerPresetChip(
                        text = sound.name,
                        selected = sound.id in activeSoundIds,
                        onClick = { onSoundSelected(sound.id) },
                    )
                }
            }
        }
        items(sounds.chunked(2)) { rowSounds ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(WnpSpacing.Lg),
            ) {
                rowSounds.forEach { sound ->
                    SoundGridCard(
                        sound = sound,
                        active = sound.id in activeSoundIds,
                        onClick = { onSoundSelected(sound.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowSounds.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun BedtimeTimerCallout(
    onStart: () -> Unit,
    onOpenTimer: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(WnpRadius.Card))
            .background(WnpColors.SurfaceLow)
            .padding(horizontal = WnpSpacing.CardPadding, vertical = WnpSpacing.Lg),
        horizontalArrangement = Arrangement.spacedBy(WnpSpacing.Md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SoundIcon(label = "定时器", active = false, selected = true, iconKey = "timer", modifier = Modifier.size(48.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(BrandCopy.BedtimeTimerTitle, color = WnpColors.OnSurface, style = WnpTypography.Body)
            Text(
                BrandCopy.bedtimeTimerSubtitle(recommendedBedtimeTimerMinutes),
                color = WnpColors.OnSurfaceVariant,
                style = WnpTypography.Label,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        SecondaryButton(text = "开始", onClick = onStart)
        IconButton(onClick = onOpenTimer) {
            AppIcon(AppIconKind.Timer, tint = WnpColors.OnSurface, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun TimerScreen(
    state: AppState,
    padding: PaddingValues,
    onPresetSelected: (Int) -> Unit,
    onStart: () -> Unit,
    onCancel: () -> Unit,
    onExtend: (Int) -> Unit,
    onCustomDuration: (Int) -> Unit,
) {
    val minutes = state.timerState.remainingMillis / 60_000L
    var customMinutes by remember { mutableStateOf("") }
    LazyColumn(
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(WnpSpacing.PageGap),
    ) {
        item { TopBar(title = "定时器") }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(WnpRadius.Sheet))
                    .background(WnpColors.Surface)
                    .padding(36.dp),
                contentAlignment = Alignment.Center,
            ) {
                val duration = state.timerState.durationMillis
                val progress = if (duration > 0L) {
                    state.timerState.remainingMillis.toFloat() / duration.toFloat()
                } else {
                    0f
                }
                TimerProgressRing(
                    progress = progress,
                    modifier = Modifier.size(196.dp),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$minutes", color = WnpColors.OnSurface, style = WnpTypography.DisplayLarge)
                        Text("分钟后停止", color = WnpColors.OnSurfaceVariant, style = WnpTypography.Body)
                    }
                }
            }
        }
        item {
            SectionHeader(title = "快速选择")
            Column(verticalArrangement = Arrangement.spacedBy(WnpSpacing.Md)) {
                Row(horizontalArrangement = Arrangement.spacedBy(WnpSpacing.Md)) {
                    timerPresetMinutes.take(3).forEach { minute ->
                        TimerPresetChip(
                            "$minute 分钟",
                            selected = minute * 60_000L == state.timerState.durationMillis,
                            onClick = { onPresetSelected(minute) },
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(WnpSpacing.Md)) {
                    timerPresetMinutes.drop(3).forEach { minute ->
                        TimerPresetChip(
                            if (minute == 60) "1 小时" else "2 小时",
                            selected = minute * 60_000L == state.timerState.durationMillis,
                            onClick = { onPresetSelected(minute) },
                        )
                    }
                }
            }
        }
        item {
            SectionHeader(title = "自定义")
            Row(
                horizontalArrangement = Arrangement.spacedBy(WnpSpacing.Lg),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WnpTextField(
                    value = customMinutes,
                    onValueChange = { value -> customMinutes = value.filter(Char::isDigit).take(4) },
                    modifier = Modifier.weight(1f),
                    placeholder = "分钟",
                )
                val customValue = customMinutes.toIntOrNull()
                PrimaryButton(
                    text = "应用",
                    enabled = customValue != null && customValue > 0,
                    onClick = {
                        customValue?.takeIf { it > 0 }?.let(onCustomDuration)
                        customMinutes = ""
                    },
                )
            }
        }
        item {
            SectionHeader(title = "淡出")
            SettingsRow(title = "结束前 5 分钟淡出", subtitle = "不改变已保存混音的音量")
        }
        item {
            SectionHeader(title = "结束后")
            SettingsRow(title = "停止播放", subtitle = "完成后自动停止所有声音", trailing = {
                Text("已选", color = WnpColors.Primary, style = WnpTypography.Label)
            })
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(WnpSpacing.Lg)) {
                PrimaryButton(
                    text = if (state.timerState.isActive) "取消" else "开始",
                    onClick = if (state.timerState.isActive) onCancel else onStart,
                    modifier = Modifier.weight(1f),
                )
                SecondaryButton(
                    text = "延长 10 分钟",
                    onClick = { onExtend(10) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun SavedMixesScreen(
    state: AppState,
    padding: PaddingValues,
    onPlayMix: (String) -> Unit,
    onDeleteMix: (String) -> Unit,
    onRenameMix: (String, String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onCreateMix: () -> Unit,
) {
    LazyColumn(
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(WnpSpacing.SectionGap),
    ) {
        item { TopBar(title = "已保存") }
        items(state.mixState.savedMixes) { mix ->
            MixCard(
                mix = mix,
                onPlay = { onPlayMix(mix.id) },
                onDelete = { onDeleteMix(mix.id) },
                onRename = { title -> onRenameMix(mix.id, title) },
                onToggleFavorite = { onToggleFavorite(mix.id) },
            )
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 104.dp)
                    .clip(RoundedCornerShape(WnpRadius.Card))
                    .background(WnpColors.SurfaceLow)
                    .clickable { onCreateMix() }
                    .padding(WnpSpacing.CardPadding),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(WnpSpacing.Sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppIcon(AppIconKind.Add, tint = WnpColors.Primary, modifier = Modifier.size(20.dp))
                    Text("新建混音", color = WnpColors.Primary, style = WnpTypography.Body)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    state: AppState,
    padding: PaddingValues,
    onStartLastMixChange: (Boolean) -> Unit,
) {
    val content = SettingsContent.releaseReady()
    LazyColumn(
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(WnpSpacing.SectionGap),
    ) {
        item { TopBar(title = "设置") }
        content.sections.forEach { section ->
            item { SectionHeader(title = section.title) }
            items(section.rows) { row ->
                SettingsContentRow(
                    row = row,
                    startLastMix = state.settings.startLastMix,
                    onStartLastMixChange = onStartLastMixChange,
                )
            }
        }
    }
}

@Composable
private fun SettingsContentRow(
    row: SettingsRowContent,
    startLastMix: Boolean,
    onStartLastMixChange: (Boolean) -> Unit,
) {
    when (row.kind) {
        SettingsRowKind.ReadOnly -> SettingsRow(title = row.title, subtitle = row.subtitle)
        SettingsRowKind.Toggle -> ToggleRow(
            title = row.title,
            subtitle = row.subtitle,
            checked = if (row.title == "启动时继续上次混音") startLastMix else false,
            onCheckedChange = if (row.title == "启动时继续上次混音") onStartLastMixChange else { _ -> },
        )
        SettingsRowKind.Disabled -> SettingsRow(
            title = row.title,
            subtitle = row.subtitle,
            trailing = {
                Text(
                    text = "暂不可用",
                    color = WnpColors.OnSurfaceVariant,
                    style = WnpTypography.Label,
                )
            },
        )
    }
}

@Composable
private fun NowPlayingHero(
    mix: SoundMix,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onToggleFavorite: () -> Unit,
    onMasterVolumeChange: (Float) -> Unit,
    onTimer: () -> Unit,
    onEdit: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(WnpRadius.Sheet))
            .background(WnpColors.Surface)
            .padding(horizontal = WnpSpacing.HeroPadding, vertical = WnpSpacing.CardPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SoundIcon(label = mix.title, active = isPlaying, iconKey = "mixer", modifier = Modifier.size(80.dp))
        Spacer(Modifier.height(WnpSpacing.Md))
        Text(mix.title, color = WnpColors.OnSurface, style = WnpTypography.Display)
        Text(
            mix.layers.joinToString(" · ") { SampleContent.soundName(it.soundId) },
            color = WnpColors.OnSurfaceVariant,
            style = WnpTypography.Body,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(WnpSpacing.Md))
        PrimaryButton(
            text = if (isPlaying) "暂停" else "播放",
            onClick = onTogglePlay,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(WnpSpacing.Sm))
        Row(horizontalArrangement = Arrangement.spacedBy(WnpSpacing.Lg)) {
            IconButton(onClick = onToggleFavorite) {
                AppIcon(AppIconKind.Favorite, tint = if (mix.isFavorite) WnpColors.Tertiary else WnpColors.OnSurface, modifier = Modifier.size(22.dp))
            }
            IconButton(onClick = onTimer) {
                AppIcon(AppIconKind.Timer, tint = WnpColors.OnSurface, modifier = Modifier.size(22.dp))
            }
            IconButton(onClick = onEdit) {
                AppIcon(AppIconKind.Mixer, tint = WnpColors.OnSurface, modifier = Modifier.size(22.dp))
            }
        }
        VolumeSlider(value = mix.masterVolume, onValueChange = onMasterVolumeChange)
    }
}

@Composable
private fun SoundPill(sound: Sound, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(WnpRadius.Button))
            .clickable(onClick = onClick)
            .padding(WnpSpacing.Xs),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SoundIcon(label = sound.name, active = false, selected = selected, iconKey = sound.iconKey)
        Text(
            sound.name,
            color = WnpColors.OnSurfaceVariant,
            style = WnpTypography.Label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CompactMixCard(mix: SoundMix, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(WnpRadius.Card))
            .background(WnpColors.SurfaceLow)
            .clickable(onClick = onClick)
            .padding(WnpSpacing.CardPadding),
    ) {
        Text(mix.title, color = WnpColors.OnSurface, style = WnpTypography.Label, maxLines = 1)
        Text("${mix.layers.size} 层", color = WnpColors.OnSurfaceVariant, style = WnpTypography.Label)
    }
}

@Composable
private fun LayerRow(
    layer: SoundLayer,
    isPlaying: Boolean,
    onVolumeChange: (Float) -> Unit,
    onMutedChange: (Boolean) -> Unit,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(WnpRadius.Card))
            .background(WnpColors.Surface)
            .padding(WnpSpacing.CardPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SoundIcon(
            label = SampleContent.soundName(layer.soundId),
            active = isPlaying && !layer.isMuted,
            selected = !layer.isMuted,
            iconKey = layer.soundId,
        )
        Column(modifier = Modifier.weight(1f).padding(horizontal = WnpSpacing.Lg)) {
            Text(SampleContent.soundName(layer.soundId), color = WnpColors.OnSurface, style = WnpTypography.Body)
            VolumeSlider(value = layer.volume, onValueChange = onVolumeChange)
        }
        IconButton(onClick = { onMutedChange(!layer.isMuted) }) {
            AppIcon(
                kind = if (layer.isMuted) AppIconKind.Volume else AppIconKind.Mute,
                tint = WnpColors.OnSurface,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.size(WnpSpacing.Xs))
        IconButton(onClick = onRemove) {
            AppIcon(AppIconKind.Delete, tint = WnpColors.IconMuted, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun SoundGridCard(
    sound: Sound,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .aspectRatio(1.22f)
            .clip(RoundedCornerShape(WnpRadius.Card))
            .background(if (active) WnpColors.SurfaceHigh else WnpColors.SurfaceLow)
            .clickable(onClick = onClick)
            .padding(WnpSpacing.CardPadding),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        SoundIcon(label = sound.name, active = false, selected = active, iconKey = sound.iconKey)
        Column {
            Text(sound.name, color = WnpColors.OnSurface, style = WnpTypography.Body, maxLines = 1)
            Text(sound.category.displayName, color = WnpColors.OnSurfaceVariant, style = WnpTypography.Label)
        }
    }
}

@Composable
private fun MixCard(
    mix: SoundMix,
    onPlay: () -> Unit,
    onDelete: () -> Unit,
    onRename: (String) -> Unit,
    onToggleFavorite: () -> Unit,
) {
    var editing by remember(mix.id) { mutableStateOf(false) }
    var title by remember(mix.id, mix.title) { mutableStateOf(mix.title) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(WnpRadius.Card))
            .background(WnpColors.Surface)
            .padding(WnpSpacing.CardPadding),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SoundIcon(
                label = mix.title,
                active = false,
                iconKey = mix.layers.firstOrNull()?.soundId ?: "mixer",
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onPlay)
                    .padding(horizontal = WnpSpacing.Lg, vertical = WnpSpacing.Md),
            ) {
                Text(mix.title, color = WnpColors.OnSurface, style = WnpTypography.Body, maxLines = 1)
                Text(
                    mix.layers.joinToString(" · ") { SampleContent.soundName(it.soundId) },
                    color = WnpColors.OnSurfaceVariant,
                    style = WnpTypography.Label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(onClick = onToggleFavorite) {
                AppIcon(AppIconKind.Favorite, tint = if (mix.isFavorite) WnpColors.Secondary else WnpColors.OnSurfaceVariant, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.size(WnpSpacing.Xs))
            IconButton(onClick = { editing = !editing }) {
                AppIcon(AppIconKind.Edit, tint = WnpColors.OnSurface, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.size(WnpSpacing.Xs))
            IconButton(onClick = onDelete) {
                AppIcon(AppIconKind.Delete, tint = WnpColors.IconMuted, modifier = Modifier.size(20.dp))
            }
        }
        if (editing) {
            Spacer(Modifier.height(WnpSpacing.Lg))
            Row(
                horizontalArrangement = Arrangement.spacedBy(WnpSpacing.Lg),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WnpTextField(
                    value = title,
                    onValueChange = { title = it.take(30) },
                    modifier = Modifier.weight(1f),
                )
                PrimaryButton(
                    text = "保存",
                    enabled = title.isNotBlank(),
                    onClick = {
                        onRename(title.trim())
                        editing = false
                    },
                )
            }
        }
    }
}
