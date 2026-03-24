package app.krafted.towerjigsaw.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.towerjigsaw.data.db.AppDatabase
import app.krafted.towerjigsaw.data.db.PuzzleDao
import app.krafted.towerjigsaw.game.Difficulty
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: PuzzleDao = AppDatabase.getInstance(application).puzzleDao()

    private val _isTimedMode = MutableStateFlow(false)
    val isTimedMode: StateFlow<Boolean> = _isTimedMode

    val completedKeys: StateFlow<Set<String>> = dao.getAllCompletedKeys()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun toggleMode() {
        _isTimedMode.value = !_isTimedMode.value
    }

    fun isUnlocked(puzzleId: Int, difficulty: Difficulty): Boolean {
        return when (difficulty) {
            Difficulty.EASY -> true
            Difficulty.MEDIUM -> completedKeys.value.contains("${puzzleId}_EASY")
            Difficulty.HARD -> completedKeys.value.contains("${puzzleId}_MEDIUM")
        }
    }

    fun isCompleted(puzzleId: Int, difficulty: Difficulty): Boolean {
        return completedKeys.value.contains("${puzzleId}_${difficulty.name}")
    }
}
