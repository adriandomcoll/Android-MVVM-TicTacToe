package org.proven.jocparelles

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel per al Joc de les Parelles (Cubiletes).
 */
class ParellesViewModel : ViewModel() {

    private val model = ParellesModel()

    var cards by mutableStateOf(model.cards)
        private set

    var isGameOver by mutableStateOf(model.isGameOver)
        private set

    var isProcessing by mutableStateOf(false)
        private set

    private fun updateUiState() {
        cards = model.cards
        isGameOver = model.isGameOver
    }

    fun onCardClick(cardId: Int) {
        if (isProcessing) return

        val flipped = model.selectCard(cardId)
        if (!flipped) return

        updateUiState()

        val faceUpCards = model.getUnmatchedFaceUpCards()
        if (faceUpCards.size == 2) {
            isProcessing = true
            val isMatch = model.checkMatch()
            updateUiState()

            if (!isMatch) {
                viewModelScope.launch {
                    delay(1000L)
                    model.flipUnmatchedFaceDown()
                    updateUiState()
                    isProcessing = false
                }
            } else {
                isProcessing = false
            }
        }
    }

    fun onRestartClick() {
        model.resetGame()
        isProcessing = false
        updateUiState()
    }
}
