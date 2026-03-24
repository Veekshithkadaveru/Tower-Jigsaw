package app.krafted.towerjigsaw.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import app.krafted.towerjigsaw.game.Difficulty
import app.krafted.towerjigsaw.game.PuzzlePiece

@Composable
fun PiecesTray(
    pieces: List<PuzzlePiece>,
    imageBitmap: ImageBitmap,
    difficulty: Difficulty,
    activePieceId: Int?,
    onPiecePicked: (Int) -> Unit,
    onPieceMoved: (Int, Float, Float) -> Unit,
    onPieceDropped: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val unplacedPieces = remember(pieces) {
        pieces.filter { !it.isPlaced }
    }

    val srcTileWidth = imageBitmap.width / difficulty.cols
    val srcTileHeight = imageBitmap.height / difficulty.rows

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.2f))
            .padding(12.dp)
    ) {
        Text(
            text = "Pieces remaining: ${unplacedPieces.size}",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            items(unplacedPieces, key = { it.id }) { piece ->
                val isActive = piece.id == activePieceId
                val borderColor = if (isActive) {
                    Color(0xFFFFD700)
                } else {
                    Color.White.copy(alpha = 0.5f)
                }

                Canvas(
                    modifier = Modifier
                        .size(64.dp)
                        .clickable { onPiecePicked(piece.id) }
                ) {
                    val srcOffset = IntOffset(
                        piece.correctCol * srcTileWidth,
                        piece.correctRow * srcTileHeight
                    )
                    val srcSize = IntSize(srcTileWidth, srcTileHeight)
                    val dstSize = IntSize(size.width.toInt(), size.height.toInt())

                    drawImage(
                        image = imageBitmap,
                        srcOffset = srcOffset,
                        srcSize = srcSize,
                        dstOffset = IntOffset.Zero,
                        dstSize = dstSize
                    )

                    drawRect(
                        color = borderColor,
                        topLeft = Offset.Zero,
                        size = Size(size.width, size.height),
                        style = Stroke(width = if (isActive) 2.5f else 1.5f)
                    )
                }
            }
        }
    }
}
