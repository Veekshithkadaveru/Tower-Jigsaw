package app.krafted.towerjigsaw.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.towerjigsaw.data.db.AppDatabase
import app.krafted.towerjigsaw.data.db.PuzzleDao
import app.krafted.towerjigsaw.data.db.PuzzleResult
import app.krafted.towerjigsaw.game.Difficulty
import app.krafted.towerjigsaw.game.PuzzleEngine
import app.krafted.towerjigsaw.game.PuzzlePiece
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class PuzzleViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: PuzzleDao = AppDatabase.getInstance(application).puzzleDao()

    private val _state = MutableStateFlow(PuzzleUiState())
    val state: StateFlow<PuzzleUiState> = _state.asStateFlow()

    private var startTime: Long = 0L
    private var timerJob: Job? = null
    private var autoSolveJob: Job? = null

    fun startPuzzle(puzzleId: Int, difficulty: Difficulty, isTimedMode: Boolean) {
        timerJob?.cancel()

        val generationResult = PuzzleEngine.generatePieces(difficulty)

        _state.value = PuzzleUiState(
            puzzleId = puzzleId,
            difficulty = difficulty,
            pieces = generationResult.pieces,
            emptyCol = generationResult.emptyCol,
            emptyRow = generationResult.emptyRow,
            isTimedMode = isTimedMode,
            targetTimeMs = difficulty.targetTimeMs
        )

        startTime = System.currentTimeMillis()

        if (isTimedMode) {
            startTimer()
        }
    }

    fun onPieceTapped(pieceId: Int) {
        val current = _state.value
        val piece = current.pieces.find { it.id == pieceId } ?: return
        
        val isAdjacent = (abs(piece.currentCol - current.emptyCol) == 1 && piece.currentRow == current.emptyRow) ||
                         (abs(piece.currentRow - current.emptyRow) == 1 && piece.currentCol == current.emptyCol)

        if (isAdjacent) {
            val tempCol = piece.currentCol
            val tempRow = piece.currentRow

            val newPieces = current.pieces.map {
                if (it.id == pieceId) it.copy(currentCol = current.emptyCol, currentRow = current.emptyRow)
                else it
            }

            val isComplete = PuzzleEngine.isPuzzleComplete(newPieces)

            if (isComplete) {
                timerJob?.cancel()
                val elapsed = System.currentTimeMillis() - startTime
                val score = PuzzleEngine.calculateScore(current.difficulty, elapsed)
                val stars = PuzzleEngine.calculateStars(current.isTimedMode, current.difficulty, elapsed)
                _state.value = current.copy(
                    pieces = newPieces,
                    emptyCol = -1,
                    emptyRow = -1,
                    isComplete = true,
                    timeElapsedMs = elapsed,
                    finalScore = score,
                    stars = stars
                )
                saveResult(
                    puzzleId = current.puzzleId,
                    difficulty = current.difficulty,
                    isTimedMode = current.isTimedMode,
                    completionTimeMs = elapsed,
                    score = score,
                    stars = stars
                )
            } else {
                _state.value = current.copy(
                    pieces = newPieces,
                    emptyCol = tempCol,
                    emptyRow = tempRow
                )
            }
        }
    }

    fun onFixRow() {
        val current = _state.value
        if (current.isComplete || current.isAutoSolving) return

        val targetRow = PuzzleEngine.getTargetRow(current.pieces, current.difficulty.rows) ?: return

        autoSolveJob?.cancel()
        timerJob?.cancel()

        _state.update { it.copy(isAutoSolving = true, isComputingSolution = true) }

        autoSolveJob = viewModelScope.launch(Dispatchers.Default) {
            val rowMoves = try {
                val fullSolution = PuzzleEngine.findFullSolution(
                    current.pieces, current.emptyCol, current.emptyRow, current.difficulty
                )
                trimToRowComplete(fullSolution, current.pieces, current.emptyCol, current.emptyRow, targetRow)
            } catch (e: Exception) {
                emptyList()
            }

            withContext(Dispatchers.Main) {
                _state.update { it.copy(isComputingSolution = false) }
            }

            for (pieceId in rowMoves) {
                if (!_state.value.isAutoSolving) break
                withContext(Dispatchers.Main) { onPieceTapped(pieceId) }
                delay(420)
            }

            withContext(Dispatchers.Main) {
                _state.update { it.copy(isAutoSolving = false, isComputingSolution = false) }
            }
        }
    }

    fun onStopFixRow() {
        autoSolveJob?.cancel()
        _state.update { it.copy(isAutoSolving = false, isComputingSolution = false) }
        if (_state.value.isTimedMode && !_state.value.isComplete) startTimer()
    }

    /**
     * From the full solution, return only the prefix of moves needed
     * to get [targetRow] fully into its correct positions.
     */
    private fun trimToRowComplete(
        solution: List<Int>,
        initialPieces: List<PuzzlePiece>,
        initialEmptyCol: Int,
        initialEmptyRow: Int,
        targetRow: Int
    ): List<Int> {
        var simPieces = initialPieces.toMutableList()
        var simEmptyCol = initialEmptyCol
        var simEmptyRow = initialEmptyRow

        for ((index, pieceId) in solution.withIndex()) {
            val piece = simPieces.find { it.id == pieceId } ?: break
            val oldCol = piece.currentCol
            val oldRow = piece.currentRow
            simPieces = simPieces.map {
                if (it.id == pieceId) it.copy(currentCol = simEmptyCol, currentRow = simEmptyRow) else it
            }.toMutableList()
            simEmptyCol = oldCol
            simEmptyRow = oldRow

            val rowDone = simPieces
                .filter { it.correctRow == targetRow }
                .all { it.currentCol == it.correctCol && it.currentRow == it.correctRow }
            if (rowDone) return solution.subList(0, index + 1)
        }
        return solution
    }

    private fun saveResult(
        puzzleId: Int,
        difficulty: Difficulty,
        isTimedMode: Boolean,
        completionTimeMs: Long,
        score: Int,
        stars: Int
    ) {
        viewModelScope.launch {
            dao.insertResult(
                PuzzleResult(
                    puzzleId = puzzleId,
                    difficulty = difficulty.name,
                    isTimedMode = isTimedMode,
                    completionTimeMs = completionTimeMs,
                    score = score,
                    stars = stars
                )
            )
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(100)
                val elapsed = System.currentTimeMillis() - startTime
                val current = _state.value

                if (current.isComplete) break

                if (current.isTimedMode && elapsed >= current.targetTimeMs) {
                    _state.update {
                        it.copy(
                            timeElapsedMs = elapsed,
                            isComplete = true,
                            finalScore = 0,
                            stars = 0
                        )
                    }
                    break
                }

                _state.update { it.copy(timeElapsedMs = elapsed) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        autoSolveJob?.cancel()
    }
}
