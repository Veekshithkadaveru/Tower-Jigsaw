package app.krafted.towerjigsaw.game

data class PuzzlePiece(
    val id: Int,
    val correctCol: Int,
    val correctRow: Int,
    var currentCol: Int,
    var currentRow: Int
)
