package com.whitenoisepro.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton as MaterialIconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            .border(1.dp, WnpColors.Outline, CircleShape),
    ) {
        content()
    }
}

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
        colors = SliderDefaults.colors(
            thumbColor = WnpColors.Primary,
            activeTrackColor = WnpColors.Primary,
            inactiveTrackColor = WnpColors.SurfaceVariant,
        ),
    )
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
) {
    val background = if (active) WnpColors.Primary else WnpColors.SurfaceHigh
    val contentColor = if (active) WnpColors.Background else WnpColors.OnSurface
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(background)
            .border(1.dp, if (active) WnpColors.Primary else WnpColors.Outline, CircleShape),
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
            .heightIn(min = 64.dp)
            .padding(vertical = WnpSpacing.Md),
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
