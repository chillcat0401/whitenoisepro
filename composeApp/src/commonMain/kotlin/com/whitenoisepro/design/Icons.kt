package com.whitenoisepro.design

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke

enum class AppIconKind {
    Add,
    Delete,
    Edit,
    Fan,
    Favorite,
    Fireplace,
    Forest,
    Home,
    Library,
    Mixer,
    Mute,
    Noise,
    Ocean,
    Pause,
    Play,
    Rain,
    Saved,
    Settings,
    Timer,
    Volume,
}

fun soundIconKind(iconKey: String): AppIconKind = when (iconKey) {
    "fan" -> AppIconKind.Fan
    "rain" -> AppIconKind.Rain
    "ocean" -> AppIconKind.Ocean
    "forest" -> AppIconKind.Forest
    "fireplace" -> AppIconKind.Fireplace
    "white_noise", "pink_noise", "brown_noise" -> AppIconKind.Noise
    else -> AppIconKind.Mixer
}

@Composable
fun AppIcon(
    kind: AppIconKind,
    modifier: Modifier = Modifier,
    tint: Color = WnpColors.OnSurface,
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val stroke = Stroke(
            width = width * 0.075f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        val thinStroke = Stroke(
            width = width * 0.055f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        )
        fun p(x: Float, y: Float) = Offset(width * x, height * y)
        fun line(x1: Float, y1: Float, x2: Float, y2: Float) = drawLine(tint, p(x1, y1), p(x2, y2), stroke.width, StrokeCap.Round)
        fun circle(cx: Float, cy: Float, radius: Float, style: Stroke = stroke) = drawCircle(tint, width * radius, p(cx, cy), style = style)

        when (kind) {
            AppIconKind.Play -> {
                val path = Path().apply {
                    moveTo(width * 0.34f, height * 0.24f)
                    lineTo(width * 0.34f, height * 0.76f)
                    lineTo(width * 0.74f, height * 0.5f)
                    close()
                }
                drawPath(path, tint)
            }
            AppIconKind.Pause -> {
                line(0.38f, 0.25f, 0.38f, 0.75f)
                line(0.62f, 0.25f, 0.62f, 0.75f)
            }
            AppIconKind.Add -> {
                line(0.5f, 0.25f, 0.5f, 0.75f)
                line(0.25f, 0.5f, 0.75f, 0.5f)
            }
            AppIconKind.Delete -> {
                line(0.28f, 0.34f, 0.72f, 0.34f)
                line(0.37f, 0.34f, 0.4f, 0.78f)
                line(0.63f, 0.34f, 0.6f, 0.78f)
                line(0.42f, 0.22f, 0.58f, 0.22f)
            }
            AppIconKind.Edit -> {
                line(0.3f, 0.7f, 0.67f, 0.33f)
                line(0.58f, 0.24f, 0.76f, 0.42f)
                line(0.25f, 0.75f, 0.42f, 0.7f)
            }
            AppIconKind.Favorite -> {
                val path = Path().apply {
                    moveTo(width * 0.5f, height * 0.78f)
                    cubicTo(width * 0.18f, height * 0.56f, width * 0.2f, height * 0.24f, width * 0.39f, height * 0.28f)
                    cubicTo(width * 0.45f, height * 0.29f, width * 0.48f, height * 0.34f, width * 0.5f, height * 0.39f)
                    cubicTo(width * 0.52f, height * 0.34f, width * 0.55f, height * 0.29f, width * 0.61f, height * 0.28f)
                    cubicTo(width * 0.8f, height * 0.24f, width * 0.82f, height * 0.56f, width * 0.5f, height * 0.78f)
                }
                drawPath(path, tint, style = stroke)
            }
            AppIconKind.Timer -> {
                circle(0.5f, 0.55f, 0.28f)
                line(0.5f, 0.55f, 0.5f, 0.38f)
                line(0.5f, 0.55f, 0.63f, 0.62f)
                line(0.42f, 0.18f, 0.58f, 0.18f)
            }
            AppIconKind.Settings -> {
                circle(0.5f, 0.5f, 0.14f)
                for (index in 0 until 8) {
                    val angle = (Math.PI * 2 * index / 8).toFloat()
                    val start = Offset(width * (0.5f + kotlin.math.cos(angle) * 0.25f), height * (0.5f + kotlin.math.sin(angle) * 0.25f))
                    val end = Offset(width * (0.5f + kotlin.math.cos(angle) * 0.35f), height * (0.5f + kotlin.math.sin(angle) * 0.35f))
                    drawLine(tint, start, end, thinStroke.width, StrokeCap.Round)
                }
            }
            AppIconKind.Mixer -> {
                line(0.28f, 0.22f, 0.28f, 0.78f)
                line(0.5f, 0.22f, 0.5f, 0.78f)
                line(0.72f, 0.22f, 0.72f, 0.78f)
                circle(0.28f, 0.42f, 0.055f, thinStroke)
                circle(0.5f, 0.62f, 0.055f, thinStroke)
                circle(0.72f, 0.34f, 0.055f, thinStroke)
            }
            AppIconKind.Home -> {
                val path = Path().apply {
                    moveTo(width * 0.22f, height * 0.48f)
                    lineTo(width * 0.5f, height * 0.24f)
                    lineTo(width * 0.78f, height * 0.48f)
                    lineTo(width * 0.72f, height * 0.78f)
                    lineTo(width * 0.32f, height * 0.78f)
                    close()
                }
                drawPath(path, tint, style = stroke)
            }
            AppIconKind.Library -> {
                line(0.26f, 0.25f, 0.26f, 0.75f)
                line(0.5f, 0.22f, 0.5f, 0.78f)
                line(0.74f, 0.28f, 0.74f, 0.72f)
            }
            AppIconKind.Saved -> {
                val path = Path().apply {
                    moveTo(width * 0.28f, height * 0.22f)
                    lineTo(width * 0.72f, height * 0.22f)
                    lineTo(width * 0.72f, height * 0.78f)
                    lineTo(width * 0.5f, height * 0.64f)
                    lineTo(width * 0.28f, height * 0.78f)
                    close()
                }
                drawPath(path, tint, style = stroke)
            }
            AppIconKind.Volume -> {
                line(0.24f, 0.56f, 0.38f, 0.56f)
                line(0.38f, 0.56f, 0.54f, 0.38f)
                line(0.54f, 0.38f, 0.54f, 0.72f)
                line(0.54f, 0.72f, 0.38f, 0.56f)
                line(0.66f, 0.42f, 0.72f, 0.66f)
            }
            AppIconKind.Mute -> {
                line(0.24f, 0.56f, 0.38f, 0.56f)
                line(0.38f, 0.56f, 0.54f, 0.38f)
                line(0.54f, 0.38f, 0.54f, 0.72f)
                line(0.26f, 0.26f, 0.76f, 0.76f)
            }
            AppIconKind.Noise -> {
                line(0.25f, 0.42f, 0.25f, 0.62f)
                line(0.38f, 0.32f, 0.38f, 0.72f)
                line(0.51f, 0.24f, 0.51f, 0.8f)
                line(0.64f, 0.35f, 0.64f, 0.68f)
                line(0.77f, 0.45f, 0.77f, 0.6f)
            }
            AppIconKind.Fan -> {
                circle(0.5f, 0.5f, 0.07f, thinStroke)
                drawArc(tint, 215f, 85f, false, topLeft = p(0.22f, 0.22f), size = Size(width * 0.36f, height * 0.36f), style = stroke)
                drawArc(tint, -25f, 85f, false, topLeft = p(0.42f, 0.22f), size = Size(width * 0.36f, height * 0.36f), style = stroke)
                drawArc(tint, 95f, 85f, false, topLeft = p(0.32f, 0.46f), size = Size(width * 0.36f, height * 0.36f), style = stroke)
            }
            AppIconKind.Rain -> {
                line(0.3f, 0.28f, 0.7f, 0.28f)
                line(0.28f, 0.45f, 0.22f, 0.62f)
                line(0.48f, 0.43f, 0.42f, 0.68f)
                line(0.68f, 0.45f, 0.62f, 0.62f)
            }
            AppIconKind.Ocean -> {
                drawArc(tint, 180f, 180f, false, topLeft = p(0.18f, 0.38f), size = Size(width * 0.34f, height * 0.24f), style = stroke)
                drawArc(tint, 180f, 180f, false, topLeft = p(0.48f, 0.38f), size = Size(width * 0.34f, height * 0.24f), style = stroke)
                drawArc(tint, 180f, 180f, false, topLeft = p(0.28f, 0.58f), size = Size(width * 0.34f, height * 0.18f), style = thinStroke)
            }
            AppIconKind.Forest -> {
                line(0.28f, 0.76f, 0.4f, 0.28f)
                line(0.4f, 0.28f, 0.52f, 0.76f)
                line(0.5f, 0.76f, 0.62f, 0.36f)
                line(0.62f, 0.36f, 0.76f, 0.76f)
                line(0.22f, 0.76f, 0.82f, 0.76f)
            }
            AppIconKind.Fireplace -> {
                val path = Path().apply {
                    moveTo(width * 0.5f, height * 0.78f)
                    cubicTo(width * 0.27f, height * 0.64f, width * 0.38f, height * 0.44f, width * 0.48f, height * 0.28f)
                    cubicTo(width * 0.5f, height * 0.44f, width * 0.73f, height * 0.5f, width * 0.5f, height * 0.78f)
                }
                drawPath(path, tint, style = stroke)
                line(0.3f, 0.82f, 0.7f, 0.82f)
            }
        }
    }
}
