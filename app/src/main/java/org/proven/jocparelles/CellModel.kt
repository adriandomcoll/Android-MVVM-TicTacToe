package org.proven.jocparelles

/**
 * Data class que representa una casilla individual del tablero de Tres en Raya.
 * Es inmutable para garantizar la reactividad del estado.
 *
 * @property id Identificador único de la casilla.
 * @property position Índice numérico de la posición en el tablero (0 a 8).
 * @property symbol Símbolo actual contenido en la casilla ("X", "O" o null si está vacía).
 */
data class CellModel(
    val id: Int,
    val position: Int,
    val symbol: String? = null
)
