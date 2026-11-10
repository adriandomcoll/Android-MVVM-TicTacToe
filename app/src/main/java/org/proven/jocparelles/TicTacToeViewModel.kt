package org.proven.jocparelles

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel encargado de la gestión del estado reactivo del juego Tres en Raya (Tic-Tac-Toe).
 * Hereda de la clase ViewModel de Android para sobrevivir a cambios de configuración.
 */
class TicTacToeViewModel : ViewModel() {

    // MutableStateFlow privado: Almacena el estado mutable del tablero (encapsulación)
    private val _boardState = MutableStateFlow(BoardModel())

    // StateFlow público de solo lectura: Expone el estado a la UI mediante asStateFlow()
    val boardState: StateFlow<BoardModel> = _boardState.asStateFlow()

    /**
     * Procesa el evento de pulsación en una casilla del tablero.
     * Llama al método makeMove() del modelo inmutable y actualiza el valor del MutableStateFlow.
     *
     * @param position Índice de la casilla seleccionada (0 a 8).
     */
    fun onCellClick(position: Int) {
        _boardState.value = _boardState.value.makeMove(position)
    }

    /**
     * Reinicia el juego restableciendo el estado a una nueva instancia inicial de BoardModel.
     */
    fun restartGame() {
        _boardState.value = BoardModel()
    }
}
