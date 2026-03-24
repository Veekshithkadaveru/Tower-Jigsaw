package app.krafted.towerjigsaw.game

data class PuzzlePiece(
    val id: Int,
    val correctCol: Int,
    val correctRow: Int,
    val currentX: Float,
    val currentY: Float,
    val isPlaced: Boolean = false
)
