package org.proven.jocparelles

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel per a la gestió de l'estat reactiu del Tres en Ratlla.
 */
class GameViewModel : ViewModel() {

    private val _boardState = MutableStateFlow(BoardModel())
    val boardState: StateFlow<BoardModel> = _boardState.asStateFlow()

    fun onCellClick(position: Int) {
        _boardState.value = _boardState.value.makeMove(position)
    }

    fun restartGame() {
        _boardState.value = BoardModel()
    }
}
