package app.krafted.towerjigsaw.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.krafted.towerjigsaw.game.Difficulty
import app.krafted.towerjigsaw.game.PuzzleEngine
import app.krafted.towerjigsaw.game.Puzzles
import app.krafted.towerjigsaw.ui.components.PuzzleBoard
import app.krafted.towerjigsaw.ui.components.TimerBar
import app.krafted.towerjigsaw.ui.theme.DisplayFont
import app.krafted.towerjigsaw.viewmodel.PuzzleViewModel
import kotlinx.coroutines.delay

private val PsTextPrimary   = Color(0xFFF0F0F8)
private val PsTextSecondary = Color(0xFF9090B0)

@Composable
fun PuzzleScreen(
    puzzleId: Int,
    difficultyName: String,
    isTimedMode: Boolean,
    onPuzzleComplete: (Int, String, Int, Int, Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val difficulty = Difficulty.valueOf(difficultyName)
    val puzzle     = Puzzles.getById(puzzleId)
    val context    = LocalContext.current
    val density    = LocalDensity.current

    val viewModel: PuzzleViewModel = viewModel()
    val state by viewModel.state.collectAsState()

    var isStarted by remember { mutableStateOf(false) }

    LaunchedEffect(puzzleId, difficulty, isTimedMode) {
        if (!isStarted) {
            viewModel.startPuzzle(puzzleId, difficulty, isTimedMode)
            isStarted = true
        }
    }

    LaunchedEffect(state.isComplete) {
        if (state.isComplete) {
            delay(1000)
            onPuzzleComplete(puzzleId, difficultyName, state.finalScore, state.stars, state.timeElapsedMs)
        }
    }

    val resId = remember(puzzle.imageResName) {
        context.resources.getIdentifier(puzzle.imageResName, "drawable", context.packageName)
    }
    val imageBitmap = if (resId != 0) ImageBitmap.imageResource(id = resId) else null

    val targetRow = remember(state.pieces) {
        PuzzleEngine.getTargetRow(state.pieces, difficulty.rows)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(
                Color(0xFF080810), Color(0xFF0F0F22), Color(0xFF151530)
            )))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0E0E20).copy(alpha = 0.8f))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
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
                        .border(1.dp, Color(0xFF252548), CircleShape)
                ) {
                    Text(text = "\u2190", fontSize = 18.sp, color = PsTextPrimary, fontWeight = FontWeight.Light)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = puzzle.name.uppercase(),
                        style = TextStyle(
                            fontFamily = DisplayFont,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = PsTextPrimary
                        )
                    )
                    Text(
                        text = difficulty.displayName.uppercase(),
                        style = TextStyle(
                            fontFamily = DisplayFont,
                            fontSize = 12.sp,
                            color = Color(0xFFFFD54F),
                            letterSpacing = 1.5.sp
                        )
                    )
                    val completed = state.pieces.count {
                        it.currentCol == it.correctCol && it.currentRow == it.correctRow
                    }
                    Text(
                        text = "$completed / ${state.pieces.size} pieces placed",
                        color = PsTextSecondary,
                        fontSize = 11.sp
                    )
                }

                if (isTimedMode) {
                    val remainingMs = (state.targetTimeMs - state.timeElapsedMs).coerceAtLeast(0)
                    Text(
                        text = "⏱ ${PuzzleEngine.formatTime(remainingMs)}",
                        color = PsTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }

                when {
                    state.isComputingSolution -> {
                        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color(0xFF80DEEA),
                                strokeWidth = 2.5.dp
                            )
                        }
                    }
                    state.isAutoSolving -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFF5252).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFFFF5252).copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .clickable { viewModel.onStopFixRow() }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Stop", color = Color(0xFFFF5252), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF80DEEA).copy(alpha = 0.18f), Color(0xFF80DEEA).copy(alpha = 0.10f))
                                    )
                                )
                                .border(
                                    1.dp,
                                    Brush.linearGradient(listOf(Color(0xFF80DEEA).copy(alpha = 0.5f), Color(0xFF80DEEA).copy(alpha = 0.2f))),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable(enabled = !state.isComplete && targetRow != null) { viewModel.onFixRow() }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (targetRow != null) "Fix Row ${targetRow + 1}" else "Done ✓",
                                color = if (!state.isComplete && targetRow != null) Color(0xFF80DEEA) else PsTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (isTimedMode) {
                TimerBar(
                    timeElapsedMs = state.timeElapsedMs,
                    targetTimeMs  = state.targetTimeMs,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )
            }

            if (imageBitmap != null) {
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val availW = constraints.maxWidth.toFloat()
                    val availH = constraints.maxHeight.toFloat()

                    if (availW > 0f && availH > 0f) {
                        val imgAspect = imageBitmap.width.toFloat() / imageBitmap.height.toFloat()
                        val boxAspect = availW / availH

                        val bw: Float
                        val bh: Float
                        if (imgAspect > boxAspect) {
                            bw = availW
                            bh = availW / imgAspect
                        } else {
                            bh = availH
                            bw = availH * imgAspect
                        }

                        Box(contentAlignment = Alignment.Center) {
                            PuzzleBoard(
                                imageBitmap  = imageBitmap,
                                pieces       = state.pieces,
                                difficulty   = difficulty,
                                boardWidth   = bw,
                                boardHeight  = bh,
                                onPieceTapped = if (state.isAutoSolving) { _ -> }
                                               else viewModel::onPieceTapped,
                                modifier = Modifier.size(
                                    width  = with(density) { bw.toDp() },
                                    height = with(density) { bh.toDp() }
                                )
                            )

                            if (state.isComputingSolution) {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            Color(0xFF13132B).copy(alpha = 0.9f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            1.dp,
                                            Color(0xFF252548),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 24.dp, vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(
                                            modifier    = Modifier.size(36.dp),
                                            color       = Color(0xFF80DEEA),
                                            strokeWidth = 3.dp
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text  = if (targetRow != null)
                                                "Fixing row ${targetRow + 1}…" else "Solving…",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
