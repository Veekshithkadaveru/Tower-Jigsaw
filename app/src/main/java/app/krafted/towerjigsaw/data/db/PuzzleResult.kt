package app.krafted.towerjigsaw.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "puzzle_results")
data class PuzzleResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val puzzleId: Int,
    val difficulty: String,
    val isTimedMode: Boolean,
    val completionTimeMs: Long,
    val score: Int,
    val stars: Int,
    val completedAt: Long = System.currentTimeMillis()
)
