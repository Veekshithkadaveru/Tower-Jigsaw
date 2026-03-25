package app.krafted.towerjigsaw.game

import androidx.compose.ui.graphics.Color

data class PuzzleInfo(
    val id: Int,
    val name: String,
    val imageResName: String,
    val accentColor: Color
)

object Puzzles {
    val all: List<PuzzleInfo> = listOf(
        PuzzleInfo(1, "Timber Cottage", "tower_sym_1", Color(0xFF795548)),
        PuzzleInfo(2, "Crane Market", "tower_sym_2", Color(0xFF546E7A)),
        PuzzleInfo(3, "Green Manor", "tower_sym_3", Color(0xFF4CAF50)),
        PuzzleInfo(4, "Market Stall", "tower_sym_4", Color(0xFFF57F17)),
        PuzzleInfo(5, "Red Dwelling", "tower_sym_5", Color(0xFF6A1B9A)),
        PuzzleInfo(6, "Yellow Villa", "tower_sym_6", Color(0xFFBF360C)),
        PuzzleInfo(7, "Blue Cottage", "tower_sym_7", Color(0xFF1565C0))
    )

    fun getById(id: Int): PuzzleInfo = all.first { it.id == id }
}
