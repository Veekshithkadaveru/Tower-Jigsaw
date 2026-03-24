package app.krafted.towerjigsaw.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import app.krafted.towerjigsaw.game.Difficulty
import app.krafted.towerjigsaw.game.PuzzlePiece

@Composable
fun DraggablePiece(
    piece: PuzzlePiece,
    imageBitmap: ImageBitmap,
    difficulty: Difficulty,
    boardWidth: Float,
    boardHeight: Float,
    isActive: Boolean,
    onPiecePicked: (Int) -> Unit,
    onPieceMoved: (Int, Float, Float) -> Unit,
    onPieceDropped: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (piece.isPlaced) return

    val cols = difficulty.cols
    val rows = difficulty.rows
    val cellWidth = boardWidth / cols
    val cellHeight = boardHeight / rows

    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.08f else 1.0f,
        animationSpec = spring(stiffness = 400f),
        label = "pieceScale"
    )

    val srcCellWidth = imageBitmap.width / cols
    val srcCellHeight = imageBitmap.height / rows

    val density = LocalDensity.current
    val cellWidthDp = with(density) { cellWidth.toInt().toDp() }
    val cellHeightDp = with(density) { cellHeight.toInt().toDp() }

    Canvas(
        modifier = modifier
            .offset { IntOffset(piece.currentX.toInt(), piece.currentY.toInt()) }
            .size(cellWidthDp, cellHeightDp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(piece.id) {
                detectDragGestures(
                    onDragStart = { onPiecePicked(piece.id) },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onPieceMoved(piece.id, dragAmount.x, dragAmount.y)
                    },
                    onDragEnd = { onPieceDropped(piece.id) }
                )
            }
    ) {
        drawImage(
            image = imageBitmap,
            srcOffset = IntOffset(piece.correctCol * srcCellWidth, piece.correctRow * srcCellHeight),
            srcSize = IntSize(srcCellWidth, srcCellHeight),
            dstSize = IntSize(size.width.toInt(), size.height.toInt())
        )

        drawRect(
            color = Color.White.copy(alpha = 0.4f),
            topLeft = Offset.Zero,
            size = Size(size.width, size.height),
            style = Stroke(width = 1.5f)
        )
    }
}
