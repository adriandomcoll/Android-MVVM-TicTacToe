package org.proven.jocparelles

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar el estado reactivo y la lógica del juego de Parelles (Cubiletes).
 * Hereda de ViewModel de Android para mantener el estado durante cambios de configuración.
 */
class ParellesViewModel : ViewModel() {

    // Modelo que almacena los datos y la lógica del juego de parejas
    private val model = ParellesModel()

    // Estado reactivo expuesto a Compose con mutableStateOf y el delegado 'by'
    var cards by mutableStateOf(model.cards)
        private set

    var isGameOver by mutableStateOf(model.isGameOver)
        private set

    // Estado para bloquear clics durante el retardo de 1 segundo
    var isProcessing by mutableStateOf(false)
        private set

    /**
     * Sincroniza las propiedades reactivas del ViewModel con el modelo subyacente.
     */
    private fun updateUiState() {
        cards = model.cards
        isGameOver = model.isGameOver
    }

    /**
     * Procesa el clic en una carta. Si hay 2 cartas destapadas que no coinciden,
     * inicia una corrutina en viewModelScope para pausar 1 segundo con delay(1000L).
     *
     * @param cardId Identificador de la carta pulsada.
     */
    fun onCardClick(cardId: Int) {
        // Bloqueamos interacciones si estamos procesando un retardo
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
                // Si no coinciden, ejecutamos la suspensión de 1000ms en el alcance de corrutina del ViewModel
                viewModelScope.launch {
                    delay(1000L) // Retardo sin bloquear el hilo principal de la UI
                    model.flipUnmatchedFaceDown()
                    updateUiState()
                    isProcessing = false
                }
            } else {
                isProcessing = false
            }
        }
    }

    /**
     * Reinicia el juego restableciendo el modelo y el estado visual.
     */
    fun onRestartClick() {
        model.resetGame()
        isProcessing = false
        updateUiState()
    }
}
