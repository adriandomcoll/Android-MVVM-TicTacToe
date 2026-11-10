package org.proven.jocparelles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Pantalla principal del juego Tres en Raya en Jetpack Compose.
 *
 * @param onBackToMenu Callback para volver al menú principal.
 * @param viewModel Instancia del ViewModel obtenida mediante viewModel().
 */
@Composable
fun TicTacToeScreen(
    onBackToMenu: () -> Unit,
    viewModel: TicTacToeViewModel = viewModel()
) {
    // Subscripción reactiva al StateFlow del ViewModel mediante collectAsState()
    val boardState by viewModel.boardState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Fila superior: Botón de volver al menú y Título
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onBackToMenu,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("← Menú")
                }

                Text(
                    text = "Tres en Ratlla",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Indicador dinámico de estado: Muestra el turno actual o el mensaje de fin de juego (victoria/empate)
            val statusMessage = when {
                boardState.isGameOver && boardState.winner == "DRAW" -> "Empat! No hi ha guanyador."
                boardState.isGameOver && boardState.winner != null -> "Ha guanyat el jugador ${boardState.winner}!"
                else -> "Torn del jugador: ${boardState.currentTurn}"
            }

            val statusColor = when {
                boardState.isGameOver && boardState.winner == "DRAW" -> Color(0xFFFF9800)
                boardState.isGameOver && boardState.winner != null -> Color(0xFF4CAF50)
                boardState.currentTurn == "X" -> Color(0xFF2196F3)
                else -> Color(0xFFE91E63)
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = statusMessage,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            // Componente del tablero 3x3
            BoardGrid(
                cells = boardState.cells,
                onCellClick = { position -> viewModel.onCellClick(position) },
                isGameOver = boardState.isGameOver
            )

            // Botón de reinici del joc
            Button(
                onClick = { viewModel.restartGame() },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .width(180.dp)
                    .height(48.dp)
            ) {
                Text(
                    text = "Reiniciar joc",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Componente que renderiza la cuadrícula de 3x3 casillas utilizando filas y columnas para evitar cuelgues de layout.
 */
@Composable
fun BoardGrid(
    cells: List<CellModel>,
    onCellClick: (Int) -> Unit,
    isGameOver: Boolean,
    modifier: Modifier = Modifier
) {
    if (cells.size >= 9) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Fila 1: Posiciones 0, 1, 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in 0..2) {
                    CellItemView(
                        cell = cells[i],
                        onClick = { onCellClick(cells[i].position) },
                        isGameOver = isGameOver,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Fila 2: Posiciones 3, 4, 5
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in 3..5) {
                    CellItemView(
                        cell = cells[i],
                        onClick = { onCellClick(cells[i].position) },
                        isGameOver = isGameOver,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Fila 3: Posiciones 6, 7, 8
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in 6..8) {
                    CellItemView(
                        cell = cells[i],
                        onClick = { onCellClick(cells[i].position) },
                        isGameOver = isGameOver,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Componente individual que representa una casilla interactiva del tablero.
 *
 * @param cell Instancia del modelo CellModel con los datos de la casilla.
 * @param onClick Evento invocado al hacer clic en la casilla.
 * @param isGameOver Controla si la casilla es interactiva o está deshabilitada por fin de juego.
 */
@Composable
fun CellItemView(
    cell: CellModel,
    onClick: () -> Unit,
    isGameOver: Boolean,
    modifier: Modifier = Modifier
) {
    val symbolColor = when (cell.symbol) {
        "X" -> Color(0xFF2196F3)
        "O" -> Color(0xFFE91E63)
        else -> Color.Transparent
    }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(enabled = !isGameOver && cell.symbol == null) { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cell.symbol ?: "",
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                color = symbolColor
            )
        }
    }
}
