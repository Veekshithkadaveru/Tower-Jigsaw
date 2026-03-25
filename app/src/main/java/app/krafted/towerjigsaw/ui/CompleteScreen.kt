package app.krafted.towerjigsaw.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import app.krafted.towerjigsaw.viewmodel.CompleteViewModel
import androidx.compose.ui.graphics.SolidColor
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.towerjigsaw.game.Difficulty
import app.krafted.towerjigsaw.game.PuzzleEngine
import app.krafted.towerjigsaw.game.Puzzles
import app.krafted.towerjigsaw.ui.components.ConfettiCanvas
import app.krafted.towerjigsaw.ui.theme.DisplayFont
import kotlinx.coroutines.delay

private val CsBgTop    = Color(0xFF080810)
private val CsBgMid    = Color(0xFF0F0F22)
private val CsBgBottom = Color(0xFF151530)
private val CsGold     = Color(0xFFFFD54F)
private val CsGoldBright = Color(0xFFFFF176)
private val CsTextPrimary   = Color(0xFFF0F0F8)
private val CsTextSecondary = Color(0xFF9090B0)

@Composable
fun CompleteScreen(
    puzzleId: Int,
    difficultyName: String,
    isTimedMode: Boolean,
    score: Int,
    stars: Int,
    timeMs: Long,
    onNextPuzzle: (() -> Unit)?,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val puzzle = Puzzles.getById(puzzleId)
    val difficulty = Difficulty.valueOf(difficultyName)
    val context = LocalContext.current
    val completeViewModel: CompleteViewModel = viewModel()
    var playerName by remember { mutableStateOf("Player") }

    val resId = remember(puzzle.imageResName) {
        context.resources.getIdentifier(puzzle.imageResName, "drawable", context.packageName)
    }

    // --- animation state triggers ---
    var revealVisible  by remember { mutableStateOf(false) }
    var statsVisible   by remember { mutableStateOf(false) }
    var star1Visible   by remember { mutableStateOf(false) }
    var star2Visible   by remember { mutableStateOf(false) }
    var star3Visible   by remember { mutableStateOf(false) }
    var buttonsVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        revealVisible = true
        delay(350)
        statsVisible = true
        delay(200)
        if (stars >= 1) { star1Visible = true; delay(220) }
        if (stars >= 2) { star2Visible = true; delay(220) }
        if (stars >= 3) { star3Visible = true; delay(220) }
        buttonsVisible = true
    }

    // --- animations ---
    val revealScale by animateFloatAsState(
        targetValue = if (revealVisible) 1f else 0.82f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 180f),
        label = "revealScale"
    )
    val revealAlpha by animateFloatAsState(
        targetValue = if (revealVisible) 1f else 0f,
        animationSpec = tween(550),
        label = "revealAlpha"
    )
    val statsAlpha by animateFloatAsState(
        targetValue = if (statsVisible) 1f else 0f,
        animationSpec = tween(400),
        label = "statsAlpha"
    )
    val star1Scale by animateFloatAsState(
        targetValue = if (star1Visible) 1f else 0.1f,
        animationSpec = spring(dampingRatio = 0.3f, stiffness = 500f),
        label = "star1Scale"
    )
    val star1Alpha by animateFloatAsState(
        targetValue = if (star1Visible) 1f else 0f,
        animationSpec = tween(180),
        label = "star1Alpha"
    )
    val star2Scale by animateFloatAsState(
        targetValue = if (star2Visible) 1f else 0.1f,
        animationSpec = spring(dampingRatio = 0.3f, stiffness = 500f),
        label = "star2Scale"
    )
    val star2Alpha by animateFloatAsState(
        targetValue = if (star2Visible) 1f else 0f,
        animationSpec = tween(180),
        label = "star2Alpha"
    )
    val star3Scale by animateFloatAsState(
        targetValue = if (star3Visible) 1f else 0.1f,
        animationSpec = spring(dampingRatio = 0.3f, stiffness = 500f),
        label = "star3Scale"
    )
    val star3Alpha by animateFloatAsState(
        targetValue = if (star3Visible) 1f else 0f,
        animationSpec = tween(180),
        label = "star3Alpha"
    )
    val buttonsAlpha by animateFloatAsState(
        targetValue = if (buttonsVisible) 1f else 0f,
        animationSpec = tween(350),
        label = "buttonsAlpha"
    )

    // --- shimmer + glow for title ---
    val infiniteTransition = rememberInfiniteTransition(label = "titleAnim")
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CsBgTop, CsBgMid, CsBgBottom)))
    ) {
        // Confetti particle burst
        ConfettiCanvas(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // ── Title ──────────────────────────────────────────────
            Text(
                text = "PUZZLE COMPLETE",
                style = TextStyle(
                    fontFamily = DisplayFont,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = CsGold,
                    letterSpacing = 3.sp,
                    shadow = Shadow(
                        color = CsGold.copy(alpha = glowAlpha),
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
                                CsGoldBright.copy(alpha = 0.5f),
                                CsGoldBright.copy(alpha = 0.5f),
                                Color.Transparent
                            ),
                            start = Offset(size.width * shimmerOffset, 0f),
                            end = Offset(
                                size.width * (shimmerOffset + 0.35f),
                                size.height * 0.8f
                            )
                        )
                        drawRect(brush = shimmerBrush, blendMode = BlendMode.SrcAtop)
                    }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${puzzle.name.uppercase()} · ${difficulty.displayName.uppercase()}",
                style = TextStyle(
                    fontFamily = DisplayFont,
                    fontSize = 12.sp,
                    color = CsTextSecondary,
                    letterSpacing = 2.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Assembled image reveal ─────────────────────────────
            if (resId != 0) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = revealScale
                            scaleY = revealScale
                            alpha  = revealAlpha
                        }
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(
                                    puzzle.accentColor.copy(alpha = 0.6f),
                                    puzzle.accentColor.copy(alpha = 0.12f)
                                )
                            ),
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = puzzle.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Stars ─────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.graphicsLayer { alpha = statsAlpha }
            ) {
                listOf(
                    Triple(stars >= 1, star1Scale, star1Alpha),
                    Triple(stars >= 2, star2Scale, star2Alpha),
                    Triple(stars >= 3, star3Scale, star3Alpha)
                ).forEachIndexed { i, (earned, sc, al) ->
                    if (i > 0) Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (earned) "⭐" else "☆",
                        fontSize = 38.sp,
                        modifier = Modifier.graphicsLayer {
                            scaleX = sc
                            scaleY = sc
                            alpha  = al
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Stats row ─────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = statsAlpha }
                    .background(
                        Color(0xFF13132B),
                        RoundedCornerShape(16.dp)
                    )
                    .border(
                        1.dp,
                        Color(0xFF252548).copy(alpha = 0.8f),
                        RoundedCornerShape(16.dp)
                    )
                    .padding(vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(label = "TIME", value = PuzzleEngine.formatTime(timeMs))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(Color.White.copy(alpha = 0.1f))
                )
                StatItem(label = "SCORE", value = "$score pts")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Name entry ────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = buttonsAlpha }
            ) {
                Text(
                    text = "YOUR NAME",
                    style = TextStyle(
                        fontFamily = DisplayFont,
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = CsTextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0E0E20))
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(CsGold.copy(alpha = 0.5f), CsGold.copy(alpha = 0.15f))
                            ),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    BasicTextField(
                        value = playerName,
                        onValueChange = { if (it.length <= 20) playerName = it },
                        textStyle = TextStyle(
                            fontFamily = DisplayFont,
                            fontSize = 15.sp,
                            color = CsTextPrimary
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(CsGold),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Buttons ───────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = buttonsAlpha },
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (onNextPuzzle != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(puzzle.accentColor.copy(alpha = 0.8f), puzzle.accentColor.copy(alpha = 0.6f))
                                )
                            )
                            .border(
                                1.dp,
                                Brush.linearGradient(listOf(puzzle.accentColor, puzzle.accentColor.copy(alpha = 0.5f))),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                completeViewModel.saveResult(
                                    puzzleId, difficultyName, isTimedMode, timeMs, score, stars, playerName
                                )
                                onNextPuzzle()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NEXT PUZZLE",
                            style = TextStyle(
                                fontFamily = DisplayFont,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                color = Color.White
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(
                            1.dp,
                            Brush.linearGradient(listOf(Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.08f))),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            completeViewModel.saveResult(
                                puzzleId, difficultyName, isTimedMode, timeMs, score, stars, playerName
                            )
                            onHome()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "HOME",
                        style = TextStyle(
                            fontFamily = DisplayFont,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = CsTextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = TextStyle(
                color = CsTextSecondary,
                fontSize = 10.sp,
                letterSpacing = 1.5.sp,
                fontFamily = DisplayFont
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = TextStyle(
                color = CsTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = DisplayFont
            )
        )
    }
}
