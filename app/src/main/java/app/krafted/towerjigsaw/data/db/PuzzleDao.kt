package app.krafted.towerjigsaw.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PuzzleDao {
    @Insert
    suspend fun insertResult(result: PuzzleResult)

    @Query("SELECT * FROM puzzle_results WHERE puzzleId = :puzzleId AND difficulty = :difficulty AND isTimedMode = 1 ORDER BY score DESC LIMIT :limit")
    fun getTopScores(puzzleId: Int, difficulty: String, limit: Int = 10): Flow<List<PuzzleResult>>

    @Query("SELECT EXISTS(SELECT 1 FROM puzzle_results WHERE puzzleId = :puzzleId AND difficulty = :difficulty LIMIT 1)")
    fun isCompleted(puzzleId: Int, difficulty: String): Flow<Boolean>

    @Query("SELECT * FROM puzzle_results WHERE puzzleId = :puzzleId AND difficulty = :difficulty ORDER BY score DESC LIMIT 1")
    fun getBestResult(puzzleId: Int, difficulty: String): Flow<PuzzleResult?>

    @Query("SELECT * FROM puzzle_results WHERE puzzleId = :puzzleId ORDER BY completedAt DESC")
    fun getResultsForPuzzle(puzzleId: Int): Flow<List<PuzzleResult>>

    @Query("SELECT DISTINCT puzzleId || '_' || difficulty FROM puzzle_results")
    fun getAllCompletedKeys(): Flow<List<String>>
}
