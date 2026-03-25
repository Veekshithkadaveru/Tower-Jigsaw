package app.krafted.towerjigsaw.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
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

private val PsTextPrimary = Color(0xFFF0F0F8)
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
    val puzzle = Puzzles.getById(puzzleId)
    val context = LocalContext.current

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

    var boardWidth by remember { mutableFloatStateOf(0f) }
    var boardHeight by remember { mutableFloatStateOf(0f) }

    val bgResId = remember {
        context.resources.getIdentifier("back_4", "drawable", context.packageName)
    }

    val targetRow = remember(state.pieces) {
        PuzzleEngine.getTargetRow(state.pieces, difficulty.rows)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (bgResId != 0) {
            Image(
                painter = painterResource(id = bgResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = 0.4f
            )
        }

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Text(text = "\u2190", fontSize = 24.sp, color = PsTextPrimary)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = puzzle.name.uppercase(),
                            style = TextStyle(
                                fontFamily = DisplayFont,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                color = PsTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "· ${difficulty.displayName.uppercase()}",
                            style = TextStyle(
                                fontFamily = DisplayFont,
                                fontSize = 14.sp,
                                color = Color(0xFFFFD54F),
                                letterSpacing = 1.sp
                            )
                        )
                    }
                    val completed = state.pieces.count { it.currentCol == it.correctCol && it.currentRow == it.correctRow }
                    Text(
                        text = "Progress: $completed / ${state.pieces.size + 1} valid",
                        color = PsTextSecondary,
                        fontSize = 12.sp
                    )
                }

                if (isTimedMode) {
                    val remainingMs = (state.targetTimeMs - state.timeElapsedMs).coerceAtLeast(0)
                    Text(
                        text = "\u23F1 ${PuzzleEngine.formatTime(remainingMs)}",
                        color = PsTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                when {
                    state.isComputingSolution -> {
                        Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color(0xFF80DEEA),
                                strokeWidth = 2.5.dp
                            )
                        }
                    }
                    state.isAutoSolving -> {
                        TextButton(onClick = { viewModel.onStopFixRow() }) {
                            Text(
                                text = "Stop",
                                color = Color(0xFFFF5252),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    else -> {
                        TextButton(
                            onClick = { viewModel.onFixRow() },
                            enabled = !state.isComplete && targetRow != null
                        ) {
                            Text(
                                text = if (targetRow != null) "Fix\nRow ${targetRow + 1}" else "Done",
                                color = if (!state.isComplete && targetRow != null) Color(0xFF80DEEA) else PsTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            }

            if (isTimedMode) {
                TimerBar(
                    timeElapsedMs = state.timeElapsedMs,
                    targetTimeMs = state.targetTimeMs,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (imageBitmap != null) {
                val density = LocalDensity.current

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .onGloballyPositioned { coordinates ->
                            val width = coordinates.size.width.toFloat()
                            val height = coordinates.size.height.toFloat()
                            if (width > 0 && height > 0) {
                                val imageAspect = imageBitmap.width.toFloat() / imageBitmap.height.toFloat()
                                val boxAspect = width / height
                                val bw: Float
                                val bh: Float
                                if (imageAspect > boxAspect) {
                                    bw = width
                                    bh = width / imageAspect
                                } else {
                                    bh = height
                                    bw = height * imageAspect
                                }
                                if (boardWidth != bw || boardHeight != bh) {
                                    boardWidth = bw
                                    boardHeight = bh
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (boardWidth > 0f && boardHeight > 0f) {
                        Box(contentAlignment = Alignment.Center) {
                            PuzzleBoard(
                                imageBitmap = imageBitmap,
                                pieces = state.pieces,
                                difficulty = difficulty,
                                boardWidth = boardWidth,
                                boardHeight = boardHeight,
                                onPieceTapped = if (state.isAutoSolving) { _ -> } else viewModel::onPieceTapped,
                                modifier = Modifier.size(
                                    width = with(density) { boardWidth.toDp() },
                                    height = with(density) { boardHeight.toDp() }
                                )
                            )

                            if (state.isComputingSolution) {
                                Box(
                                    modifier = Modifier
                                        .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 24.dp, vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(36.dp),
                                            color = Color(0xFF80DEEA),
                                            strokeWidth = 3.dp
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = if (targetRow != null) "Fixing row ${targetRow + 1}…" else "Solving…",
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
