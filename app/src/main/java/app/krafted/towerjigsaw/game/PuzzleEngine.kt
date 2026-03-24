package app.krafted.towerjigsaw.game

import kotlin.math.hypot
import kotlin.random.Random

object PuzzleEngine {

    const val SNAP_THRESHOLD_PX = 60f

    fun generatePieces(
        difficulty: Difficulty,
        trayStartX: Float,
        trayEndX: Float,
        trayStartY: Float,
        trayEndY: Float
    ): List<PuzzlePiece> {
        val pieces = mutableListOf<PuzzlePiece>()
        for (row in 0 until difficulty.rows) {
            for (col in 0 until difficulty.cols) {
                pieces.add(
                    PuzzlePiece(
                        id = row * difficulty.cols + col,
                        correctCol = col,
                        correctRow = row,
                        currentX = Random.nextFloat() * (trayEndX - trayStartX) + trayStartX,
                        currentY = Random.nextFloat() * (trayEndY - trayStartY) + trayStartY,
                        isPlaced = false
                    )
                )
            }
        }
        return pieces.shuffled()
    }

    fun getCorrectSlotPosition(
        piece: PuzzlePiece,
        pieceWidth: Float,
        pieceHeight: Float,
        boardOffsetX: Float,
        boardOffsetY: Float
    ): Pair<Float, Float> {
        val x = boardOffsetX + piece.correctCol * pieceWidth
        val y = boardOffsetY + piece.correctRow * pieceHeight
        return Pair(x, y)
    }

    fun checkSnap(
        piece: PuzzlePiece,
        pieceWidth: Float,
        pieceHeight: Float,
        boardOffsetX: Float,
        boardOffsetY: Float,
        threshold: Float = SNAP_THRESHOLD_PX
    ): Pair<Float, Float>? {
        val (correctX, correctY) = getCorrectSlotPosition(
            piece, pieceWidth, pieceHeight, boardOffsetX, boardOffsetY
        )
        val distance = hypot(
            piece.currentX - correctX,
            piece.currentY - correctY
        )
        return if (distance < threshold) Pair(correctX, correctY) else null
    }

    fun isPuzzleComplete(pieces: List<PuzzlePiece>): Boolean {
        return pieces.all { it.isPlaced }
    }

    fun calculateScore(
        difficulty: Difficulty,
        elapsedTimeMs: Long
    ): Int {
        val remainingMs = (difficulty.targetTimeMs - elapsedTimeMs).coerceAtLeast(0)
        val remainingSeconds = (remainingMs / 1000).toInt()
        return difficulty.basePoints + (remainingSeconds * 10)
    }

    fun calculateStars(
        isTimedMode: Boolean,
        difficulty: Difficulty,
        elapsedTimeMs: Long
    ): Int {
        if (!isTimedMode) return 3
        return when {
            elapsedTimeMs <= difficulty.targetTimeMs / 2 -> 3
            elapsedTimeMs <= difficulty.targetTimeMs -> 2
            else -> 1
        }
    }

    fun formatTime(timeMs: Long): String {
        val totalSeconds = (timeMs / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }
}
