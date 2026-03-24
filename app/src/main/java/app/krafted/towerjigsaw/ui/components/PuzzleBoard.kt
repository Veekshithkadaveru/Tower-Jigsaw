package app.krafted.towerjigsaw.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import app.krafted.towerjigsaw.game.Difficulty
import app.krafted.towerjigsaw.game.PuzzlePiece

@Composable
fun PuzzleBoard(
    imageBitmap: ImageBitmap,
    pieces: List<PuzzlePiece>,
    difficulty: Difficulty,
    boardWidth: Float,
    boardHeight: Float,
    modifier: Modifier = Modifier
) {
    val cols = difficulty.cols
    val rows = difficulty.rows

    val cellWidth = boardWidth / cols
    val cellHeight = boardHeight / rows

    val srcTileWidth = imageBitmap.width / cols
    val srcTileHeight = imageBitmap.height / rows

    Canvas(modifier = modifier) {
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.3f),
            topLeft = Offset.Zero,
            size = Size(boardWidth, boardHeight),
            cornerRadius = CornerRadius(12f, 12f)
        )

        val ghostStroke = Stroke(width = 1.5f)
        val ghostColor = Color.White.copy(alpha = 0.15f)
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                drawRect(
                    color = ghostColor,
                    topLeft = Offset(col * cellWidth, row * cellHeight),
                    size = Size(cellWidth, cellHeight),
                    style = ghostStroke
                )
            }
        }

        val dstCellSize = IntSize(cellWidth.toInt(), cellHeight.toInt())

        for (piece in pieces) {
            val srcOffset = IntOffset(
                piece.correctCol * srcTileWidth,
                piece.correctRow * srcTileHeight
            )
            val srcSize = IntSize(srcTileWidth, srcTileHeight)

            if (piece.isPlaced) {
                val dstOffset = IntOffset(
                    (piece.correctCol * cellWidth).toInt(),
                    (piece.correctRow * cellHeight).toInt()
                )
                drawImage(
                    image = imageBitmap,
                    srcOffset = srcOffset,
                    srcSize = srcSize,
                    dstOffset = dstOffset,
                    dstSize = dstCellSize
                )
            } else {
                val dstOffset = IntOffset(
                    piece.currentX.toInt(),
                    piece.currentY.toInt()
                )
                drawImage(
                    image = imageBitmap,
                    srcOffset = srcOffset,
                    srcSize = srcSize,
                    dstOffset = dstOffset,
                    dstSize = dstCellSize
                )
                drawRect(
                    color = Color.White.copy(alpha = 0.3f),
                    topLeft = Offset(piece.currentX, piece.currentY),
                    size = Size(cellWidth, cellHeight),
                    style = Stroke(width = 1f)
                )
            }
        }
    }
}
