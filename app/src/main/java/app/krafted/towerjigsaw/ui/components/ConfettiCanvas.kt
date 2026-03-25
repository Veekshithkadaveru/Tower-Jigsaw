package app.krafted.towerjigsaw.ui.components

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

private data class ConfettiParticle(
    val xFraction: Float,       // 0..1 normalized start x
    val vx: Float,              // horizontal velocity (fraction/sec equivalent)
    val vy: Float,              // vertical velocity
    val color: Color,
    val pieceSize: Float,       // pixels at 1.0 scale
    val rotation: Float,        // initial rotation degrees
    val rotationSpeed: Float,   // degrees per unit time
    val shape: Int
)

private val confettiColors = listOf(
    Color(0xFFFFD54F),
    Color(0xFFFF7043),
    Color(0xFF42A5F5),
    Color(0xFF66BB6A),
    Color(0xFFEC407A),
    Color(0xFFAB47BC),
    Color(0xFF26C6DA),
    Color(0xFFFFF176),
)

@Composable
fun ConfettiCanvas(modifier: Modifier = Modifier) {
    val rng = remember { Random(System.currentTimeMillis()) }

    val particles = remember {
        List(60) {
            ConfettiParticle(
                xFraction = rng.nextFloat(),
                vx = (rng.nextFloat() - 0.5f) * 0.5f,
                vy = 0.25f + rng.nextFloat() * 0.55f,
                color = confettiColors[rng.nextInt(confettiColors.size)],
                pieceSize = 8f + rng.nextFloat() * 10f,
                rotation = rng.nextFloat() * 360f,
                rotationSpeed = (rng.nextFloat() - 0.5f) * 600f,
                shape = rng.nextInt(3)
            )
        }
    }

    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 4500)
        ) { value, _ ->
            progress = value
        }
    }

    Canvas(modifier = modifier) {
        val t = progress
        particles.forEach { p ->
            // Parabolic fall: y = vy*t + 0.5*gravity*t²; start slightly above screen
            val px = (p.xFraction + p.vx * t + 0.02f * kotlin.math.sin(t * p.rotationSpeed * 0.1f)) * size.width
            val py = (-0.08f + p.vy * t + 0.4f * t * t) * size.height

            if (py > size.height + 20f || px < -20f || px > size.width + 20f) return@forEach

            val alpha = (1f - (t * 0.8f).coerceAtMost(1f)).coerceAtLeast(0f)
            val color = p.color.copy(alpha = alpha)
            val rot = p.rotation + p.rotationSpeed * t

            rotate(degrees = rot, pivot = Offset(px, py)) {
                when (p.shape) {
                    0 -> {
                        drawCircle(
                            color = color,
                            radius = p.pieceSize / 2f,
                            center = Offset(px, py)
                        )
                    }
                    1 -> {
                        drawRect(
                            color = color,
                            topLeft = Offset(px - p.pieceSize / 2f, py - p.pieceSize / 4f),
                            size = Size(p.pieceSize, p.pieceSize / 2f)
                        )
                    }
                    2 -> {
                        val halfSize = p.pieceSize / 2f
                        drawPath(
                            path = Path().apply {
                                moveTo(px, py - halfSize)
                                lineTo(px - halfSize, py + halfSize / 2f)
                                lineTo(px + halfSize, py + halfSize / 2f)
                                close()
                            },
                            color = color
                        )
                    }
                }
            }
        }
    }
}
