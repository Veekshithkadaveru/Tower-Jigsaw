package app.krafted.towerjigsaw.game

import kotlin.math.abs

data class PuzzleGenerationResult(
    val pieces: List<PuzzlePiece>,
    val emptyCol: Int,
    val emptyRow: Int
)

object PuzzleEngine {

    /**
     * Generates a shuffled, 100% mathematically solvable sliding puzzle board.
     * @param cols Number of columns (e.g., 2 for your Easy level)
     * @param rows Number of rows (e.g., 3 for your Easy level)
     * @param shuffleSteps Number of random slides to perform (100 is usually plenty)
     * @return An IntArray representing the shuffled board, where (cols * rows - 1) is the empty space.
     */
    private fun generateSolvableBoard(cols: Int, rows: Int, shuffleSteps: Int = 100): IntArray {
        val totalTiles = cols * rows
        val board = IntArray(totalTiles) { it }
        
        var emptyIndex = totalTiles - 1 
        var previousEmptyIndex = -1 
    
        for (step in 0 until shuffleSteps) {
            val validMoves = mutableListOf<Int>()
            val emptyRow = emptyIndex / cols
            val emptyCol = emptyIndex % cols
    
            if (emptyRow > 0) validMoves.add(emptyIndex - cols)
            if (emptyRow < rows - 1) validMoves.add(emptyIndex + cols)
            if (emptyCol > 0) validMoves.add(emptyIndex - 1)
            if (emptyCol < cols - 1) validMoves.add(emptyIndex + 1)
    
            validMoves.remove(previousEmptyIndex)
    
            val moveToMake = validMoves.random()
            
            board[emptyIndex] = board[moveToMake]
            board[moveToMake] = totalTiles - 1
            
            previousEmptyIndex = emptyIndex
            emptyIndex = moveToMake
        }
    
        return board
    }

    fun generatePieces(difficulty: Difficulty): PuzzleGenerationResult {
        val cols = difficulty.cols
        val rows = difficulty.rows
        
        val boardArray = generateSolvableBoard(cols, rows, 100)
        
        val pieces = mutableListOf<PuzzlePiece>()
        var emptyCol = -1
        var emptyRow = -1
        
        for (i in boardArray.indices) {
            val pieceId = boardArray[i]
            val currentCol = i % cols
            val currentRow = i / cols
            
            if (pieceId == (cols * rows - 1)) {
                emptyCol = currentCol
                emptyRow = currentRow
                continue
            }
            
            val correctCol = pieceId % cols
            val correctRow = pieceId / cols
            
            pieces.add(
                PuzzlePiece(
                    id = pieceId,
                    correctCol = correctCol,
                    correctRow = correctRow,
                    currentCol = currentCol,
                    currentRow = currentRow
                )
            )
        }

        return PuzzleGenerationResult(pieces, emptyCol, emptyRow)
    }

    fun isPuzzleComplete(pieces: List<PuzzlePiece>): Boolean {
        return pieces.all { it.currentCol == it.correctCol && it.currentRow == it.correctRow }
    }

    fun calculateScore(
        difficulty: Difficulty,
        elapsedTimeMs: Long
    ): Int {
        val remainingMs = (difficulty.targetTimeMs - elapsedTimeMs).coerceAtLeast(0)
        val remainingSeconds = (remainingMs / 1000).toInt()
        return difficulty.basePoints + (remainingSeconds * 10)
    }

    fun calculateStars(
        isTimedMode: Boolean,
        difficulty: Difficulty,
        elapsedTimeMs: Long
    ): Int {
        if (!isTimedMode) return 3
        return when {
            elapsedTimeMs <= difficulty.targetTimeMs / 2 -> 3
            elapsedTimeMs <= difficulty.targetTimeMs -> 2
            else -> 1
        }
    }

    fun formatTime(timeMs: Long): String {
        val totalSeconds = (timeMs / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }


    /** Returns the topmost row index that still has misplaced pieces, or null if all complete. */
    fun getTargetRow(pieces: List<PuzzlePiece>, rows: Int): Int? {
        for (row in 0 until rows) {
            if (pieces.filter { it.correctRow == row }.any { it.currentCol != it.correctCol || it.currentRow != it.correctRow }) {
                return row
            }
        }
        return null
    }

    /** Returns the full ordered list of piece IDs to tap to solve the puzzle. */
    fun findFullSolution(
        pieces: List<PuzzlePiece>,
        emptyCol: Int,
        emptyRow: Int,
        difficulty: Difficulty,
        timeBudgetMs: Long = 5000L
    ): List<Int> {
        val cols = difficulty.cols
        val rows = difficulty.rows
        val board = boardFromPieces(pieces, emptyCol, emptyRow, cols, rows)
        val originalBoard = board.copyOf()
        val emptyIndex = emptyRow * cols + emptyCol

        val moveIndices = if (difficulty == Difficulty.EASY) {
            solveBFSFull(board, emptyIndex, cols, rows)
        } else {
            solveIDAFull(board, emptyIndex, cols, rows, timeBudgetMs)
                ?: greedyFullSolution(originalBoard, emptyIndex, cols, rows)
        }

        // Simulate moves on original board to extract piece IDs in order
        val simBoard = originalBoard.copyOf()
        var simEmpty = emptyIndex
        return moveIndices.map { moveIdx ->
            val pieceId = simBoard[moveIdx]
            simBoard[simEmpty] = pieceId
            simBoard[moveIdx] = cols * rows - 1
            simEmpty = moveIdx
            pieceId
        }
    }

    private fun boardFromPieces(
        pieces: List<PuzzlePiece>,
        emptyCol: Int,
        emptyRow: Int,
        cols: Int,
        rows: Int
    ): IntArray {
        val total = cols * rows
        val board = IntArray(total) { -1 }
        val emptyId = total - 1
        board[emptyRow * cols + emptyCol] = emptyId
        for (p in pieces) {
            board[p.currentRow * cols + p.currentCol] = p.id
        }
        return board
    }

    private fun manhattanDistance(board: IntArray, cols: Int, rows: Int): Int {
        val emptyId = cols * rows - 1
        var dist = 0
        for (i in board.indices) {
            val id = board[i]
            if (id == emptyId) continue
            val correctCol = id % cols
            val correctRow = id / cols
            val currentCol = i % cols
            val currentRow = i / cols
            dist += abs(correctCol - currentCol) + abs(correctRow - currentRow)
        }
        return dist
    }

    private fun neighbors(index: Int, cols: Int, rows: Int): List<Int> {
        val r = index / cols
        val c = index % cols
        val result = mutableListOf<Int>()
        if (r > 0) result.add(index - cols)
        if (r < rows - 1) result.add(index + cols)
        if (c > 0) result.add(index - 1)
        if (c < cols - 1) result.add(index + 1)
        return result
    }

    private fun solveBFSFull(board: IntArray, emptyIndex: Int, cols: Int, rows: Int): List<Int> {
        if (manhattanDistance(board, cols, rows) == 0) return emptyList()

        data class State(val board: IntArray, val emptyIdx: Int, val path: List<Int>) {
            fun key(): Long {
                var h = 0L
                for (v in board) h = h * 31 + v
                return h
            }
        }

        val visited = HashSet<Long>()
        val queue = ArrayDeque<State>()
        val initial = State(board.copyOf(), emptyIndex, emptyList())
        visited.add(initial.key())
        queue.add(initial)

        while (queue.isNotEmpty()) {
            val cur = queue.removeFirst()
            for (n in neighbors(cur.emptyIdx, cols, rows)) {
                val newBoard = cur.board.copyOf()
                newBoard[cur.emptyIdx] = newBoard[n]
                newBoard[n] = cols * rows - 1
                val newPath = cur.path + n
                val next = State(newBoard, n, newPath)
                if (!visited.add(next.key())) continue
                if (manhattanDistance(newBoard, cols, rows) == 0) return newPath
                queue.add(next)
            }
        }
        return emptyList()
    }

    private fun solveIDAFull(
        board: IntArray,
        emptyIndex: Int,
        cols: Int,
        rows: Int,
        timeBudgetMs: Long = 5000L
    ): List<Int>? {
        if (manhattanDistance(board, cols, rows) == 0) return emptyList()

        val deadline = System.currentTimeMillis() + timeBudgetMs
        var timedOut = false
        val path = mutableListOf<Int>()

        fun search(emptyIdx: Int, g: Int, threshold: Int, prevEmpty: Int): Int {
            if (System.currentTimeMillis() > deadline) { timedOut = true; return Int.MAX_VALUE }
            val f = g + manhattanDistance(board, cols, rows)
            if (f > threshold) return f
            if (f - g == 0) return -1

            var minT = Int.MAX_VALUE
            for (n in neighbors(emptyIdx, cols, rows)) {
                if (n == prevEmpty) continue
                val tileId = board[n]
                board[emptyIdx] = tileId
                board[n] = cols * rows - 1
                path.add(n)

                val result = search(n, g + 1, threshold, emptyIdx)

                if (result == -1) return -1
                if (timedOut) return Int.MAX_VALUE

                board[n] = tileId
                board[emptyIdx] = cols * rows - 1
                path.removeLastOrNull()

                if (result < minT) minT = result
            }
            return minT
        }

        var threshold = manhattanDistance(board, cols, rows)
        while (threshold < 200) {
            path.clear()
            val result = search(emptyIndex, 0, threshold, -1)
            if (timedOut) return null
            if (result == -1) return path.toList()
            if (result == Int.MAX_VALUE) return null
            threshold = result
        }
        return null
    }

    private fun greedyFullSolution(
        board: IntArray,
        emptyIndex: Int,
        cols: Int,
        rows: Int,
        maxSteps: Int = 500
    ): List<Int> {
        val result = mutableListOf<Int>()
        val simBoard = board.copyOf()
        var simEmpty = emptyIndex
        var prevEmpty = -1

        repeat(maxSteps) {
            if (manhattanDistance(simBoard, cols, rows) == 0) return result
            var bestMove: Int? = null
            var bestDist = Int.MAX_VALUE
            for (n in neighbors(simEmpty, cols, rows)) {
                if (n == prevEmpty) continue
                val tileId = simBoard[n]
                simBoard[simEmpty] = tileId
                simBoard[n] = cols * rows - 1
                val d = manhattanDistance(simBoard, cols, rows)
                simBoard[n] = tileId
                simBoard[simEmpty] = cols * rows - 1
                if (d < bestDist) { bestDist = d; bestMove = n }
            }
            val move = bestMove ?: neighbors(simEmpty, cols, rows).firstOrNull { it != prevEmpty }
                ?: return result
            prevEmpty = simEmpty
            simBoard[simEmpty] = simBoard[move]
            simBoard[move] = cols * rows - 1
            result.add(move)
            simEmpty = move
        }
        return result
    }
}
