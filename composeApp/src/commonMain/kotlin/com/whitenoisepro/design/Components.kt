package com.whitenoisepro.design

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton as MaterialIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = WnpDimens.MinTouchTarget + WnpSpacing.Xs),
        shape = RoundedCornerShape(WnpRadius.Button),
        colors = ButtonDefaults.buttonColors(
            containerColor = WnpColors.Primary,
            contentColor = WnpColors.Background,
            disabledContainerColor = WnpColors.SurfaceVariant,
            disabledContentColor = WnpColors.OnSurfaceVariant,
        ),
    ) {
        Text(text = text, style = WnpTypography.Label)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = WnpDimens.MinTouchTarget + WnpSpacing.Xs),
        shape = RoundedCornerShape(WnpRadius.Button),
        border = androidx.compose.foundation.BorderStroke(1.dp, WnpColors.Outline),
        colors = ButtonDefaults.buttonColors(
            containerColor = WnpColors.SurfaceLow,
            contentColor = WnpColors.Primary,
            disabledContainerColor = WnpColors.SurfaceLow,
            disabledContentColor = WnpColors.OnSurfaceVariant,
        ),
    ) {
        Text(text = text, style = WnpTypography.Label)
    }
}

@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .size(WnpDimens.MinTouchTarget)
            .clip(CircleShape)
            .background(WnpColors.SurfaceHigh)
            .border(1.dp, WnpColors.SurfaceVariant, CircleShape),
    ) {
        content()
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun VolumeSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Slider(
        value = value.coerceIn(0f, 1f),
        onValueChange = { onValueChange(it.coerceIn(0f, 1f)) },
        modifier = modifier.heightIn(min = WnpDimens.MinTouchTarget),
        thumb = {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(WnpColors.Primary),
            )
        },
        track = { sliderState ->
            val range = sliderState.valueRange.endInclusive - sliderState.valueRange.start
            val fraction = if (range > 0f) {
                ((sliderState.value - sliderState.valueRange.start) / range).coerceIn(0f, 1f)
            } else {
                0f
            }
            Box(modifier = Modifier.fillMaxWidth().height(6.dp)) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxWidth()
                        .height(2.dp)
                        .clip(RoundedCornerShape(WnpRadius.Chip))
                        .background(WnpColors.SurfaceVariant),
                )
                if (fraction > 0f) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxWidth(fraction)
                            .height(6.dp)
                            .clip(RoundedCornerShape(WnpRadius.Chip))
                            .background(WnpColors.Primary),
                    )
                }
            }
        },
    )
}

@Composable
fun WnpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder?.let {
            { Text(it, color = WnpColors.OnSurfaceVariant, style = WnpTypography.Body) }
        },
        singleLine = singleLine,
        shape = RoundedCornerShape(WnpRadius.Field),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = WnpColors.Primary,
            unfocusedBorderColor = WnpColors.Outline,
            focusedContainerColor = WnpColors.SurfaceLow,
            unfocusedContainerColor = WnpColors.SurfaceLow,
            cursorColor = WnpColors.Primary,
            focusedTextColor = WnpColors.OnSurface,
            unfocusedTextColor = WnpColors.OnSurface,
        ),
    )
}

@Composable
fun TimerProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val clamped = progress.coerceIn(0f, 1f)
    Box(
        modifier = modifier.drawBehind {
            val stroke = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            val inset = stroke.width / 2
            val arcSize = Size(size.width - stroke.width, size.height - stroke.width)
            drawArc(
                color = WnpColors.SurfaceVariant,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = arcSize,
                style = stroke,
            )
            if (clamped > 0f) {
                drawArc(
                    color = WnpColors.Primary,
                    startAngle = -90f,
                    sweepAngle = 360f * clamped,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                    size = arcSize,
                    style = stroke,
                )
            }
        },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
fun TimerPresetChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val background = if (selected) WnpColors.Primary else WnpColors.Surface
    val contentColor = if (selected) WnpColors.Background else WnpColors.OnSurface
    Box(
        modifier = modifier
            .heightIn(min = WnpDimens.MinTouchTarget)
            .clip(RoundedCornerShape(WnpRadius.Chip))
            .background(background)
            .border(1.dp, WnpColors.SurfaceVariant, RoundedCornerShape(WnpRadius.Chip))
            .clickable(onClick = onClick)
            .padding(horizontal = WnpSpacing.Lg, vertical = WnpSpacing.Sm),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = contentColor, style = WnpTypography.Label)
    }
}

@Composable
fun SoundIcon(
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    iconKey: String = label,
    selected: Boolean = false,
) {
    val background = when {
        active -> WnpColors.Primary
        else -> WnpColors.SurfaceHigh
    }
    val contentColor = when {
        active -> WnpColors.Background
        selected -> WnpColors.Primary
        else -> WnpColors.OnSurface
    }
    val borderColor = when {
        active -> WnpColors.Primary
        selected -> WnpColors.Primary
        else -> WnpColors.SurfaceVariant
    }
    val haloModifier = if (active) {
        val transition = rememberInfiniteTransition(label = "soundIconBreath")
        val haloAlpha by transition.animateFloat(
            initialValue = WnpMotion.HaloMinAlpha,
            targetValue = WnpMotion.HaloMaxAlpha,
            animationSpec = infiniteRepeatable(
                animation = tween(WnpMotion.BreathDurationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "haloAlpha",
        )
        val haloScale by transition.animateFloat(
            initialValue = 1.06f,
            targetValue = 1.30f,
            animationSpec = infiniteRepeatable(
                animation = tween(WnpMotion.BreathDurationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "haloScale",
        )
        Modifier.drawBehind {
            drawCircle(
                color = WnpColors.Primary.copy(alpha = haloAlpha),
                radius = size.minDimension / 2f * haloScale,
            )
        }
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .size(48.dp)
            .then(haloModifier)
            .clip(CircleShape)
            .background(background)
            .border(1.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(
            kind = soundIconKind(iconKey),
            tint = contentColor,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
fun SettingsRow(
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val rowModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }
    Row(
        modifier = rowModifier
            .fillMaxWidth()
            .heightIn(min = 68.dp)
            .padding(vertical = WnpSpacing.Lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = WnpColors.OnSurface,
                style = WnpTypography.Body,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = WnpColors.OnSurfaceVariant,
                    style = WnpTypography.Label,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(WnpSpacing.Lg))
            trailing()
        }
    }
}

@Composable
fun ToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    SettingsRow(
        title = title,
        subtitle = subtitle,
        trailing = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        },
        modifier = modifier,
    )
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = WnpSpacing.Xl, bottom = WnpSpacing.Md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = title, color = WnpColors.OnSurface, style = WnpTypography.Title)
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(text = actionText, color = WnpColors.Primary, style = WnpTypography.Label)
            }
        }
    }
}
