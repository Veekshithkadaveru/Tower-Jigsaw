package app.krafted.towerjigsaw.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun TimerBar(
    timeElapsedMs: Long,
    targetTimeMs: Long,
    modifier: Modifier = Modifier
) {
    val remainingMs = (targetTimeMs - timeElapsedMs).coerceAtLeast(0)
    val fraction = remainingMs.toFloat() / targetTimeMs.toFloat().coerceAtLeast(1f)
    
    val isUrgent = fraction < 0.25f

    val color by animateColorAsState(
        targetValue = when {
            fraction > 0.5f -> Color(0xFF4CAF50) // Green
            fraction > 0.25f -> Color(0xFFFFEB3B) // Yellow
            else -> Color(0xFFF44336) // Red
        },
        label = "TimerColor"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "timerPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(Color(0xFF0F0F22), RoundedCornerShape(4.dp))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction)
                .graphicsLayer { alpha = if (isUrgent) pulseAlpha else 1f }
                .shadow(elevation = 6.dp, spotColor = color, ambientColor = color, shape = RoundedCornerShape(4.dp))
                .background(color, RoundedCornerShape(4.dp))
        )
    }
}
