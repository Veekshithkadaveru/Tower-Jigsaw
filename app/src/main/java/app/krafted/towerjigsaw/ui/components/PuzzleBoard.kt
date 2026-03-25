package app.krafted.towerjigsaw.ui.components

import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.towerjigsaw.game.Difficulty
import app.krafted.towerjigsaw.game.PuzzlePiece

@Composable
fun PuzzleBoard(
    imageBitmap: ImageBitmap,
    pieces: List<PuzzlePiece>,
    difficulty: Difficulty,
    boardWidth: Float,
    boardHeight: Float,
    onPieceTapped: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val cols = difficulty.cols
    val rows = difficulty.rows
    val cellWidth = boardWidth / cols
    val cellHeight = boardHeight / rows
    val srcTileWidth = imageBitmap.width / cols
    val srcTileHeight = imageBitmap.height / rows

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                color = Color(0xFF0F0F22).copy(alpha = 0.85f),
                topLeft = Offset.Zero,
                size = Size(boardWidth, boardHeight),
                cornerRadius = CornerRadius(16f, 16f)
            )
            drawRoundRect(
                color = Color.White.copy(alpha = 0.06f),
                topLeft = Offset.Zero,
                size = Size(boardWidth, boardHeight),
                cornerRadius = CornerRadius(16f, 16f),
                style = Stroke(width = 2f)
            )
            drawImage(
                image = imageBitmap,
                alpha = 0.15f,
                dstSize = IntSize(boardWidth.toInt(), boardHeight.toInt())
            )
            val slotColor = Color.White.copy(alpha = 0.02f)
            val ghostColor = Color.White.copy(alpha = 0.04f)
            val ghostStroke = Stroke(width = 1.5f)
            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    val cellOffset = Offset(col * cellWidth, row * cellHeight)
                    val cellSize = Size(cellWidth, cellHeight)
                    drawRect(color = slotColor, topLeft = cellOffset, size = cellSize)
                    drawRect(color = ghostColor, topLeft = cellOffset, size = cellSize, style = ghostStroke)
                }
            }
        }

        val density = LocalDensity.current
        val cellWidthDp = with(density) { cellWidth.toDp() }
        val cellHeightDp = with(density) { cellHeight.toDp() }

        pieces.forEach { piece ->
            key(piece.id) {
                val animatedOffset by animateIntOffsetAsState(
                    targetValue = IntOffset(
                        (piece.currentCol * cellWidth).toInt(),
                        (piece.currentRow * cellHeight).toInt()
                    ),
                    animationSpec = spring(stiffness = 500f, dampingRatio = 0.7f),
                    label = "slide_${piece.id}"
                )

                Box(
                    modifier = Modifier
                        .offset { animatedOffset }
                        .size(cellWidthDp, cellHeightDp)
                        .clickable { onPieceTapped(piece.id) }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawImage(
                            image = imageBitmap,
                            srcOffset = IntOffset(piece.correctCol * srcTileWidth, piece.correctRow * srcTileHeight),
                            srcSize = IntSize(srcTileWidth, srcTileHeight),
                            dstOffset = IntOffset.Zero,
                            dstSize = IntSize(size.width.toInt(), size.height.toInt())
                        )
                        drawRect(
                            color = Color.White.copy(alpha = 0.5f),
                            topLeft = Offset.Zero,
                            size = Size(size.width, size.height),
                            style = Stroke(width = 1f)
                        )
                        drawRect(
                            color = Color.Black.copy(alpha = 0.4f),
                            topLeft = Offset(1f, 1f),
                            size = Size(size.width, size.height),
                            style = Stroke(width = 1.5f)
                        )
                    }

                    Text(
                        text = "${piece.correctRow * cols + piece.correctCol + 1}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
