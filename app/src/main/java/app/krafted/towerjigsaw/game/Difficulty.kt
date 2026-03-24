package app.krafted.towerjigsaw.game

enum class Difficulty(
    val cols: Int,
    val rows: Int,
    val targetTimeMs: Long,
    val basePoints: Int,
    val displayName: String
) {
    EASY(cols = 2, rows = 3, targetTimeMs = 120_000L, basePoints = 100, displayName = "Easy"),
    MEDIUM(cols = 3, rows = 4, targetTimeMs = 240_000L, basePoints = 250, displayName = "Medium"),
    HARD(cols = 4, rows = 5, targetTimeMs = 420_000L, basePoints = 500, displayName = "Hard");

    val totalPieces: Int get() = cols * rows
}
