package app.krafted.towerjigsaw.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.towerjigsaw.R
import app.krafted.towerjigsaw.ui.theme.DisplayFont

private val SpBgTop      = Color(0xFF06060F)
private val SpBgMid      = Color(0xFF0A0A1A)
private val SpBgBottom   = Color(0xFF0E0E22)
private val SpGold       = Color(0xFFFFD54F)
private val SpGoldBright = Color(0xFFFFF176)
private val SpCardBg     = Color(0xFF13132B)
private val SpCardBorder = Color(0xFF252548)
private val SpTextPrimary  = Color(0xFFF0F0F8)
private val SpTextTertiary = Color(0xFF5A5A7A)

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val iconAlpha by animateFloatAsState(
        targetValue    = if (visible) 1f else 0f,
        animationSpec  = tween(900, easing = FastOutSlowInEasing),
        label          = "iconAlpha"
    )
    val iconScale by animateFloatAsState(
        targetValue   = if (visible) 1f else 0.80f,
        animationSpec = spring(dampingRatio = 0.70f, stiffness = 110f),
        label         = "iconScale"
    )
    val dividerScale by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(650, delayMillis = 520, easing = FastOutSlowInEasing),
        label         = "divider"
    )
    val titleAlpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(650, delayMillis = 620, easing = FastOutSlowInEasing),
        label         = "titleAlpha"
    )
    val titleSlide by animateFloatAsState(
        targetValue   = if (visible) 0f else 16f,
        animationSpec = tween(650, delayMillis = 620, easing = FastOutSlowInEasing),
        label         = "titleSlide"
    )
    val tagAlpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(550, delayMillis = 950, easing = FastOutSlowInEasing),
        label         = "tagAlpha"
    )

    val inf = rememberInfiniteTransition(label = "splashInf")

    val shimmerOffset by inf.animateFloat(
        initialValue  = -1f,
        targetValue   = 2.5f,
        animationSpec = infiniteRepeatable(
            tween(3600, easing = LinearEasing), RepeatMode.Restart
        ),
        label = "shimmer"
    )
    val goldGlow by inf.animateFloat(
        initialValue  = 0.20f,
        targetValue   = 0.55f,
        animationSpec = infiniteRepeatable(
            tween(2600, easing = LinearEasing), RepeatMode.Reverse
        ),
        label = "goldGlow"
    )

    Box(
        modifier          = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(SpBgTop, SpBgMid, SpBgBottom))),
        contentAlignment  = Alignment.Center
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF18104A).copy(alpha = 0.90f), Color.Transparent),
                    center = Offset(size.width * 0.50f, size.height * 0.40f),
                    radius = size.width * 1.05f
                ),
                size = size
            )
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(SpGold.copy(alpha = 0.040f), Color.Transparent),
                    center = Offset(size.width * 0.50f, size.height * 0.37f),
                    radius = size.width * 0.48f
                ),
                size = size
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .graphicsLayer {
                        scaleX = iconScale
                        scaleY = iconScale
                        alpha  = iconAlpha
                    }
                    .shadow(
                        elevation    = 48.dp,
                        shape        = RoundedCornerShape(32.dp),
                        ambientColor = SpGold.copy(alpha = 0.22f),
                        spotColor    = Color(0xFF5030CC).copy(alpha = 0.18f)
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF1F1C46),
                                SpCardBg,
                                Color(0xFF0D0D20)
                            ),
                            start = Offset(0f, 0f),
                            end   = Offset(420f, 420f)
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                SpGold.copy(alpha = 0.60f),
                                Color.White.copy(alpha = 0.07f),
                                SpCardBorder.copy(alpha = 0.25f)
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter            = painterResource(id = R.drawable.tower_icon),
                    contentDescription = "Tower Jigsaw",
                    modifier           = Modifier.size(96.dp),
                    contentScale       = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(0.8.dp)
                    .graphicsLayer { scaleX = dividerScale }
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                SpGold.copy(alpha = 0.50f),
                                Color.Transparent
                            )
                        ),
                        RoundedCornerShape(1.dp)
                    )
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.graphicsLayer {
                    alpha        = titleAlpha
                    translationY = titleSlide
                }
            ) {
                Text(
                    text  = "TOWER",
                    style = TextStyle(
                        fontFamily    = DisplayFont,
                        fontSize      = 32.sp,
                        fontWeight    = FontWeight.Black,
                        color         = SpTextPrimary,
                        letterSpacing = 5.sp
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text  = "JIGSAW",
                    style = TextStyle(
                        fontFamily    = DisplayFont,
                        fontSize      = 32.sp,
                        fontWeight    = FontWeight.Light,
                        color         = SpGold,
                        letterSpacing = 6.sp,
                        shadow        = Shadow(
                            color      = SpGold.copy(alpha = goldGlow),
                            offset     = Offset(0f, 0f),
                            blurRadius = 28f
                        )
                    ),
                    modifier = Modifier
                        .graphicsLayer { alpha = 0.99f }
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        SpGoldBright.copy(alpha = 0.55f),
                                        Color.White.copy(alpha = 0.18f),
                                        SpGoldBright.copy(alpha = 0.55f),
                                        Color.Transparent
                                    ),
                                    start = Offset(size.width * shimmerOffset, 0f),
                                    end   = Offset(
                                        size.width * (shimmerOffset + 0.35f),
                                        size.height * 0.8f
                                    )
                                ),
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text     = "Piece together the castles",
                style    = TextStyle(
                    fontSize      = 12.sp,
                    color         = SpTextTertiary,
                    letterSpacing = 1.2.sp
                ),
                modifier = Modifier.graphicsLayer { alpha = tagAlpha }
            )
        }
    }
}
