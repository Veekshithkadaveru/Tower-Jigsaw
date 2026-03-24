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
        PuzzleInfo(1, "Stone Turret", "tower_sym_1", Color(0xFF795548)),
        PuzzleInfo(2, "Castle Gate", "tower_sym_2", Color(0xFF546E7A)),
        PuzzleInfo(3, "Watch Tower", "tower_sym_3", Color(0xFF4CAF50)),
        PuzzleInfo(4, "Cannon Tower", "tower_sym_4", Color(0xFFF57F17)),
        PuzzleInfo(5, "Royal Keep", "tower_sym_5", Color(0xFF6A1B9A)),
        PuzzleInfo(6, "Fortress Wall", "tower_sym_6", Color(0xFFBF360C)),
        PuzzleInfo(7, "War Catapult", "tower_sym_7", Color(0xFF1565C0))
    )

    fun getById(id: Int): PuzzleInfo = all.first { it.id == id }
}
