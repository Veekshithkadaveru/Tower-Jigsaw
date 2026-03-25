package app.krafted.towerjigsaw.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.towerjigsaw.game.Difficulty
import app.krafted.towerjigsaw.game.PuzzleInfo
import app.krafted.towerjigsaw.game.Puzzles
import app.krafted.towerjigsaw.ui.theme.DisplayFont

private val BgTop = Color(0xFF080810)
private val BgMid = Color(0xFF0F0F22)
private val BgBottom = Color(0xFF151530)
private val CardBg = Color(0xFF13132B)
private val CardBorder = Color(0xFF252548)
private val SurfaceDim = Color(0xFF0E0E20)
private val Gold = Color(0xFFFFD54F)
private val GoldBright = Color(0xFFFFF176)
private val GoldDeep = Color(0xFFBFA030)
private val RelaxedActive = Color(0xFF1B5E20)
private val TimedActive = Color(0xFF8B1A1A)
private val TextPrimary = Color(0xFFF0F0F8)
private val TextSecondary = Color(0xFF9090B0)
private val TextTertiary = Color(0xFF606080)
private val DividerColor = Color(0xFF1E1E40)

@Composable
fun HomeScreen(
    isTimedMode: Boolean,
    completedKeys: Set<String>,
    onPuzzleSelected: (puzzleId: Int) -> Unit,
    onToggleMode: () -> Unit,
    onLeaderboardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgTop, BgMid, BgBottom)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Header(onLeaderboardClick = onLeaderboardClick)

            Spacer(modifier = Modifier.height(20.dp))

            ModeToggle(isTimedMode = isTimedMode, onToggle = onToggleMode)

            Spacer(modifier = Modifier.height(28.dp))

            SectionHeader()

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(Puzzles.all) { index, puzzle ->
                    PuzzleCard(
                        puzzle = puzzle,
                        index = index,
                        completedKeys = completedKeys,
                        onClick = { onPuzzleSelected(puzzle.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(onLeaderboardClick: () -> Unit) {
    val titleText = "TOWER"
    val accentText = "JIGSAW"

    val enterAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800, delayMillis = 200),
        label = "enterAlpha"
    )
    val enterSlide by animateFloatAsState(
        targetValue = 0f,
        animationSpec = spring(
            dampingRatio = 0.7f,
            stiffness = Spring.StiffnessLow
        ),
        label = "enterSlide"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600, delayMillis = 600),
        label = "subtitleAlpha"
    )
    val lineWidth by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(500, delayMillis = 800),
        label = "lineWidth"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "headerAnim")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = enterAlpha
                        translationY = (1f - enterAlpha) * 20f
                    }
            ) {
                Text(
                    text = titleText,
                    style = TextStyle(
                        fontFamily = DisplayFont,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 3.sp,
                        shadow = Shadow(
                            color = Color.White.copy(alpha = 0.06f),
                            offset = Offset(0f, 2f),
                            blurRadius = 8f
                        )
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = accentText,
                    style = TextStyle(
                        fontFamily = DisplayFont,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light,
                        color = Gold,
                        letterSpacing = 4.sp,
                        shadow = Shadow(
                            color = Gold.copy(alpha = glowAlpha),
                            offset = Offset(0f, 0f),
                            blurRadius = 24f
                        )
                    ),
                    modifier = Modifier
                        .graphicsLayer { alpha = 0.99f }
                        .drawWithContent {
                            drawContent()
                            val shimmerBrush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    GoldBright.copy(alpha = 0.5f),
                                    Color.White.copy(alpha = 0.15f),
                                    GoldBright.copy(alpha = 0.5f),
                                    Color.Transparent
                                ),
                                start = Offset(size.width * shimmerOffset, 0f),
                                end = Offset(
                                    size.width * (shimmerOffset + 0.35f),
                                    size.height * 0.8f
                                )
                            )
                            drawRect(
                                brush = shimmerBrush,
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.graphicsLayer { alpha = subtitleAlpha }
            ) {
                Box(
                    modifier = Modifier
                        .width(24.dp * lineWidth)
                        .height(1.5.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Gold.copy(alpha = 0.6f), Gold.copy(alpha = 0.1f))
                            ),
                            RoundedCornerShape(1.dp)
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Piece together the castles",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextTertiary,
                        letterSpacing = 0.3.sp,
                        fontSize = 12.sp
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .graphicsLayer {
                    alpha = enterAlpha
                    scaleX = 0.8f + enterAlpha * 0.2f
                    scaleY = 0.8f + enterAlpha * 0.2f
                }
        ) {
            IconButton(
                onClick = onLeaderboardClick,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(Gold.copy(alpha = 0.1f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            listOf(Gold.copy(alpha = 0.3f), Gold.copy(alpha = 0.08f))
                        ),
                        CircleShape
                    )
            ) {
                Text(text = "\uD83C\uDFC6", fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun SectionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(20.dp)
                .height(2.dp)
                .background(Gold.copy(alpha = 0.35f), RoundedCornerShape(1.dp))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "SELECT PUZZLE",
            style = MaterialTheme.typography.labelLarge.copy(
                color = TextTertiary,
                letterSpacing = 3.sp,
                fontSize = 11.sp
            )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(listOf(DividerColor, Color.Transparent))
                )
        )
    }
}

@Composable
private fun ModeToggle(isTimedMode: Boolean, onToggle: () -> Unit) {
    val relaxedBg by animateColorAsState(
        if (!isTimedMode) RelaxedActive else Color.Transparent,
        animationSpec = tween(300), label = "relaxedBg"
    )
    val timedBg by animateColorAsState(
        if (isTimedMode) TimedActive else Color.Transparent,
        animationSpec = tween(300), label = "timedBg"
    )
    val relaxedTextColor by animateColorAsState(
        if (!isTimedMode) Color.White else TextTertiary,
        animationSpec = tween(300), label = "relaxedText"
    )
    val timedTextColor by animateColorAsState(
        if (isTimedMode) Color.White else TextTertiary,
        animationSpec = tween(300), label = "timedText"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDim)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(relaxedBg)
                    .clickable(onClick = { if (isTimedMode) onToggle() }),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(6.dp)) {
                        drawCircle(color = if (!isTimedMode) Color(0xFF4CAF50) else Color.Transparent)
                    }
                    if (!isTimedMode) Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RELAXED",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = relaxedTextColor,
                            letterSpacing = 1.5.sp,
                            fontSize = 12.sp
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(timedBg)
                    .clickable(onClick = { if (!isTimedMode) onToggle() }),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(6.dp)) {
                        drawCircle(color = if (isTimedMode) Color(0xFFF44336) else Color.Transparent)
                    }
                    if (isTimedMode) Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TIMED",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = timedTextColor,
                            letterSpacing = 1.5.sp,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PuzzleCard(
    puzzle: PuzzleInfo,
    index: Int,
    completedKeys: Set<String>,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(
        puzzle.imageResName, "drawable", context.packageName
    )
    val completedCount = Difficulty.entries.count { d ->
        completedKeys.contains("${puzzle.id}_${d.name}")
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "cardScale"
    )
    val entryAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(450, delayMillis = index * 80),
        label = "entryAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = entryAlpha
                translationY = (1f - entryAlpha) * 24f
            }
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = puzzle.accentColor.copy(alpha = 0.15f),
                spotColor = puzzle.accentColor.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(16.dp))
            .drawBehind {
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        listOf(puzzle.accentColor.copy(alpha = 0.08f), Color.Transparent)
                    ),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .background(CardBg, RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        puzzle.accentColor.copy(alpha = 0.35f),
                        CardBorder.copy(alpha = 0.4f),
                        CardBorder.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(48.dp)
                .align(Alignment.CenterStart)
                .background(
                    Brush.verticalGradient(
                        listOf(puzzle.accentColor.copy(alpha = 0.8f), puzzle.accentColor.copy(alpha = 0.2f))
                    ),
                    RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(
                                puzzle.accentColor.copy(alpha = 0.1f),
                                puzzle.accentColor.copy(alpha = 0.03f)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            listOf(
                                puzzle.accentColor.copy(alpha = 0.3f),
                                puzzle.accentColor.copy(alpha = 0.08f)
                            )
                        ),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (resId != 0) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = puzzle.name,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = String.format("%02d", index + 1),
                        style = MaterialTheme.typography.displayMedium.copy(
                            color = puzzle.accentColor.copy(alpha = 0.6f),
                            fontSize = 24.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = puzzle.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressDots(completedCount = completedCount, accentColor = puzzle.accentColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$completedCount of 3",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextTertiary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Difficulty.entries.forEach { difficulty ->
                        DifficultyChip(
                            puzzle = puzzle,
                            difficulty = difficulty,
                            completedKeys = completedKeys
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .alpha(0.4f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "\u203A",
                    fontSize = 22.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}

@Composable
private fun ProgressDots(completedCount: Int, accentColor: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(3) { i ->
            Canvas(modifier = Modifier.size(5.dp)) {
                drawCircle(
                    color = if (i < completedCount) accentColor else Color.White.copy(alpha = 0.12f)
                )
            }
        }
    }
}

@Composable
private fun DifficultyChip(
    puzzle: PuzzleInfo,
    difficulty: Difficulty,
    completedKeys: Set<String>
) {
    val key = "${puzzle.id}_${difficulty.name}"
    val completed = completedKeys.contains(key)
    val unlocked = when (difficulty) {
        Difficulty.EASY -> true
        Difficulty.MEDIUM -> completedKeys.contains("${puzzle.id}_${Difficulty.EASY.name}")
        Difficulty.HARD -> completedKeys.contains("${puzzle.id}_${Difficulty.MEDIUM.name}")
    }

    val chipBg = when {
        completed -> puzzle.accentColor.copy(alpha = 0.15f)
        !unlocked -> Color.White.copy(alpha = 0.03f)
        else -> Color.White.copy(alpha = 0.06f)
    }
    val chipBorder = when {
        completed -> puzzle.accentColor.copy(alpha = 0.4f)
        !unlocked -> Color.White.copy(alpha = 0.06f)
        else -> Color.White.copy(alpha = 0.12f)
    }
    val textColor = when {
        completed -> puzzle.accentColor
        !unlocked -> Color.White.copy(alpha = 0.2f)
        else -> Color.White.copy(alpha = 0.5f)
    }
    val icon = when {
        completed -> " \u2713"
        !unlocked -> " \uD83D\uDD12"
        else -> ""
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(chipBg)
            .border(0.5.dp, chipBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = difficulty.displayName + icon,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontWeight = if (completed) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}
