package org.proven.jocparelles

/**
 * Data class per a la carta del Joc de les Parelles.
 */
data class CardModel(
    val id: Int,
    val imageResId: Int,
    val isFaceUp: Boolean = false,
    val isMatched: Boolean = false
)

/**
 * Model per al Joc de les Parelles.
 */
class ParellesModel {

    private val availableImages = listOf(
        R.drawable.isabeau,
        R.drawable.mirabelle,
        R.drawable.odile,
        R.drawable.siffrin
    )

    var cards: List<CardModel> = emptyList()
        private set

    var isGameOver: Boolean = false
        private set

    init {
        resetGame()
    }

    fun resetGame() {
        val selectedImages = availableImages.shuffled().take(3)
        val shuffledPairs = (selectedImages + selectedImages).shuffled()

        cards = shuffledPairs.mapIndexed { index, imgRes ->
            CardModel(
                id = index,
                imageResId = imgRes,
                isFaceUp = false,
                isMatched = false
            )
        }
        isGameOver = false
    }

    fun selectCard(cardId: Int): Boolean {
        val card = cards.find { it.id == cardId } ?: return false

        if (card.isMatched || card.isFaceUp) return false

        val faceUpUnmatchedCount = cards.count { it.isFaceUp && !it.isMatched }

        if (faceUpUnmatchedCount < 2) {
            cards = cards.map {
                if (it.id == cardId) it.copy(isFaceUp = true) else it
            }
            return true
        }
        return false
    }

    fun getUnmatchedFaceUpCards(): List<CardModel> {
        return cards.filter { it.isFaceUp && !it.isMatched }
    }

    fun checkMatch(): Boolean {
        val faceUpUnmatched = getUnmatchedFaceUpCards()
        if (faceUpUnmatched.size == 2) {
            val (c1, c2) = faceUpUnmatched
            if (c1.imageResId == c2.imageResId) {
                cards = cards.map {
                    if ((it.id == c1.id) || (it.id == c2.id)) {
                        it.copy(isMatched = true)
                    } else it
                }
                if (cards.all { it.isMatched }) {
                    isGameOver = true
                }
                return true
            }
        }
        return false
    }

    fun flipUnmatchedFaceDown() {
        cards = cards.map {
            if (it.isFaceUp && !it.isMatched) {
                it.copy(isFaceUp = false)
            } else it
        }
    }
}
