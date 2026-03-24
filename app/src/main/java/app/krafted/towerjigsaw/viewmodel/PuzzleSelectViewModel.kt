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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PuzzleSelectViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: PuzzleDao = AppDatabase.getInstance(application).puzzleDao()

    val completedKeys: StateFlow<Set<String>> = dao.getAllCompletedKeys()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private val _bestResults = MutableStateFlow<Map<String, PuzzleResult?>>(emptyMap())
    val bestResults: StateFlow<Map<String, PuzzleResult?>> = _bestResults

    fun loadBestResults(puzzleId: Int) {
        Difficulty.entries.forEach { difficulty ->
            viewModelScope.launch {
                dao.getBestResult(puzzleId, difficulty.name).collect { result ->
                    _bestResults.value = _bestResults.value + (difficulty.name to result)
                }
            }
        }
    }

    fun isUnlocked(puzzleId: Int, difficulty: Difficulty, completedKeys: Set<String>): Boolean {
        return when (difficulty) {
            Difficulty.EASY -> true
            Difficulty.MEDIUM -> completedKeys.contains("${puzzleId}_${Difficulty.EASY.name}")
            Difficulty.HARD -> completedKeys.contains("${puzzleId}_${Difficulty.MEDIUM.name}")
        }
    }
}
