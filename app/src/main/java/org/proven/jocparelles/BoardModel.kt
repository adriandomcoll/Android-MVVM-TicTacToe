package org.proven.jocparelles

/**
 * Modelo de datos inmutable que representa el estado completo del tablero de Tres en Raya
 * y encapsula la lógica de negocio pura del juego.
 *
 * @property cells Lista inmutable de las 9 casillas del tablero.
 * @property currentTurn Turno actual del jugador ("X" u "O").
 * @property winner Ganador del juego ("X", "O", "DRAW" para empate, o null si continúa el juego).
 * @property isGameOver Indica si la partida ha finalizado.
 */
data class BoardModel(
    val cells: List<CellModel> = createInitialBoard(),
    val currentTurn: String = "X",
    val winner: String? = null,
    val isGameOver: Boolean = false
) {
    companion object {
        /**
         * Función de fábrica que inicializa el tablero creando 9 casillas vacías (posiciones 0 a 8).
         */
        fun createInitialBoard(): List<CellModel> {
            return (0..8).map { index ->
                CellModel(
                    id = index,
                    position = index,
                    symbol = null
                )
            }
        }
    }

    /**
     * Procesa la jugada en la posición indicada y retorna una NUEVA instancia inmutable de BoardModel
     * utilizando el método .copy() para notificar cambios de estado a la UI.
     *
     * @param position Índice de la casilla pulsada (0 a 8).
     * @return Nueva copia inmutable del estado del tablero actualizado.
     */
    fun makeMove(position: Int): BoardModel {
        // Si el juego ha terminado o la casilla ya está ocupada, se ignora el movimiento
        if (isGameOver || cells[position].symbol != null) {
            return this
        }

        // Se crea una nueva lista de casillas duplicando y actualizando solo la casilla seleccionada con .copy()
        val updatedCells = cells.map { cell ->
            if (cell.position == position) {
                cell.copy(symbol = currentTurn)
            } else {
                cell
            }
        }

        // Verificamos si este movimiento produce un ganador o empate
        val (newWinner, gameOver) = checkWinner(updatedCells)
        val nextTurn = if (currentTurn == "X") "O" else "X"

        // Devolvemos una nueva instancia inmutable de BoardModel con los cambios
        return copy(
            cells = updatedCells,
            currentTurn = if (gameOver) currentTurn else nextTurn,
            winner = newWinner,
            isGameOver = gameOver
        )
    }

    /**
     * Comprueba si existe alguna combinación ganadora (3 en raya) o si el tablero está lleno (empate).
     *
     * @param currentCells Lista actual de las 9 casillas.
     * @return Pair con el ganador ("X", "O", "DRAW" o null) y un Boolean indicando si el juego terminó.
     */
    private fun checkWinner(currentCells: List<CellModel>): Pair<String?, Boolean> {
        // Las 8 combinaciones ganadoras posibles en un tablero 3x3
        val winningCombinations = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Filas horizontales
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Columnas verticales
            listOf(0, 4, 8), listOf(2, 4, 6)                  // Diagonales
        )

        for (combo in winningCombinations) {
            val (a, b, c) = combo
            val symbolA = currentCells[a].symbol
            val symbolB = currentCells[b].symbol
            val symbolC = currentCells[c].symbol

            // Si las tres casillas de la combinación tienen el mismo símbolo (diferente de null), hay ganador
            if (symbolA != null && symbolA == symbolB && symbolA == symbolC) {
                return Pair(symbolA, true)
            }
        }

        // Si no hay ganador y todas las casillas están ocupadas, se declara empate ("DRAW")
        val isBoardFull = currentCells.all { it.symbol != null }
        if (isBoardFull) {
            return Pair("DRAW", true)
        }

        // El juego continúa
        return Pair(null, false)
    }
}
