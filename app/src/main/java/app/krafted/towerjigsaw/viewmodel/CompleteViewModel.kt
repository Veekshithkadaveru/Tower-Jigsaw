package app.krafted.towerjigsaw.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.towerjigsaw.data.db.AppDatabase
import app.krafted.towerjigsaw.data.db.PuzzleResult
import kotlinx.coroutines.launch

class CompleteViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).puzzleDao()

    fun saveResult(
        puzzleId: Int,
        difficulty: String,
        isTimedMode: Boolean,
        completionTimeMs: Long,
        score: Int,
        stars: Int,
        playerName: String
    ) {
        viewModelScope.launch {
            dao.insertResult(
                PuzzleResult(
                    puzzleId = puzzleId,
                    difficulty = difficulty,
                    isTimedMode = isTimedMode,
                    completionTimeMs = completionTimeMs,
                    score = score,
                    stars = stars,
                    playerName = playerName.trim().ifEmpty { "Player" }
                )
            )
        }
    }
}
