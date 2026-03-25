package app.krafted.towerjigsaw.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.towerjigsaw.data.db.PuzzleResult
import app.krafted.towerjigsaw.game.Difficulty
import app.krafted.towerjigsaw.game.PuzzleEngine
import app.krafted.towerjigsaw.game.Puzzles
import app.krafted.towerjigsaw.ui.theme.DisplayFont

private val PsBgTop = Color(0xFF080810)
private val PsBgMid = Color(0xFF0F0F22)
private val PsBgBottom = Color(0xFF151530)
private val PsGoldBright = Color(0xFFFFF176)
private val PsGold = Color(0xFFFFD54F)
private val PsTextPrimary = Color(0xFFF0F0F8)
private val PsTextSecondary = Color(0xFF9090B0)
private val PsTextTertiary = Color(0xFF606080)
private val PsCardBg = Color(0xFF13132B)
private val PsCardBorder = Color(0xFF252548)
private val PsDivider = Color(0xFF1E1E40)

@Composable
fun PuzzleSelectScreen(
    puzzleId: Int,
    isTimedMode: Boolean,
    completedKeys: Set<String>,
    bestResults: Map<String, PuzzleResult?>,
    onDifficultySelected: (difficulty: Difficulty) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val puzzle = Puzzles.getById(puzzleId)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(PsBgTop, PsBgMid, PsBgBottom)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            TopBar(
                puzzleName = puzzle.name,
                puzzleId = puzzleId,
                onBack = onBack
            )

            PuzzleHeroImage(
                imageResName = puzzle.imageResName,
                puzzleName = puzzle.name,
                accentColor = puzzle.accentColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            DifficultySection(
                accentColor = puzzle.accentColor
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Difficulty.entries.forEachIndexed { index, difficulty ->
                    val key = "${puzzleId}_${difficulty.name}"
                    val isCompleted = completedKeys.contains(key)
                    val isUnlocked = when (difficulty) {
                        Difficulty.EASY -> true
                        Difficulty.MEDIUM -> completedKeys.contains("${puzzleId}_${Difficulty.EASY.name}")
                        Difficulty.HARD -> completedKeys.contains("${puzzleId}_${Difficulty.MEDIUM.name}")
                    }
                    val bestResult = bestResults[difficulty.name]

                    DifficultyCard(
                        difficulty = difficulty,
                        accentColor = puzzle.accentColor,
                        isCompleted = isCompleted,
                        isUnlocked = isUnlocked,
                        bestResult = bestResult,
                        entryDelayMs = 300 + index * 100,
                        onClick = { if (isUnlocked) onDifficultySelected(difficulty) }
                    )
                }
            }

            if (isTimedMode) {
                Spacer(modifier = Modifier.height(20.dp))
                TimedModeBadge()
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TopBar(
    puzzleName: String,
    puzzleId: Int,
    onBack: () -> Unit
) {
    val enterAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(700, delayMillis = 100),
        label = "topBarAlpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "topBarAnim")
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
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .graphicsLayer { alpha = enterAlpha },
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .background(
                    Brush.radialGradient(listOf(Color.White.copy(alpha = 0.06f), Color.Transparent)),
                    shape = CircleShape
                )
                .border(1.dp, PsCardBorder, CircleShape)
        ) {
            Text(
                text = "\u2190",
                fontSize = 18.sp,
                color = PsTextPrimary,
                fontWeight = FontWeight.Light
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = puzzleName,
            style = TextStyle(
                fontFamily = DisplayFont,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PsTextPrimary,
                letterSpacing = 0.5.sp,
                shadow = Shadow(
                    color = PsGold.copy(alpha = glowAlpha),
                    offset = Offset(0f, 0f),
                    blurRadius = 20f
                )
            ),
            modifier = Modifier
                .weight(1f)
                .graphicsLayer { alpha = 0.99f }
                .drawWithContent {
                    drawContent()
                    val shimmerBrush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            PsGoldBright.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.1f),
                            PsGoldBright.copy(alpha = 0.4f),
                            Color.Transparent
                        ),
                        start = Offset(size.width * shimmerOffset, 0f),
                        end = Offset(size.width * (shimmerOffset + 0.4f), size.height)
                    )
                    drawRect(brush = shimmerBrush, blendMode = BlendMode.SrcAtop)
                }
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(PsGold.copy(alpha = 0.12f))
                .border(1.dp, PsGold.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "#%02d".format(puzzleId),
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PsGold,
                    letterSpacing = 1.sp
                )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
private fun PuzzleHeroImage(
    imageResName: String,
    puzzleName: String,
    accentColor: Color
) {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(imageResName, "drawable", context.packageName)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        if (resId != 0) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = puzzleName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(accentColor.copy(alpha = 0.15f))
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xFF080810).copy(alpha = 0.92f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 16.dp)
        ) {
            Text(
                text = puzzleName,
                style = TextStyle(
                    fontFamily = DisplayFont,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = PsTextPrimary,
                    letterSpacing = 0.5.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(accentColor, accentColor.copy(alpha = 0.2f))
                        ),
                        RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}

@Composable
private fun DifficultySection(accentColor: Color) {
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
                .background(PsGold.copy(alpha = 0.35f), RoundedCornerShape(1.dp))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "SELECT DIFFICULTY",
            style = MaterialTheme.typography.labelLarge.copy(
                color = PsTextTertiary,
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
                    Brush.horizontalGradient(listOf(PsDivider, Color.Transparent))
                )
        )
    }
}

@Composable
private fun DifficultyCard(
    difficulty: Difficulty,
    accentColor: Color,
    isCompleted: Boolean,
    isUnlocked: Boolean,
    bestResult: PuzzleResult?,
    entryDelayMs: Int = 300,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && isUnlocked) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "cardScale"
    )
    val entryAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(450, delayMillis = entryDelayMs),
        label = "entryAlpha"
    )
    val entrySlide by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(450, delayMillis = entryDelayMs),
        label = "entrySlide"
    )

    val cardAccent = when {
        isCompleted -> accentColor
        isUnlocked -> PsGold.copy(alpha = 0.5f)
        else -> Color.White.copy(alpha = 0.1f)
    }
    val cardAlpha = if (!isUnlocked) 0.5f else 1f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = cardAlpha * entryAlpha
                translationY = (1f - entryAlpha) * 24f
            }
            .shadow(
                elevation = if (isUnlocked) 6.dp else 0.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = cardAccent.copy(alpha = 0.1f),
                spotColor = cardAccent.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(PsCardBg, RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        cardAccent.copy(alpha = 0.4f),
                        PsCardBorder.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(56.dp)
                .align(Alignment.CenterStart)
                .background(
                    Brush.verticalGradient(
                        listOf(cardAccent.copy(alpha = 0.8f), cardAccent.copy(alpha = 0.2f))
                    ),
                    RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 18.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = difficulty.displayName.uppercase(),
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) PsTextPrimary else PsTextSecondary,
                                letterSpacing = 1.sp
                            )
                        )
                        if (!isUnlocked) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "\uD83D\uDD12", fontSize = 13.sp)
                        }
                        if (isCompleted) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(accentColor.copy(alpha = 0.15f))
                                    .border(0.5.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "DONE",
                                    style = TextStyle(
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accentColor,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${difficulty.cols}\u00D7${difficulty.rows} \u00B7 ${difficulty.totalPieces} pieces",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            color = PsTextSecondary
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = PuzzleEngine.formatTime(difficulty.targetTimeMs),
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isUnlocked) PsGold else PsTextTertiary
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${difficulty.basePoints} pts",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            color = PsTextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when {
                isCompleted && bestResult != null -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Best: ${bestResult.score} pts \u00B7 ${PuzzleEngine.formatTime(bestResult.completionTimeMs)}",
                                style = TextStyle(
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = accentColor
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            StarsRow(stars = bestResult.stars, accentColor = accentColor)
                        }
                        PlayButton(
                            label = "PLAY AGAIN",
                            accentColor = accentColor,
                            isUnlocked = true
                        )
                    }
                }
                !isUnlocked -> {
                    val prevName = when (difficulty) {
                        Difficulty.MEDIUM -> Difficulty.EASY.displayName
                        Difficulty.HARD -> Difficulty.MEDIUM.displayName
                        else -> ""
                    }
                    Text(
                        text = "Complete $prevName first",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            color = PsTextTertiary
                        )
                    )
                }
                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        PlayButton(
                            label = "PLAY",
                            accentColor = PsGold,
                            isUnlocked = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StarsRow(stars: Int, accentColor: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        repeat(3) { i ->
            Text(
                text = "\u2605",
                fontSize = 14.sp,
                color = if (i < stars) accentColor else Color.White.copy(alpha = 0.15f)
            )
        }
    }
}

@Composable
private fun PlayButton(
    label: String,
    accentColor: Color,
    isUnlocked: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(accentColor.copy(alpha = 0.18f), accentColor.copy(alpha = 0.10f))
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(accentColor.copy(alpha = 0.5f), accentColor.copy(alpha = 0.2f))
                ),
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 16.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                letterSpacing = 1.sp
            )
        )
    }
}

@Composable
private fun TimedModeBadge() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF8B1A1A).copy(alpha = 0.25f))
            .border(1.dp, Color(0xFFF44336).copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "\u23F1", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "TIMED MODE \u2014 Race the clock!",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF9A9A),
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}
