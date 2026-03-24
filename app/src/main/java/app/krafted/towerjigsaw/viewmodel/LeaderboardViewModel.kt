package app.krafted.towerjigsaw.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.towerjigsaw.data.db.AppDatabase
import app.krafted.towerjigsaw.data.db.PuzzleDao
import app.krafted.towerjigsaw.data.db.PuzzleResult
import app.krafted.towerjigsaw.game.Difficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: PuzzleDao = AppDatabase.getInstance(application).puzzleDao()

    val selectedPuzzleId: MutableStateFlow<Int> = MutableStateFlow(1)
    val selectedDifficulty: MutableStateFlow<Difficulty> = MutableStateFlow(Difficulty.EASY)

    val scores: StateFlow<List<PuzzleResult>> = combine(selectedPuzzleId, selectedDifficulty) { puzzleId, difficulty ->
        Pair(puzzleId, difficulty)
    }.flatMapLatest { (puzzleId, difficulty) ->
        dao.getTopScores(puzzleId, difficulty.name, 10)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectPuzzle(puzzleId: Int) {
        selectedPuzzleId.value = puzzleId
    }

    fun selectDifficulty(difficulty: Difficulty) {
        selectedDifficulty.value = difficulty
    }
}
