package app.krafted.towerjigsaw.viewmodel

import app.krafted.towerjigsaw.game.Difficulty
import app.krafted.towerjigsaw.game.PuzzlePiece

data class PuzzleUiState(
    val puzzleId: Int = 0,
    val difficulty: Difficulty = Difficulty.EASY,
    val pieces: List<PuzzlePiece> = emptyList(),
    val emptyCol: Int = 0,
    val emptyRow: Int = 0,
    val isComplete: Boolean = false,
    val isTimedMode: Boolean = false,
    val timeElapsedMs: Long = 0L,
    val targetTimeMs: Long = 0L,
    val finalScore: Int = 0,
    val stars: Int = 0,
    val isAutoSolving: Boolean = false,
    val isComputingSolution: Boolean = false
)
