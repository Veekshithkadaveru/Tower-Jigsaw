package app.krafted.towerjigsaw.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.towerjigsaw.data.db.AppDatabase
import app.krafted.towerjigsaw.data.db.PuzzleDao
import app.krafted.towerjigsaw.data.db.PuzzleResult
import app.krafted.towerjigsaw.game.Difficulty
import app.krafted.towerjigsaw.game.PuzzleEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PuzzleViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: PuzzleDao = AppDatabase.getInstance(application).puzzleDao()

    private val _state = MutableStateFlow(PuzzleUiState())
    val state: StateFlow<PuzzleUiState> = _state.asStateFlow()

    private var startTime: Long = 0L
    private var timerJob: Job? = null

    var boardOffsetX: Float = 0f
        private set
    var boardOffsetY: Float = 0f
        private set
    var pieceWidth: Float = 0f
        private set
    var pieceHeight: Float = 0f
        private set

    fun startPuzzle(puzzleId: Int, difficulty: Difficulty, isTimedMode: Boolean) {
        timerJob?.cancel()

        val trayEndX = pieceWidth * difficulty.cols
        val boardHeight = pieceHeight * difficulty.rows + boardOffsetY
        val pieces = PuzzleEngine.generatePieces(
            difficulty = difficulty,
            trayStartX = 0f,
            trayEndX = if (trayEndX > 0f) trayEndX else 300f,
            trayStartY = if (boardHeight > 0f) boardHeight + 20f else 500f,
            trayEndY = if (boardHeight > 0f) boardHeight + 200f else 700f
        )

        _state.value = PuzzleUiState(
            puzzleId = puzzleId,
            difficulty = difficulty,
            pieces = pieces,
            totalPieces = difficulty.totalPieces,
            isTimedMode = isTimedMode,
            targetTimeMs = difficulty.targetTimeMs
        )

        startTime = System.currentTimeMillis()

        if (isTimedMode) {
            startTimer()
        }
    }

    fun setBoardDimensions(offsetX: Float, offsetY: Float, pWidth: Float, pHeight: Float) {
        boardOffsetX = offsetX
        boardOffsetY = offsetY
        pieceWidth = pWidth
        pieceHeight = pHeight

        val current = _state.value
        if (current.pieces.isNotEmpty()) {
            val unplacedExist = current.pieces.any { !it.isPlaced }
            if (unplacedExist) {
                val boardHeight = offsetY + pHeight * current.difficulty.rows
                val trayEndX = offsetX + pWidth * current.difficulty.cols
                val newPieces = current.pieces.map { piece ->
                    if (piece.isPlaced) piece
                    else piece.copy(
                        currentX = kotlin.random.Random.nextFloat() * (trayEndX - offsetX) + offsetX,
                        currentY = kotlin.random.Random.nextFloat() * 180f + boardHeight + 20f
                    )
                }
                _state.update { it.copy(pieces = newPieces) }
            }
        }
    }

    fun onPiecePicked(pieceId: Int) {
        _state.update { it.copy(activePieceId = pieceId) }
    }

    fun onPieceMoved(pieceId: Int, dx: Float, dy: Float) {
        _state.update { current ->
            val newPieces = current.pieces.map { piece ->
                if (piece.id == pieceId && !piece.isPlaced) {
                    piece.copy(currentX = piece.currentX + dx, currentY = piece.currentY + dy)
                } else {
                    piece
                }
            }
            current.copy(pieces = newPieces)
        }
    }

    fun onPieceDropped(pieceId: Int) {
        val current = _state.value
        val piece = current.pieces.find { it.id == pieceId } ?: return

        val snapResult = PuzzleEngine.checkSnap(
            piece = piece,
            pieceWidth = pieceWidth,
            pieceHeight = pieceHeight,
            boardOffsetX = boardOffsetX,
            boardOffsetY = boardOffsetY
        )

        if (snapResult != null) {
            val (snapX, snapY) = snapResult
            val newPieces = current.pieces.map {
                if (it.id == pieceId) it.copy(currentX = snapX, currentY = snapY, isPlaced = true)
                else it
            }
            val newPlacedCount = current.placedCount + 1
            val isComplete = PuzzleEngine.isPuzzleComplete(newPieces)

            if (isComplete) {
                timerJob?.cancel()
                val elapsed = System.currentTimeMillis() - startTime
                val score = PuzzleEngine.calculateScore(current.difficulty, elapsed)
                val stars = PuzzleEngine.calculateStars(current.isTimedMode, current.difficulty, elapsed)
                _state.value = current.copy(
                    pieces = newPieces,
                    placedCount = newPlacedCount,
                    isComplete = true,
                    timeElapsedMs = elapsed,
                    finalScore = score,
                    stars = stars,
                    activePieceId = null
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
                _state.update {
                    it.copy(
                        pieces = newPieces,
                        placedCount = newPlacedCount,
                        activePieceId = null
                    )
                }
            }
        } else {
            _state.update { it.copy(activePieceId = null) }
        }
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
    }
}
