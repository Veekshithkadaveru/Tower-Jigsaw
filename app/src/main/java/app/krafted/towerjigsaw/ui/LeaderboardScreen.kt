package app.krafted.towerjigsaw.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.towerjigsaw.data.db.PuzzleResult
import app.krafted.towerjigsaw.game.Difficulty
import app.krafted.towerjigsaw.game.Puzzles
import app.krafted.towerjigsaw.ui.theme.DisplayFont
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val LbBgTop = Color(0xFF080810)
private val LbBgMid = Color(0xFF0F0F22)
private val LbBgBottom = Color(0xFF151530)
private val LbGold = Color(0xFFFFD54F)
private val LbGoldBright = Color(0xFFFFF176)
private val LbTextPrimary = Color(0xFFF0F0F8)
private val LbTextSecondary = Color(0xFF9090B0)
private val LbTextTertiary = Color(0xFF606080)
private val LbCardBg = Color(0xFF13132B)
private val LbCardBorder = Color(0xFF252548)
private val LbSurface = Color(0xFF0E0E20)
private val LbSilver = Color(0xFFB0BEC5)
private val LbBronze = Color(0xFFBF8A60)
private val LbEasyActive = Color(0xFF1B5E20)
private val LbMediumActive = Color(0xFFF57F17)
private val LbHardActive = Color(0xFFC62828)

@Composable
fun LeaderboardScreen(
    selectedPuzzleId: Int,
    selectedDifficulty: Difficulty,
    scores: List<PuzzleResult>,
    onPuzzleSelected: (Int) -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(LbBgTop, LbBgMid, LbBgBottom)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            LeaderboardTopBar(onBack = onBack)

            Spacer(modifier = Modifier.height(16.dp))

            PuzzleSelector(
                selectedPuzzleId = selectedPuzzleId,
                onPuzzleSelected = onPuzzleSelected
            )

            Spacer(modifier = Modifier.height(14.dp))

            DifficultyTabs(
                selectedDifficulty = selectedDifficulty,
                onDifficultySelected = onDifficultySelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            ScoresList(
                scores = scores,
                selectedPuzzleId = selectedPuzzleId,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LeaderboardTopBar(onBack: () -> Unit) {
    val enterAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(700, delayMillis = 100),
        label = "lbEnterAlpha"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "lbHeaderAnim")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "lbShimmer"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lbGlow"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
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
                .border(1.dp, LbCardBorder, CircleShape)
        ) {
            Text(
                text = "\u2190",
                fontSize = 18.sp,
                color = LbTextPrimary,
                fontWeight = FontWeight.Light
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "LEADERBOARD",
                style = TextStyle(
                    fontFamily = DisplayFont,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = LbTextPrimary,
                    letterSpacing = 3.sp,
                    shadow = Shadow(
                        color = LbGold.copy(alpha = glowAlpha),
                        offset = Offset(0f, 0f),
                        blurRadius = 20f
                    )
                ),
                modifier = Modifier
                    .graphicsLayer { alpha = 0.99f }
                    .drawWithContent {
                        drawContent()
                        val shimmerBrush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                LbGoldBright.copy(alpha = 0.45f),
                                Color.White.copy(alpha = 0.12f),
                                LbGoldBright.copy(alpha = 0.45f),
                                Color.Transparent
                            ),
                            start = Offset(size.width * shimmerOffset, 0f),
                            end = Offset(size.width * (shimmerOffset + 0.4f), size.height)
                        )
                        drawRect(brush = shimmerBrush, blendMode = BlendMode.SrcAtop)
                    }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "\uD83C\uDFC6", fontSize = 18.sp)
        }
    }
}

@Composable
private fun PuzzleSelector(
    selectedPuzzleId: Int,
    onPuzzleSelected: (Int) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(Puzzles.all) { puzzle ->
            val isSelected = puzzle.id == selectedPuzzleId
            val chipBg by animateColorAsState(
                targetValue = if (isSelected) puzzle.accentColor.copy(alpha = 0.25f) else LbCardBg,
                animationSpec = tween(200),
                label = "chipBg_${puzzle.id}"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) puzzle.accentColor.copy(alpha = 0.7f) else LbCardBorder,
                animationSpec = tween(200),
                label = "chipBorder_${puzzle.id}"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) puzzle.accentColor else LbTextTertiary,
                animationSpec = tween(200),
                label = "chipText_${puzzle.id}"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(chipBg)
                    .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                    .clickable { onPuzzleSelected(puzzle.id) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = puzzle.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = textColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                        letterSpacing = 0.3.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun DifficultyTabs(
    selectedDifficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit
) {
    val difficulties = listOf(Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(LbSurface)
            .border(1.dp, LbCardBorder, RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            difficulties.forEachIndexed { index, difficulty ->
                val isActive = difficulty == selectedDifficulty
                val activeColor = when (difficulty) {
                    Difficulty.EASY -> LbEasyActive
                    Difficulty.MEDIUM -> LbMediumActive
                    Difficulty.HARD -> LbHardActive
                }
                val tabBg by animateColorAsState(
                    targetValue = if (isActive) activeColor else Color.Transparent,
                    animationSpec = tween(250),
                    label = "tabBg_${difficulty.name}"
                )
                val tabText by animateColorAsState(
                    targetValue = if (isActive) Color.White else LbTextTertiary,
                    animationSpec = tween(250),
                    label = "tabText_${difficulty.name}"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(9.dp))
                        .background(tabBg)
                        .clickable { onDifficultySelected(difficulty) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = difficulty.displayName.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = tabText,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }

                if (index < difficulties.size - 1) {
                    Spacer(modifier = Modifier.width(3.dp))
                }
            }
        }
    }
}

@Composable
private fun ScoresList(
    scores: List<PuzzleResult>,
    selectedPuzzleId: Int,
    modifier: Modifier = Modifier
) {
    val puzzle = Puzzles.getById(selectedPuzzleId)

    Column(modifier = modifier) {
        if (scores.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No timed scores yet.\nComplete a puzzle in Timed mode!",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LbTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(scores) { index, result ->
                    ScoreRow(
                        rank = index + 1,
                        result = result,
                        accentColor = puzzle.accentColor
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Timed mode only \u00B7 Best scores",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = LbTextTertiary.copy(alpha = 0.6f),
                    letterSpacing = 0.5.sp,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
private fun ScoreRow(
    rank: Int,
    result: PuzzleResult,
    accentColor: Color
) {
    val rankColor = when (rank) {
        1 -> LbGold
        2 -> LbSilver
        3 -> LbBronze
        else -> LbTextTertiary
    }

    val isTopThree = rank <= 3

    val totalSeconds = (result.completionTimeMs / 1000).toInt()
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    val timeFormatted = "%d:%02d".format(m, s)

    val dateFormatted = SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(result.completedAt))

    if (isTopThree) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(LbCardBg)
                .border(1.dp, LbCardBorder, RoundedCornerShape(10.dp))
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(56.dp)
                    .align(Alignment.CenterStart)
                    .background(
                        Brush.verticalGradient(
                            listOf(rankColor.copy(alpha = 0.9f), rankColor.copy(alpha = 0.2f))
                        ),
                        RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                    )
            )
            ScoreRowContent(
                rank = rank,
                rankColor = rankColor,
                result = result,
                accentColor = accentColor,
                timeFormatted = timeFormatted,
                dateFormatted = dateFormatted,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    } else {
        ScoreRowContent(
            rank = rank,
            rankColor = rankColor,
            result = result,
            accentColor = accentColor,
            timeFormatted = timeFormatted,
            dateFormatted = dateFormatted,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun ScoreRowContent(
    rank: Int,
    rankColor: Color,
    result: PuzzleResult,
    accentColor: Color,
    timeFormatted: String,
    dateFormatted: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#$rank",
            style = MaterialTheme.typography.labelLarge.copy(
                color = rankColor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.width(32.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = result.playerName,
                style = TextStyle(
                    fontFamily = DisplayFont,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    letterSpacing = 0.3.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = timeFormatted,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = LbTextSecondary,
                    fontSize = 11.sp
                )
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${result.score} pts",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    letterSpacing = 0.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row {
                repeat(3) { i ->
                    Text(
                        text = if (i < result.stars) "\u2605" else "\u2606",
                        fontSize = 11.sp,
                        color = if (i < result.stars) LbGold else LbTextTertiary
                    )
                }
            }
        }

        Text(
            text = dateFormatted,
            style = MaterialTheme.typography.labelSmall.copy(
                color = LbTextTertiary,
                fontSize = 11.sp
            )
        )
    }
}
